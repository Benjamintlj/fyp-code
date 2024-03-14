package com.example.fyp_fontend.model.Question;

import com.example.fyp_fontend.interfaces.Question;

public class Acknowledge implements Question {

    private String title;
    private String description;
    private String buttonText;

    public Acknowledge(String title, String description, String buttonText) {
        this.title = title;
        this.description = description;
        this.buttonText = buttonText;
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

    public String getButtonText() {
        return buttonText;
    }
}
