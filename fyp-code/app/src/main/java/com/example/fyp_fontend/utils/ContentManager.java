package com.example.fyp_fontend.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.fyp_fontend.activity.lesson.AcknowledgeQuestionActivity;
import com.example.fyp_fontend.activity.lesson.SingleWordQuestionActivity;
import com.example.fyp_fontend.activity.lesson.VideoActivity;
import com.example.fyp_fontend.model.FeedItemModel;

import java.util.List;

public class ContentManager {
    private static final String TAG = "ContentManager";

    private static int nextItem;
    private static List<FeedItemModel> contentItems;

    public enum ContentManagerNewActivity {
        NEXT_ITEM
    }

    public static void init(List<FeedItemModel> contentItems) {
        ContentManager.contentItems = contentItems;
        ContentManager.nextItem = 0;
    }

    public static FeedItemModel getCurrentItem() {
        FeedItemModel feedItemModel = contentItems.get(nextItem - 1);
        Log.d(TAG, "getCurrentItem: " + feedItemModel.itemType.toString());
        return feedItemModel;
    }

    public static void nextItem(Context context) {
        if (nextItem >= contentItems.size()) {
            // TODO: finish screen
            return;
        }

        FeedItemModel currentItem = contentItems.get(nextItem++);
        Log.d(TAG, "nextItem: " + currentItem.itemType.toString());
        Intent intent;

        switch (currentItem.itemType) {
            case VIDEO:
                intent = new Intent(context, VideoActivity.class);
                intent.putExtra("videoUrl", currentItem.getVideoUrl().toString());
                break;
            case ACKNOWLEDGE:
                intent = new Intent(context, AcknowledgeQuestionActivity.class);
                break;
            case SINGLE_WORD:
                intent = new Intent(context, SingleWordQuestionActivity.class);
                break;
            default:
                return;
        }

        ((Activity) context).startActivityForResult(intent, ContentManagerNewActivity.NEXT_ITEM.ordinal());
    }
}
