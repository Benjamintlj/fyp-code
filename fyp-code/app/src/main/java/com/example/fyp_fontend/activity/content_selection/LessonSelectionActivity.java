package com.example.fyp_fontend.activity.content_selection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.adapter.TopicAdapter;
import com.example.fyp_fontend.model.content_selection.TopicModel;
import com.example.fyp_fontend.network.S3Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LessonSelectionActivity extends AppCompatActivity {
    private static final String TAG = "LessonSelectionActivity";
    private RecyclerView lessonsRecyclerView;
    private TopicAdapter lessonSelectionAdapter;
    private String subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subject = getIntent().getStringExtra("subject");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadingScreen();
        getLessons();
    }

    private void displayContent(List<TopicModel> topicModelList) {
        setContentView(R.layout.activity_lesson_selection);
        initViews();
        initRecyclerView(topicModelList);
    }

    private void loadingScreen() {
        setContentView(R.layout.splash_loading);
        TextView titleTextView = findViewById(R.id.loadingTitleTextView);
        titleTextView.setText(R.string.loading_lessons);
    }

    private void initViews() {
        lessonsRecyclerView = findViewById(R.id.lessonsRecyclerView);
    }

    private void initRecyclerView(List<TopicModel> topicModelList) {
        lessonSelectionAdapter = new TopicAdapter(new ArrayList<>(), getApplicationContext(), LessonSelectionActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        lessonsRecyclerView.setLayoutManager(layoutManager);
        lessonsRecyclerView.setAdapter(lessonSelectionAdapter);

        lessonSelectionAdapter.setTopicModelList(topicModelList);
    }

    private void getLessons() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<TopicModel> topicModelList = S3Handler.getInstance(getApplicationContext()).listS3DirectoryStructure(subject);
            handler.post(() -> {
                displayContent(topicModelList);
            });
        });
    }
}