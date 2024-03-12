package com.example.fyp_fontend.activity.lesson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.adapter.ContentAdapter;
import com.example.fyp_fontend.model.FeedItemModel;
import com.example.fyp_fontend.model.Question.QuestionCompleteListener;
import com.example.fyp_fontend.network.S3Handler;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ContentActivity extends AppCompatActivity {
    private static final String TAG = "LessonActivity";

    ViewPager2 viewPager;
    ContentAdapter contentAdapter;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        url = getIntent().getStringExtra("url");
        initViews();

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<FeedItemModel> feedItemsList = S3Handler.getInstance(getApplicationContext()).getLesson(url);
            handler.post(() -> {
                Log.d(TAG, "getContent: " + feedItemsList.size());
                contentAdapter = new ContentAdapter(feedItemsList, url, getApplicationContext(), new QuestionCompleteListener() {
                    @Override
                    public void onQuestionComplete() {
                        int currentItem = viewPager.getCurrentItem();
                        if (currentItem < contentAdapter.getItemCount() - 1) {
                            viewPager.setCurrentItem(currentItem + 1, true);
                        }
                    }
                });
                initRecyclerView();
            });
        });
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
    }

    private void initRecyclerView() {
        viewPager.setAdapter(contentAdapter);
    }
}