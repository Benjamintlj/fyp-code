package com.example.fyp_fontend.model.content_selection;

public class LessonModel {

    private String title;
    private String s3Url;
    private SpacedRepetition spacedRepetition = new SpacedRepetition();

    public LessonModel(String title, String s3Url) {
        this.title = title;
        this.s3Url = s3Url;
    }

    public void setSpacedRepetition(SpacedRepetition spacedRepetition) {
        this.spacedRepetition = spacedRepetition;
    }

    public SpacedRepetition getSpacedRepetition() {
        return spacedRepetition;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getS3Url() {
        return s3Url;
    }
}
