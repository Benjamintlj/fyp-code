package com.example.fyp_fontend.model.Question;

import com.example.fyp_fontend.interfaces.Question;

public class SingleWord implements Question {
    private String title;
    private String description;
    private String answer;
    private String explanation;

    public SingleWord (String title, String description, String answer, String explanation) {
        this.title = title;
        this.description = description;
        this.answer = answer;
        this.explanation = explanation;
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

    public String getExplanation() {
        return explanation;
    }
}
