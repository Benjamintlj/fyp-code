package com.example.fyp_fontend.activity.content_selection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.adapter.LessonSelectionAdapter;
import com.example.fyp_fontend.model.LessonModel;

import java.util.ArrayList;
import java.util.List;

public class LessonSelectionActivity extends AppCompatActivity {

    private RecyclerView lessonsRecyclerView;
    private LessonSelectionAdapter lessonSelectionAdapter;
    private List<LessonModel> lessonModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_selection);

        lessonModelList = new ArrayList<>();

        initViews();
        initRecyclerView();
    }

    private void initViews() {
        lessonsRecyclerView = findViewById(R.id.lessonsRecyclerView);

    }

    private void initRecyclerView() {
        lessonSelectionAdapter = new LessonSelectionAdapter(lessonModelList, getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        lessonsRecyclerView.setLayoutManager(layoutManager);
        lessonsRecyclerView.setAdapter(lessonSelectionAdapter);

        getLessons();
    }

    private void getLessons() {
        // TODO: data should be added from the cloud
        lessonModelList.add(new LessonModel("Types of Cells", "url"));
        lessonModelList.add(new LessonModel("Properties of Prokaryotes", "url"));
        lessonModelList.add(new LessonModel("Standard Form", "url"));

        lessonSelectionAdapter.setLessonModelList(lessonModelList);
    }
}