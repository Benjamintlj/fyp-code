package com.example.fyp_fontend.activity.content_selection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.adapter.LessonAdapter;
import com.example.fyp_fontend.adapter.TopicAdapter;
import com.example.fyp_fontend.model.content_selection.LessonModel;
import com.example.fyp_fontend.model.content_selection.TopicModel;
import com.example.fyp_fontend.network.S3Handler;
import com.example.fyp_fontend.network.SpacedRepetitionHandler;
import com.example.fyp_fontend.utils.NavbarHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RepetitionActivity extends AppCompatActivity {
    private static final String TAG = "RepetitionActivity";

    private RecyclerView lessonsRecyclerView;
    private LessonAdapter lessonSelectionAdapter;
    private ImageView nothingToDoImageView;
    private TextView nothingToDoTextView, titleTextView;

    @Override
    protected void onResume() {
        super.onResume();
        loadingScreen();
        getLessons();
    }

    private void loadingScreen() {
        setContentView(R.layout.splash_loading);
        TextView titleTextView = findViewById(R.id.loadingTitleTextView);
        titleTextView.setText(R.string.loading_lessons);
    }

    private void displayContent(List<LessonModel> lessonModelList) {
        setContentView(R.layout.activity_repetition);
        initViews(lessonModelList.size() > 0);
        initNavbar();
        initRecyclerView(lessonModelList);
    }

    private void initViews(boolean hasLessons) {
        lessonsRecyclerView = findViewById(R.id.lessonsRecyclerView);
        nothingToDoImageView = findViewById(R.id.nothingToDoImageView);
        nothingToDoTextView = findViewById(R.id.nothingToDoTextView);
        titleTextView = findViewById(R.id.titleTextView);

        if (hasLessons) {
            nothingToDoImageView.setVisibility(View.GONE);
            nothingToDoTextView.setVisibility(View.GONE);
        } else {
            lessonsRecyclerView.setVisibility(View.GONE);
            titleTextView.setVisibility(View.GONE);
        }
    }

    private void initNavbar() {
        BottomNavigationView navbarBottomNavigationView = findViewById(R.id.navbarBottomNavigationView);
        NavbarHandler.initNavbar(navbarBottomNavigationView, this);
    }

    private void initRecyclerView(List<LessonModel> lessonModelList) {
        lessonSelectionAdapter = new LessonAdapter(getApplicationContext(), this, lessonModelList);
        lessonsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        lessonsRecyclerView.setAdapter(lessonSelectionAdapter);
    }

    private void getLessons() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                List<LessonModel> lessonModelList = SpacedRepetitionHandler.getAllSpacedRepetitionLessonsForUser(
                        getApplicationContext()
                );

                handler.post(() -> {
                    displayContent(lessonModelList);
                });
            } catch (IOException | JSONException e) {
                Log.e(TAG, "getLessons: ", e);
            }
        });
    }
}