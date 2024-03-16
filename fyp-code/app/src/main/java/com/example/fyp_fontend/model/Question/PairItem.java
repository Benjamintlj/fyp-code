package com.example.fyp_fontend.model.Question;

public class PairItem {
    String name;
    int pairId;
    boolean isActive;

    public PairItem(String name, int pairId) {
        this.name = name;
        this.pairId = pairId;
        this.isActive = true;
    }

    public int getPairId() {
        return pairId;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
