package com.example.fyp_fontend.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.fyp_fontend.activity.lesson.FinalLessonActivity;
import com.example.fyp_fontend.activity.lesson.QuestionAcknowledgeActivity;
import com.example.fyp_fontend.activity.lesson.QuestionMatchingPairsActivity;
import com.example.fyp_fontend.activity.lesson.QuestionMultipleChoiceActivity;
import com.example.fyp_fontend.activity.lesson.QuestionReorderActivity;
import com.example.fyp_fontend.activity.lesson.QuestionSingleWordActivity;
import com.example.fyp_fontend.activity.lesson.VideoActivity;
import com.example.fyp_fontend.model.FeedItemModel;
import com.example.fyp_fontend.model.content_selection.LessonModel;
import com.example.fyp_fontend.model.content_selection.SpacedRepetitionEnum;

import java.util.List;
import java.util.Locale;

public class ContentManager {
    private static final String TAG = "ContentManager";

    private static int nextItem;
    private static List<FeedItemModel> contentItems;
    private static int score, totalNumOfQuestions, questionsAnsweredCorrectly;
    private static long questionTimer, totalQuestionTimer;
    private static LessonModel lessonModel;
    public enum ContentManagerNewActivity {
        NEXT_ITEM
    }

    public static void init(List<FeedItemModel> contentItems, LessonModel lessonModel) {
        ContentManager.contentItems = contentItems;
        ContentManager.lessonModel = lessonModel;
        nextItem = 0;
        score = 0;
        questionTimer = 0;
        totalQuestionTimer = 0;
        questionsAnsweredCorrectly = 0;


        totalNumOfQuestions = 0;
        for(FeedItemModel item : contentItems) {
            if (item.itemType != FeedItemModel.ItemType.VIDEO && item.itemType != FeedItemModel.ItemType.ACKNOWLEDGE) {
                totalNumOfQuestions++;
            }
        }
    }

    public static void increaseScore(int increaseBy) {
        if (!lessonModel.getSpacedRepetition().getSpacedRepetitionEnum().equals(SpacedRepetitionEnum.GREEN)) {
            score += increaseBy;
            questionsAnsweredCorrectly++;
        }
    }

    public static void decreaseScore(int decreaseBy) {
        if ((score - decreaseBy) < 0) {
            score = 0;
        } else {
            score -= decreaseBy;
        }
    }

    public static int getScore() {
        return score;
    }
    public static void startTimer() {
        questionTimer = System.currentTimeMillis();
    }

    public static String stopTimer () {
        long difference = System.currentTimeMillis() - questionTimer;
        totalQuestionTimer += difference;

        return timeToString(difference);
    }

    public static String getTotalQuestionTimer() {
        return timeToString(totalQuestionTimer);
    }

    private static String timeToString(long time) {
        int seconds = (int) (time / 1000) % 60 ;
        int minutes = (int) ((time / (1000*60)) % 60);

        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    public static FeedItemModel getCurrentItem() {
        FeedItemModel feedItemModel = contentItems.get(nextItem - 1);
        Log.d(TAG, "getCurrentItem: " + feedItemModel.itemType.toString());
        return feedItemModel;
    }

    public static int getTotalNumOfQuestions() {
        return totalNumOfQuestions;
    }

    public static int getQuestionsAnsweredCorrectly() {
        return questionsAnsweredCorrectly;
    }

    public static void nextItem(Context context) {
        Intent intent;
        if (nextItem >= contentItems.size()) {
            intent = new Intent(context, FinalLessonActivity.class);
        } else {
            FeedItemModel currentItem = contentItems.get(nextItem++);

            switch (currentItem.itemType) {
                case VIDEO:
                    intent = new Intent(context, VideoActivity.class);
                    intent.putExtra("videoUrl", currentItem.getVideoUrl().toString());
                    break;
                case ACKNOWLEDGE:
                    intent = new Intent(context, QuestionAcknowledgeActivity.class);
                    startTimer();
                    break;
                case SINGLE_WORD:
                    intent = new Intent(context, QuestionSingleWordActivity.class);
                    startTimer();
                    break;
                case MULTIPLE_CHOICE:
                    intent = new Intent(context, QuestionMultipleChoiceActivity.class);
                    startTimer();
                    break;
                case MATCHING_PAIRS:
                    intent = new Intent(context, QuestionMatchingPairsActivity.class);
                    startTimer();
                    break;
                case REORDER:
                    intent = new Intent(context, QuestionReorderActivity.class);
                    startTimer();
                    break;
                default:
                    return;
            }
        }

        ((Activity) context).startActivityForResult(intent, ContentManagerNewActivity.NEXT_ITEM.ordinal());
    }

    public static String getS3Url() {
        return lessonModel.getS3Url();
    }

    public static int getPercentage() {
        return (questionsAnsweredCorrectly / totalNumOfQuestions) * 100;
    }
}
