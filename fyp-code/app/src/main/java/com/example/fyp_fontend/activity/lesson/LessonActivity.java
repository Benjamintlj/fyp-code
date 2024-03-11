package com.example.fyp_fontend.activity.lesson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.adapter.LessonAdapter;
import com.example.fyp_fontend.model.FeedItemModel;

import java.util.ArrayList;
import java.util.List;

public class LessonActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    LessonAdapter lessonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        getContent();
        initViews();
        initRecyclerView();
    }

    private void getContent() {
        List<FeedItemModel> feedItemsList = new ArrayList<>();

        // TODO: should come from cloud
        feedItemsList.add(new FeedItemModel(FeedItemModel.ItemType.VIDEO));
        feedItemsList.add(new FeedItemModel(FeedItemModel.ItemType.QUESTION));
        feedItemsList.add(new FeedItemModel(FeedItemModel.ItemType.VIDEO));
        feedItemsList.add(new FeedItemModel(FeedItemModel.ItemType.QUESTION));

        lessonAdapter = new LessonAdapter(feedItemsList);
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
    }

    private void initRecyclerView() {
        viewPager.setAdapter(lessonAdapter);
    }
}