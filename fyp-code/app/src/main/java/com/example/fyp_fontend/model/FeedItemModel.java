package com.example.fyp_fontend.model;

import com.example.fyp_fontend.model.Question.Question;
import com.example.fyp_fontend.model.Question.Acknowledge;
import com.example.fyp_fontend.model.Question.SingleWord;

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
