package com.example.fyp_fontend.model.Question;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchingPairs implements Question {
    private static final String TAG = "MatchingPairs";
    private String title;
    private String description;
    private String explanation;
    private List<PairItem> leftItems, rightItems;
    private int score;
    private int leftActiveIndex, rightActiveIndex;

    public MatchingPairs (String title, String description, List<Pair<String, String>> stringPairs, String explanation, int score) {
        this.title = title;
        this.description = description;
        this.explanation = explanation;
        this.score = score;
        leftActiveIndex = -1;
        rightActiveIndex = -1;
        leftItems = new ArrayList<>();
        rightItems = new ArrayList<>();

        for (int i = 0; i < stringPairs.size(); i++) {
            Pair<String, String> pair = stringPairs.get(i);

            leftItems.add(new PairItem(pair.first, i));
            rightItems.add(new PairItem(pair.second, i));
        }

        Collections.shuffle(leftItems);
        Collections.shuffle(rightItems);
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

    @Override
    public String getExplanation() {
        return explanation;
    }

    @Override
    public int getScore() {
        return score;
    }

    public int getPairId(boolean isLeft, int index) {
        return getColumn(isLeft).get(index).pairId;
    }

    public PairItem getPairItemByPairId(boolean isLeft, int pairId) {

        for (PairItem item : getColumn(isLeft)) {
            if (item.pairId == pairId) return item;
        }
        return null;
    }

    public List<PairItem> getColumn(boolean isLeft) {
        return isLeft ? leftItems : rightItems;
    }

    public void setActiveIndex(boolean isLeft, int activeIndex) {
        if (isLeft) {
            this.leftActiveIndex = activeIndex;
        } else {
            this.rightActiveIndex = activeIndex;
        }
    }

    public Boolean checkMatch() {
        if (rightActiveIndex == -1 || leftActiveIndex == -1) return null;

        boolean match = getPairId(true, leftActiveIndex) == getPairId(false, rightActiveIndex);

        rightActiveIndex = -1;
        leftActiveIndex = -1;

        return match;
    }
}
