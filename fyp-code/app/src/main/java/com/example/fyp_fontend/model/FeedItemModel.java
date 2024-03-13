package com.example.fyp_fontend.model;

import android.content.Context;

import com.example.fyp_fontend.Interfaces.Question;
import com.example.fyp_fontend.model.Question.Acknowledge;
import com.example.fyp_fontend.model.Question.SingleWord;
import com.example.fyp_fontend.network.S3Handler;

import java.net.URL;

public class FeedItemModel {

    public enum ItemType {
        VIDEO,
        ACKNOWLEDGE,
        SINGLE_WORD
    }

    public ItemType itemType;
    private Question question;
    private URL videoUrl;

    public FeedItemModel(Question question) {

        this.question = question;

        if (question instanceof Acknowledge) itemType = ItemType.ACKNOWLEDGE;
        else if (question instanceof SingleWord) itemType = ItemType.SINGLE_WORD;

//        this.itemType = itemType;
//
//        if (itemType == ItemType.ACKNOWLEDGE) {
//            question = S3Handler.getInstance(context).getQuestion(s3ContentLocation, ItemType.ACKNOWLEDGE);
//        }
    }

    public FeedItemModel(URL videoUrl) {
        this.itemType = ItemType.VIDEO;
        this.videoUrl = videoUrl;
    }

    public Question getQuestion() {
        return question;
    }

    public URL getVideoUrl() {
        return videoUrl;
    }
}
