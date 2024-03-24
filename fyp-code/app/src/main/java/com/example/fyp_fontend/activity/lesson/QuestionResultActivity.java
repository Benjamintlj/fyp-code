package com.example.fyp_fontend.activity.lesson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.fragments.ContinueFragment;
import com.example.fyp_fontend.utils.ContentManager;
import com.example.fyp_fontend.utils.RandomSelector;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class QuestionResultActivity extends AppCompatActivity {
    private static final String TAG = "QuestionResultActivity";

    public static final String ARG_IS_CORRECT = "isCorrect";
    public static final String ARG_INCREASE_SCORE = "increaseScore";

    boolean isCorrect;
    TextView titleViewText, descriptionTextView, scoreTextView, timeTextView;
    GifImageView gifImageView;
    int increaseScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_result);

        isCorrect = getIntent().getBooleanExtra(ARG_IS_CORRECT, false);
        increaseScore = getIntent().getIntExtra(ARG_INCREASE_SCORE, 0);

        initViews();
        setScore();
        setTimer();

        try {
            setDialog();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        initFragment();
    }

    private void initViews() {
        titleViewText = findViewById(R.id.titleViewText);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        gifImageView = findViewById(R.id.gifImageView);
        scoreTextView = findViewById(R.id.scoreTextView);
        timeTextView = findViewById(R.id.timeTextView);
    }

    private void setDialog() throws IOException {
        GifDrawable gifDrawable;

        if (isCorrect) {
            titleViewText.setText(RandomSelector.selectRandomString(new String[]{
                    getString(R.string.congratulations_1),
                    getString(R.string.congratulations_2),
                    getString(R.string.congratulations_3),
                    getString(R.string.congratulations_4),
                    getString(R.string.congratulations_5),
                    getString(R.string.congratulations_6),
                    getString(R.string.congratulations_7),
                    getString(R.string.congratulations_8),
                    getString(R.string.congratulations_9)
            }));

            gifDrawable = new GifDrawable(getResources(), R.drawable.tick);
        } else {
            titleViewText.setText(RandomSelector.selectRandomString(new String[]{
                    getString(R.string.failure_1),
                    getString(R.string.failure_2),
                    getString(R.string.failure_3),
                    getString(R.string.failure_4),
                    getString(R.string.failure_5),
                    getString(R.string.failure_6),
                    getString(R.string.failure_7),
                    getString(R.string.failure_8)
            }));

            gifDrawable = new GifDrawable(getResources(), R.drawable.cross);
        }

        descriptionTextView.setText(ContentManager.getCurrentItem().getQuestion().getExplanation());

        gifDrawable.setLoopCount(1);
        gifImageView.setImageDrawable(gifDrawable);
    }

    private void setScore() {
        if (isCorrect) {
            ContentManager.increaseScore(increaseScore);
            Log.d(TAG, "score: " + ContentManager.getScore());
            Log.d(TAG, "increase by: " + String.valueOf(increaseScore));

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                ValueAnimator valueAnimator = ValueAnimator.ofInt(0, increaseScore);
                valueAnimator.setDuration(1200);

                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                        scoreTextView.setText("+" + animation.getAnimatedValue().toString());
                    }
                });

                valueAnimator.start();
            }, 1000);
        } else {
            int decreaseBy = 1;
            ContentManager.decreaseScore(decreaseBy);
            scoreTextView.setText(String.format("-%s", Integer.toString(decreaseBy)));
            scoreTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.failureRed));
        }
    }

    private void setTimer() {
        timeTextView.setText(ContentManager.stopTimer());
    }

    private void initFragment() {
        ContinueFragment continueFragment = new ContinueFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, continueFragment).commit();
    }
}
