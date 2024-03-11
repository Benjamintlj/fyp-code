package com.example.fyp_fontend.model;

public class FeedItemModel {

    public enum ItemType {
        VIDEO,
        QUESTION
    }

    public ItemType itemType;
    private String fileName;

    public FeedItemModel(ItemType itemType, String fileName) {
        this.fileName = fileName;
        this.itemType = itemType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
