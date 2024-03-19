package com.example.fyp_fontend.model.Question;

import java.util.List;

public class Reorder implements Question {

    private String title;
    private String description;
    private List<String> order;
    private String startName, endName;
    private String explanation;
    private int score;

    public Reorder(String title, String description, List<String> order, String startName, String endName, String explanation, int score) {
        this.title = title;
        this.description = description;
        this.order = order;
        this.startName = startName;
        this.endName = endName;
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

    @Override
    public String getExplanation() {
        return explanation;
    }

    @Override
    public int getScore() {
        return 0;
    }

    public List<String> getOrder() {
        return order;
    }

    public String getStartName() {
        return startName;
    }

    public String getEndName() {
        return endName;
    }
}
