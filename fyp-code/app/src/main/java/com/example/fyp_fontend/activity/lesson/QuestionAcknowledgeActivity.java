package com.example.fyp_fontend.activity.lesson;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.fragments.ContinueFragment;
import com.example.fyp_fontend.utils.ContentManager;

public class QuestionAcknowledgeActivity extends AppCompatActivity {

    TextView titleTextView, descriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_acknowledge);

        initViews();
        setContent();
    }

    private void initViews() {
        titleTextView = findViewById(R.id.loadingTitleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);

        ContinueFragment continueFragment = new ContinueFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, continueFragment).commit();
    }

    private void setContent() {
        titleTextView.setText(ContentManager.getCurrentItem().getQuestion().getTitle());
        descriptionTextView.setText(ContentManager.getCurrentItem().getQuestion().getDescription());
    }
}