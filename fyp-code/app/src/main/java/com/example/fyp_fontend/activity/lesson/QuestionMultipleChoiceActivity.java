package com.example.fyp_fontend.activity.lesson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.adapter.MultipleChoiceAdapter;
import com.example.fyp_fontend.utils.ContentManager;

import java.util.ArrayList;
import java.util.Objects;

public class QuestionMultipleChoiceActivity extends AppCompatActivity implements MultipleChoiceAdapter.MultipleChoiceAdapterInterface {

    TextView titleTextView, descriptionTextView;
    RecyclerView questionsRecyclerView;
    MultipleChoiceAdapter multipleChoiceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_multiple_choice);

        initViews();
        setContent();
    }

    private void initViews() {
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        questionsRecyclerView = findViewById(R.id.questionsRecyclerView);

        multipleChoiceAdapter = new MultipleChoiceAdapter(getApplicationContext(), this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        questionsRecyclerView.setLayoutManager(layoutManager);
        questionsRecyclerView.setAdapter(multipleChoiceAdapter);
    }

    private void setContent() {
        titleTextView.setText(ContentManager.getCurrentItem().getQuestion().getTitle());
        descriptionTextView.setText(ContentManager.getCurrentItem().getQuestion().getDescription());
    }

    @Override
    public void answerListener(boolean isCorrect) {
        Intent intent = new Intent(getApplicationContext(), QuestionResultActivity.class);

        if (isCorrect) {
            intent.putExtra(QuestionResultActivity.ARG_IS_CORRECT, true);
            intent.putExtra(QuestionResultActivity.ARG_INCREASE_SCORE, ContentManager.getCurrentItem().getQuestion().getScore());
        } else {
            intent.putExtra(QuestionResultActivity.ARG_IS_CORRECT, false);
        }

        startActivity(intent);
        finish();
    }
}