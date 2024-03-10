package com.example.fyp_fontend.model;

public class FeedItemModel {

    public enum ItemType {
        VIDEO,
        QUESTION
    }

    public ItemType itemType;
    private String temp;

    public FeedItemModel(ItemType itemType) {
        this.temp = "text";
        this.itemType = itemType;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
