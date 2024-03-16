package com.example.fyp_fontend.model.Question;

import android.util.Log;

public class SingleWord implements Question {
    private static final String TAG = "SingleWord";
    private String title;
    private String description;
    private String answer;
    private String explanation;
    private int score;

    public SingleWord (String title, String description, String answer, String explanation, int score) {
        this.title = title;
        this.description = description;
        this.answer = answer;
        this.explanation = explanation;
        this.score = score;
        Log.d(TAG, "SingleWord: should be made");
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public String getAnswer() {
        return answer;
    }

    @Override
    public String getExplanation() {
        return explanation;
    }

    @Override
    public int getScore() {
        return score;
    }
}
