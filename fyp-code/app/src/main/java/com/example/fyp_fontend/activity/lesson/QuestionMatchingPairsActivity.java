package com.example.fyp_fontend.activity.lesson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.adapter.MatchingPairsAdapter;
import com.example.fyp_fontend.model.Question.MatchingPairs;
import com.example.fyp_fontend.utils.ContentManager;

public class QuestionMatchingPairsActivity extends AppCompatActivity implements MatchingPairsAdapter.MatchingPairsAdapterInterface {

    TextView titleTextView, descriptionTextView;
    RecyclerView leftRecyclerView, rightRecyclerView;
    MatchingPairsAdapter leftAdapter, rightAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_matching_pairs);

        initViews();
        setContent();
    }

    private void initViews() {
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        rightRecyclerView = findViewById(R.id.rightRecyclerView);
        leftRecyclerView = findViewById(R.id.leftRecyclerView);

        leftAdapter = new MatchingPairsAdapter(true, this);
        RecyclerView.LayoutManager leftLayoutManager = new LinearLayoutManager(getApplicationContext());
        leftRecyclerView.setLayoutManager(leftLayoutManager);
        leftRecyclerView.setAdapter(leftAdapter);

        rightAdapter = new MatchingPairsAdapter(false, this);
        RecyclerView.LayoutManager rightLayoutManager = new LinearLayoutManager(getApplicationContext());
        rightRecyclerView.setLayoutManager(rightLayoutManager);
        rightRecyclerView.setAdapter(rightAdapter);
    }

    private void setContent() {
        titleTextView.setText(ContentManager.getCurrentItem().getQuestion().getTitle());
        descriptionTextView.setText(ContentManager.getCurrentItem().getQuestion().getDescription());
    }

    @Override
    public void disableItem(boolean isLeft, int pairId) {
        ((MatchingPairs) ContentManager.getCurrentItem().getQuestion()).getPairItemByPairId(isLeft, pairId).setActive(false);

        if (isLeft) {
            leftAdapter.notifyDataSetChanged();
        } else {
            rightAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void finished(boolean isCorrect) {
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