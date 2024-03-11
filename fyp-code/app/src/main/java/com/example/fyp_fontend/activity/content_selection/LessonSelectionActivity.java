package com.example.fyp_fontend.activity.content_selection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.adapter.TopicAdapter;
import com.example.fyp_fontend.model.LessonModel;
import com.example.fyp_fontend.model.SubtopicModel;
import com.example.fyp_fontend.model.TopicModel;

import java.util.ArrayList;
import java.util.List;

public class LessonSelectionActivity extends AppCompatActivity {

    private RecyclerView lessonsRecyclerView;
    private TopicAdapter lessonSelectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_selection);

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
        // TODO data should be added from the cloud
        List<LessonModel> lessonModelList = new ArrayList<>();
        lessonModelList.add(new LessonModel("Types of cell", "url"));
        lessonModelList.add(new LessonModel("Variations of cells", "url"));
        lessonModelList.add(new LessonModel("DNA", "url"));

        List<SubtopicModel> subTopicModelList = new ArrayList<>();
        subTopicModelList.add(new SubtopicModel("What is in a cell?", lessonModelList));
        subTopicModelList.add(new SubtopicModel("Evolution", new ArrayList<>()));
        subTopicModelList.add(new SubtopicModel("Other biology things", new ArrayList<>()));

        List<TopicModel> topicModelList = new ArrayList<>();
        topicModelList.add(new TopicModel("Cell Biology", subTopicModelList));
        topicModelList.add(new TopicModel("Other cell stuff", new ArrayList<>()));
        topicModelList.add(new TopicModel("Animals", new ArrayList<>()));

        lessonSelectionAdapter.setTopicModelList(topicModelList);
    }
}