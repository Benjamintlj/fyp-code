package com.example.fyp_fontend.activity.content_selection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

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
        setContentView(R.layout.activity_lesson_selection);

        subject = getIntent().getStringExtra("subject");

        initViews();
        initRecyclerView();
    }

    private void initViews() {
        lessonsRecyclerView = findViewById(R.id.lessonsRecyclerView);
    }

    private void initRecyclerView() {
        lessonSelectionAdapter = new TopicAdapter(new ArrayList<>(), getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        lessonsRecyclerView.setLayoutManager(layoutManager);
        lessonsRecyclerView.setAdapter(lessonSelectionAdapter);

        getLessons();
    }

    private void getLessons() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<TopicModel> topicModelList = S3Handler.getInstance(getApplicationContext()).listS3DirectoryStructure(subject);
            handler.post(() -> {
                lessonSelectionAdapter.setTopicModelList(topicModelList);
            });
        });
    }
}