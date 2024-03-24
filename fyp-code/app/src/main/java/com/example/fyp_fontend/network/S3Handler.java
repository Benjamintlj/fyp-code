package com.example.fyp_fontend.network;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.amazonaws.HttpMethod;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.example.fyp_fontend.model.Question.MatchingPairs;
import com.example.fyp_fontend.model.Question.MultipleChoice;
import com.example.fyp_fontend.model.Question.Reorder;
import com.example.fyp_fontend.model.content_selection.SpacedRepetition;
import com.example.fyp_fontend.network.callback.SpacedRepetitionDataCallback;
import com.example.fyp_fontend.utils.Globals;
import com.example.fyp_fontend.model.FeedItemModel;
import com.example.fyp_fontend.model.Question.Acknowledge;
import com.example.fyp_fontend.model.Question.SingleWord;
import com.example.fyp_fontend.model.content_selection.LessonModel;
import com.example.fyp_fontend.model.Question.Question;
import com.example.fyp_fontend.model.content_selection.SubtopicModel;
import com.example.fyp_fontend.model.content_selection.TopicModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class S3Handler {
    private static final String TAG = "S3Handler";
    private static S3Handler s3Handler;
    private final AmazonS3Client s3Client;
    private final TransferUtility transferUtility;
    private Context context;

    public S3Handler(Context context) {
        s3Client = new AmazonS3Client(Globals.getAwsCredentials());
        s3Client.setRegion(Region.getRegion(Regions.EU_WEST_1));

        this.context = context.getApplicationContext();

        this.transferUtility = TransferUtility.builder()
                .context(context.getApplicationContext())
                .s3Client(s3Client)
                .build();
    }

    public static synchronized S3Handler getInstance(Context context) {
        if (s3Handler == null) {
            s3Handler = new S3Handler(context);
        }
        return s3Handler;
    }

    public List<TopicModel> listS3DirectoryStructure(String rootSubject) {
        Map<String, TopicModel> topics = new LinkedHashMap<>();
        String rootPrefix = rootSubject + "/";
        List<String> topicPrefixes = listCommonPrefixes(rootPrefix);

        // Create countdown latch, to prevent premature loading completion
        final CountDownLatch countDownLatch = getCountDownLatch(topicPrefixes);

        for (String fullTopicPrefix : topicPrefixes) {
            String topicName = getNameFromMetadata(fullTopicPrefix);
            if (topicName == null || topics.containsKey(topicName)) continue;

            TopicModel topicModel = new TopicModel(topicName, new ArrayList<>());
            topics.put(topicName, topicModel);

            List<String> subtopicPrefixes = listCommonPrefixes(fullTopicPrefix);
            for (String subtopicPrefix : subtopicPrefixes) {
                String subTopicName = getNameFromMetadata(subtopicPrefix);
                if (subTopicName == null) continue;

                SubtopicModel subtopicModel = new SubtopicModel(subTopicName, new ArrayList<>());
                topicModel.getSubTopicModelList().add(subtopicModel);

                List<String> lessonPrefixes = listCommonPrefixes(subtopicPrefix);
                for (String lessonPrefix : lessonPrefixes) {
                    String lessonName = getNameFromMetadata(lessonPrefix);
                    if (lessonName == null) continue;

                    LessonModel lessonModel = new LessonModel(lessonName, lessonPrefix);
                    subtopicModel.getLessonModelList().add(lessonModel);

                    // Set the spaced repetition
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        try {
                            SpacedRepetitionHandler.getSpacedRepetitionData(context, lessonPrefix, new SpacedRepetitionDataCallback() {
                                @Override
                                public void onSuccess(SpacedRepetition spacedRepetition) {
                                    lessonModel.setSpacedRepetition(spacedRepetition);
                                    subtopicModel.setSpacedRepetitionEnum(spacedRepetition.getSpacedRepetitionEnum());
                                }

                                @Override
                                public void onFailure() {
                                    Log.e(TAG, "getSpacedRepetitionData failure");
                                }
                            });
                        } catch (IOException | JSONException e) {
                            Log.e(TAG, "listS3DirectoryStructure: ", e);
                        } finally {
                            countDownLatch.countDown();
                        }
                    });
                }
            }
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "Failed to wait for lesson tasks: ", e);
        }

        return new ArrayList<>(topics.values());
    }

    @NonNull
    private CountDownLatch getCountDownLatch(List<String> topicPrefixes) {
        int numberOfTasks = 0;
        for (String fullTopicPrefix : topicPrefixes) {
            List<String> subtopicPrefixes = listCommonPrefixes(fullTopicPrefix);
            for (String subtopicPrefix : subtopicPrefixes) {
                List<String> lessonPrefixes = listCommonPrefixes(subtopicPrefix);
                numberOfTasks += lessonPrefixes.size();
            }
        }
        final CountDownLatch countDownLatch = new CountDownLatch(numberOfTasks);
        return countDownLatch;
    }

    private List<String> listCommonPrefixes(String prefix) {
        List<String> prefixes = new ArrayList<>();
        ObjectListing objectListing;
        do {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                    .withBucketName(Globals.bucketName)
                    .withDelimiter("/")
                    .withPrefix(prefix);

            objectListing = s3Client.listObjects(listObjectsRequest);
            prefixes.addAll(objectListing.getCommonPrefixes());
        } while (objectListing.isTruncated());
        return prefixes;
    }

    public String getNameFromMetadata(String prefix) {
        String metadataPath = prefix + "metadata.json";
        S3Object s3Object = s3Client.getObject(Globals.bucketName, metadataPath);
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream stream = s3Object.getObjectContent()) {
            Map<String, String> metadata = mapper.readValue(stream, new TypeReference<Map<String, String>>() {});
            return metadata.get("name");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<FeedItemModel> getLesson(String objectKey) {
        String lessonDataPath = objectKey + "lesson.json";
        S3Object s3Object = s3Client.getObject(Globals.bucketName, lessonDataPath);
        List<FeedItemModel> feedItems = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent(), StandardCharsets.UTF_8))) {
            StringBuilder jsonText = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonText.append(line);
            }

            Log.d(TAG, "getLesson: " + jsonText.toString());

            JSONObject jsonObject = new JSONObject(jsonText.toString());
            JSONArray lessonStructure = jsonObject.getJSONArray("lessonStructure");

            for (int i = 0; i < lessonStructure.length(); i++) {
                JSONObject item = lessonStructure.getJSONObject(i);
                String type = item.getString("type");
                String name = item.getString("name");

                if (type.equals("video")) {
                    URL videoUrl = generateVideoURL(objectKey + "videos/" + name + ".mp4");
                    feedItems.add(new FeedItemModel(videoUrl));
                } else {
                    FeedItemModel.ItemType itemType = null;
                    switch (type) {
                        case "acknowledge":
                            itemType = FeedItemModel.ItemType.ACKNOWLEDGE;
                            break;
                        case "single_word":
                            itemType = FeedItemModel.ItemType.SINGLE_WORD;
                            break;
                        case "multiple_choice":
                            itemType = FeedItemModel.ItemType.MULTIPLE_CHOICE;
                            break;
                        case "matching_pairs":
                            itemType = FeedItemModel.ItemType.MATCHING_PAIRS;
                            break;
                        case "reorder":
                            itemType = FeedItemModel.ItemType.REORDER;
                            break;
                        default:
                            continue;
                    }

                    String path = objectKey + "questions/" + name + ".json";
                    feedItems.add(new FeedItemModel(getQuestion(path, itemType)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return feedItems;
    }

    public Question getQuestion(String questionPath, FeedItemModel.ItemType type) {
        S3Object s3Object = s3Client.getObject(Globals.bucketName, questionPath);
        Question question = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent(), StandardCharsets.UTF_8))) {
            StringBuilder jsonText = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonText.append(line);
            }

            Log.d(TAG, "getQuestion: " + jsonText);
            JSONObject jsonObject = new JSONObject(jsonText.toString());

            switch (type) {
                case ACKNOWLEDGE:
                    Log.d(TAG, "getQuestion: acknowledge");
                    question = new Acknowledge(
                            jsonObject.getString("title"),
                            jsonObject.getString("description"),
                            jsonObject.getString("buttonText")
                    );
                    break;
                case SINGLE_WORD:
                    Log.d(TAG, "getQuestion: single word");
                    question = new SingleWord(
                            jsonObject.getString("title"),
                            jsonObject.getString("description"),
                            jsonObject.getString("answer"),
                            jsonObject.getString("explanation"),
                            Integer.parseInt(jsonObject.getString("score"))
                    );
                    break;
                case MULTIPLE_CHOICE:
                    Log.d(TAG, "getQuestion: multiple choice");
                    question = new MultipleChoice(
                            jsonObject.getString("title"),
                            jsonObject.getString("description"),
                            getStringList(jsonObject.getJSONArray("options")),
                            Integer.parseInt(jsonObject.getString("answer")),
                            jsonObject.getString("explanation"),
                            Integer.parseInt(jsonObject.getString("score"))
                    );
                    break;
                case MATCHING_PAIRS:
                    Log.d(TAG, "getQuestion: matching pairs");
                    question = new MatchingPairs(
                            jsonObject.getString("title"),
                            jsonObject.getString("description"),
                            getPairs(jsonObject),
                            jsonObject.getString("explanation"),
                            Integer.parseInt(jsonObject.getString("score"))
                    );
                    break;
                case REORDER:
                    Log.d(TAG, "getQuestion: reorder");
                    question = new Reorder(
                            jsonObject.getString("title"),
                            jsonObject.getString("description"),
                            getStringList(jsonObject.getJSONArray("order")),
                            jsonObject.getString("start_name"),
                            jsonObject.getString("end_name"),
                            jsonObject.getString("explanation"),
                            Integer.parseInt(jsonObject.getString("score"))
                    );
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getQuestion: ", e);
        }

        return question;
    }

    @NonNull
    private static List<Pair<String, String>> getPairs(JSONObject jsonObject) throws JSONException {
        List<Pair<String, String>> pairsList = new ArrayList<>();
        JSONArray pairs = jsonObject.getJSONArray("pairs");

        for (int i = 0; i < pairs.length(); i++) {
            JSONArray pair = pairs.getJSONArray(i);
            String left = pair.getString(0);
            String right = pair.getString(1);
            pairsList.add(new Pair<>(left, right));
        }
        return pairsList;
    }

    private List<String> getStringList(JSONArray jsonArray) {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                result.add(jsonArray.getString(i));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    private URL generateVideoURL(String objectKey) {
        Date exparationDate = new Date();
        exparationDate.setTime(exparationDate.getTime() + 3600000);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(Globals.bucketName, objectKey)
                .withMethod(HttpMethod.GET)
                .withExpiration(exparationDate);

        return s3Client.generatePresignedUrl(generatePresignedUrlRequest);
    }
}
