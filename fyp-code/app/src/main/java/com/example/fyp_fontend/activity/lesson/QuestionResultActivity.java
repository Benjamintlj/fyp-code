package com.example.fyp_fontend.activity.lesson;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.fragments.ContinueFragment;
import com.example.fyp_fontend.utils.ContentManager;
import com.example.fyp_fontend.utils.RandomSelector;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class QuestionResultActivity extends AppCompatActivity {

    public static final String ARG_IS_CORRECT = "isCorrect";

    boolean isCorrect;
    TextView titleViewText;
    TextView descriptionTextView;
    GifImageView gifImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_result);

        isCorrect = getIntent().getBooleanExtra(ARG_IS_CORRECT, false);

        initViews();

        try {
            setDialog();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initViews() {
        titleViewText = findViewById(R.id.titleViewText);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        gifImageView = findViewById(R.id.gifImageView);

        ContinueFragment continueFragment = new ContinueFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, continueFragment).commit();
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
}
