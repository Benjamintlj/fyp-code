package com.example.fyp_fontend.activity.lesson;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.example.fyp_fontend.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class FinalLessonActivity extends AppCompatActivity {
    private static final String TAG = "FinalLessonActivity";

    GifImageView confettiGifImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_lesson);

        initViews();
    }

    private void initViews() {

        confettiGifImageView = findViewById(R.id.confettiGifImageView);

        confettiGifImageView.post(() -> {
            int width = confettiGifImageView.getWidth();
            ViewGroup.LayoutParams layoutParams = confettiGifImageView.getLayoutParams();
            layoutParams.height = width;
            confettiGifImageView.setLayoutParams(layoutParams);
        });

        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.confetti);
            gifDrawable.setLoopCount(1);
            confettiGifImageView.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            Log.e(TAG, "initViews: ", e);
        }
    }
}