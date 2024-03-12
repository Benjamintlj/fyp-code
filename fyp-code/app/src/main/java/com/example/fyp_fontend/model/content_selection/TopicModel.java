package com.example.fyp_fontend.model.content_selection;

import com.example.fyp_fontend.model.content_selection.SubtopicModel;

import java.util.List;

public class TopicModel {

    private String name;
    private List<SubtopicModel> subTopicModelList;

    public TopicModel(String name, List<SubtopicModel> subTopicModelList) {
        this.name = name;
        this.subTopicModelList = subTopicModelList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SubtopicModel> getSubTopicModelList() {
        return subTopicModelList;
    }

    public void setSubTopicModelList(List<SubtopicModel> subTopicModelList) {
        this.subTopicModelList = subTopicModelList;
    }
}
