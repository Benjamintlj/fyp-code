package com.example.fyp_fontend.network;

import android.content.Context;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.example.fyp_fontend.Utils.Globals;
import com.example.fyp_fontend.model.FeedItemModel;
import com.example.fyp_fontend.model.LessonModel;
import com.example.fyp_fontend.model.SubtopicModel;
import com.example.fyp_fontend.model.TopicModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
                }
            }
        }

        return new ArrayList<>(topics.values());
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

    private String getNameFromMetadata(String prefix) {
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

    public List<FeedItemModel> getLesson(String s3Url) {
        String lessonDataPath = s3Url + "lesson.json";
        S3Object s3Object = s3Client.getObject(Globals.bucketName, lessonDataPath);
        List<FeedItemModel> feedItems = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent(), StandardCharsets.UTF_8))) {
            StringBuilder jsonText = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonText.append(line);
            }

            JSONObject jsonObject = new JSONObject(jsonText.toString());
            JSONArray lessonStructure = jsonObject.getJSONArray("lessonStructure");

            for (int i = 0; i < lessonStructure.length(); i++) {
                JSONObject item = lessonStructure.getJSONObject(i);
                String type = item.getString("type");
                String name = item.getString("name");

                FeedItemModel.ItemType itemType = null;
                switch (type) {
                    case "video":
                        itemType = FeedItemModel.ItemType.VIDEO;
                        break;
                    case "question":
                        itemType = FeedItemModel.ItemType.QUESTION;
                        break;
                }

                if (itemType != null) {
                    FeedItemModel feedItem = new FeedItemModel(itemType, name);
                    feedItems.add(feedItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return feedItems;
    }
}
