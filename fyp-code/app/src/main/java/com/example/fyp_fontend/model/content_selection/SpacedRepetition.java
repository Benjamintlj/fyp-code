package com.example.fyp_fontend.model.content_selection;

import androidx.annotation.ColorRes;

import com.example.fyp_fontend.R;

public class SpacedRepetition {
    SpacedRepetitionEnum spacedRepetitionEnum = SpacedRepetitionEnum.UNKNOWN;
    long lastCompleted, timeToWait;
    String waitTime;

    public void init(long lastCompleted, long timeToWait) {
        this.lastCompleted = lastCompleted;
        this.timeToWait = timeToWait;

        long currentTime = System.currentTimeMillis();
        long completionTime = lastCompleted + timeToWait;
        long day1 = 86400000;

        // Set the Enum
        if (lastCompleted < 0 || timeToWait < 0) {
            spacedRepetitionEnum = SpacedRepetitionEnum.UNKNOWN;
        } else {
            if (currentTime < (completionTime - day1)) {
                spacedRepetitionEnum = SpacedRepetitionEnum.GREEN;
            } else if (currentTime >= (completionTime - day1) &&
                    currentTime < (completionTime + day1)) {
                spacedRepetitionEnum = SpacedRepetitionEnum.AMBER;
            } else if (currentTime >= (completionTime + day1)) {
                spacedRepetitionEnum = SpacedRepetitionEnum.RED;
            } else {
                spacedRepetitionEnum = SpacedRepetitionEnum.UNKNOWN;
            }
        }

        // Set the string
        long deltaTime = completionTime - currentTime;
        if (deltaTime <= 0) {
            waitTime = "ready";
        } else {
            long daysUntilCompletion = deltaTime / day1;
            if (daysUntilCompletion > 1) {
                waitTime = daysUntilCompletion + " days";
            } else {
                waitTime = "ready";
            }
        }
    }

    public SpacedRepetitionEnum getSpacedRepetitionEnum() {
        return spacedRepetitionEnum;
    }

    public long getLastCompleted() {
        return lastCompleted;
    }

    public long getTimeToWait() {
        return timeToWait;
    }

    public String getWaitTime() {
        return waitTime;
    }

    public static @ColorRes int getCardColour(SpacedRepetitionEnum spacedRepetitionEnum) {

        @ColorRes int colour;

        switch (spacedRepetitionEnum) {
            case RED:
                colour = R.color.failureRed;
                break;
            case AMBER:
                colour = R.color.warningAmber;
                break;
            case GREEN:
                colour = R.color.safeGreen;
                break;
            case UNKNOWN:
            default:
                colour = R.color.cardBg;
                break;
        }

        return colour;
    }
}
