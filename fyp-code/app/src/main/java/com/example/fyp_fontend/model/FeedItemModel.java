package com.example.fyp_fontend.model;

import com.example.fyp_fontend.model.Question.MatchingPairs;
import com.example.fyp_fontend.model.Question.MultipleChoice;
import com.example.fyp_fontend.model.Question.Question;
import com.example.fyp_fontend.model.Question.Acknowledge;
import com.example.fyp_fontend.model.Question.Reorder;
import com.example.fyp_fontend.model.Question.SingleWord;

import java.net.URL;

public class FeedItemModel {

    public enum ItemType {
        VIDEO,
        ACKNOWLEDGE,
        SINGLE_WORD,
        MULTIPLE_CHOICE,
        MATCHING_PAIRS,
        REORDER
    }

    public ItemType itemType;
    private Question question;
    private URL videoUrl;

    public FeedItemModel(Question question) {

        this.question = question;

        if (question instanceof Acknowledge) itemType = ItemType.ACKNOWLEDGE;
        else if (question instanceof SingleWord) itemType = ItemType.SINGLE_WORD;
        else if (question instanceof MultipleChoice) itemType = ItemType.MULTIPLE_CHOICE;
        else if (question instanceof MatchingPairs) itemType = ItemType.MATCHING_PAIRS;
        else if (question instanceof Reorder) itemType = ItemType.REORDER;
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
