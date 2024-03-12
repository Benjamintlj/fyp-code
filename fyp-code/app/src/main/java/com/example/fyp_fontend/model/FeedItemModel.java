package com.example.fyp_fontend.model;

import android.content.Context;

import com.example.fyp_fontend.Interfaces.Question;
import com.example.fyp_fontend.network.S3Handler;

public class FeedItemModel {

    public enum ItemType {
        VIDEO,
        ACKNOWLEDGE
    }

    public ItemType itemType;
    private Question question;

    public FeedItemModel(ItemType itemType, String s3ContentLocation, Context context) {
        this.itemType = itemType;

        if (itemType == ItemType.VIDEO) {
            // TODO: upload video
        } else if (itemType == ItemType.ACKNOWLEDGE) {
            question = S3Handler.getInstance(context).getQuestion(s3ContentLocation, ItemType.ACKNOWLEDGE);
        }
    }

    public Question getQuestion() {
        return question;
    }
}
