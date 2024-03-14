package com.example.fyp_fontend.utils;

import androidx.annotation.NonNull;

import java.util.Random;

public class RandomSelector {
    public static String selectRandomString(@NonNull String[] strings) {
        if (strings.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty.");
        }

        Random random = new Random();
        return strings[random.nextInt(strings.length)];
    }
}
