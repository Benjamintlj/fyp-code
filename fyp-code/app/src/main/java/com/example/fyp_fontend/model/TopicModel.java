package com.example.fyp_fontend.model;

import java.util.ArrayList;
import java.util.List;

public class TopicModel {

    private String name;
    private List<SubTopicModel> subTopicModelList;

    public TopicModel(String name, List<SubTopicModel> subTopicModelList) {
        this.name = name;
        this.subTopicModelList = subTopicModelList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SubTopicModel> getSubTopicModelList() {
        return subTopicModelList;
    }

    public void setSubTopicModelList(List<SubTopicModel> subTopicModelList) {
        this.subTopicModelList = subTopicModelList;
    }

    public List<String> getSubTopicNames() {
        List<String> subTopicNamesList = new ArrayList<>();

        for (SubTopicModel subTopicModel: subTopicModelList) {
            subTopicNamesList.add(subTopicModel.getName());
        }

        return subTopicNamesList;
    }
}
