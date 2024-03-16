package com.example.fyp_fontend.model.Question;

import java.util.List;

public class MultipleChoice implements Question {
    private static final String TAG = "MultipleChoice";
    private String title;
    private String description;
    private List<String> options;
    private int answer;
    private String explanation;
    private int score;

    public MultipleChoice (String title, String description, List<String> options, int answer, String explanation, int score) {
        this.title = title;
        this.description = description;
        this.options = options;
        this.answer = answer;
        this.explanation = explanation;
        this.score = score;
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

    public int getAnswer() {
        return answer;
    }

    public List<String> getOptions() {
        return options;
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
