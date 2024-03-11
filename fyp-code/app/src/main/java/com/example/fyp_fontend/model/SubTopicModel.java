package com.example.fyp_fontend.model;

import java.util.List;

public class SubTopicModel {
    private String name;
    private List<LessonModel> lessonModelList;

    public SubTopicModel(String name, List<LessonModel> lessonModelList) {
        this.name = name;
        this.lessonModelList = lessonModelList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LessonModel> getLessonModelList() {
        return lessonModelList;
    }

    public void setLessonModelList(List<LessonModel> lessonModelList) {
        this.lessonModelList = lessonModelList;
    }
}
