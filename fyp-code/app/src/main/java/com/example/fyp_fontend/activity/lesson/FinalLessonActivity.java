package com.example.fyp_fontend.activity.lesson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.network.LeaderboardHandler;
import com.example.fyp_fontend.network.SpacedRepetitionHandler;
import com.example.fyp_fontend.utils.ContentManager;
import com.example.fyp_fontend.utils.RandomSelector;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class FinalLessonActivity extends AppCompatActivity {
    private static final String TAG = "FinalLessonActivity";

    GifImageView confettiGifImageView;
    ImageView scoreImageView, timeImageView;
    TextView scoreTextView, timeTextView, titleViewText, questionTextView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_lesson);

        updateScore();

        initViews();
        confettiAnimation();
        setTitle();
        setScore();
        setTime();
        setQuestionsAnsweredCorrectly();
        initListeners();
    }

    private void initViews() {
        confettiGifImageView = findViewById(R.id.confettiGifImageView);
        scoreImageView = findViewById(R.id.scoreImageView);
        timeImageView = findViewById(R.id.timeImageView);
        scoreTextView = findViewById(R.id.scoreTextView);
        timeTextView = findViewById(R.id.timeTextView);
        titleViewText = findViewById(R.id.titleViewText);
        questionTextView = findViewById(R.id.questionTextView);
        button = findViewById(R.id.button);
    }

    private void confettiAnimation() {
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.confetti);
            gifDrawable.setLoopCount(1);
            confettiGifImageView.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            Log.e(TAG, "initViews: ", e);
        }
    }

    private void setTitle() {
        titleViewText.setText(RandomSelector.selectRandomString(new String[]{
                getString(R.string.end_lesson_title_1),
                getString(R.string.end_lesson_title_2),
                getString(R.string.end_lesson_title_3),
                getString(R.string.end_lesson_title_4),
                getString(R.string.end_lesson_title_5),
                getString(R.string.end_lesson_title_6)
        }));
    }

    private void setScore() {
        int score = ContentManager.getScore();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ValueAnimator valueAnimator = ValueAnimator.ofInt(0, score);
            valueAnimator.setDuration(1200);

            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                    scoreTextView.setText(animation.getAnimatedValue().toString());
                }
            });

            valueAnimator.start();
        }, 2000);
    }

    private void setTime() {
        timeTextView.setText(ContentManager.getTotalQuestionTimer());
    }

    private void setQuestionsAnsweredCorrectly() {
        String questionsAnsweredCorrectly = ContentManager.getQuestionsAnsweredCorrectly() + "/" + ContentManager.getTotalNumOfQuestions();
        questionTextView.setText(questionsAnsweredCorrectly);
    }

    private void initListeners() {
        button.setOnClickListener(view -> {
            finish();
        });
    }

    private void updateScore() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        int score = ContentManager.getScore();
        String s3Url = ContentManager.getS3Url();
        int percentage = ContentManager.getPercentage();

        executor.execute(() -> {
            try {
                LeaderboardHandler.updateScore(getApplicationContext(), score);
            } catch (IOException e) {
                Log.e(TAG, "updateScore: ", e);
            }

            try {
                SpacedRepetitionHandler.updateSpacedRepetitionData(
                        getApplicationContext(),
                        s3Url,
                        percentage
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
