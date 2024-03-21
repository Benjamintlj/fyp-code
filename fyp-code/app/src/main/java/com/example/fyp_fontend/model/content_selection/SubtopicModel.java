package com.example.fyp_fontend.model.content_selection;

import com.example.fyp_fontend.model.content_selection.LessonModel;

import java.util.ArrayList;
import java.util.List;

public class SubtopicModel {
    private String name;
    private List<LessonModel> lessonModelList;
    private SpacedRepetitionEnum spacedRepetitionEnum;

    public SubtopicModel(String name, List<LessonModel> lessonModelList) {
        this.name = name;
        this.lessonModelList = lessonModelList;
        spacedRepetitionEnum = SpacedRepetitionEnum.UNKNOWN;
    }

    public SpacedRepetitionEnum getSpacedRepetitionEnum() {
        return spacedRepetitionEnum;
    }

    public void setSpacedRepetitionEnum(SpacedRepetitionEnum spacedRepetitionEnum) {
        if (spacedRepetitionEnum.ordinal() > this.spacedRepetitionEnum.ordinal()) {
            this.spacedRepetitionEnum = spacedRepetitionEnum;
        }
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

    public List<String> getLessonNames() {
        List<String> lessonNamesList = new ArrayList<>();

        for (LessonModel lessonModel: lessonModelList) {
            lessonNamesList.add(lessonModel.getTitle());
        }

        return lessonNamesList;
    }
}
