package com.example.fyp_fontend.model.content_selection;

public class LessonModel {

    private String title;
    private String s3Url;

    public LessonModel(String title, String s3Url) {
        this.title = title;
        this.s3Url = s3Url;
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

    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }
}
