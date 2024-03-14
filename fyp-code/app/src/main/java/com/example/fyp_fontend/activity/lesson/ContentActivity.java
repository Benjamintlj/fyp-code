package com.example.fyp_fontend.activity.lesson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.Utils.RandomSelector;
import com.example.fyp_fontend.adapter.ContentAdapter;
import com.example.fyp_fontend.model.FeedItemModel;
import com.example.fyp_fontend.network.S3Handler;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ContentActivity extends AppCompatActivity implements ContentAdapter.ContentAdapterInterface {
    private static final String TAG = "LessonActivity";

    ViewPager2 viewPager;
    ContentAdapter contentAdapter;
    String objectKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        objectKey = getIntent().getStringExtra("objectKey");
        initViews();

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<FeedItemModel> feedItemsList = S3Handler.getInstance(getApplicationContext()).getLesson(objectKey);
            handler.post(() -> {
                contentAdapter = new ContentAdapter(feedItemsList, this);
                initRecyclerView();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (contentAdapter != null) {
            contentAdapter.releaseAllPlayers();
        }
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
    }

    private void initRecyclerView() {
        viewPager.setAdapter(contentAdapter);
    }

    @Override
    public void onQuestionComplete(Boolean isCorrect, String description) {
        int currentItem = viewPager.getCurrentItem();

        if (isCorrect == null) {
            Log.d(TAG, "onQuestionComplete: null");
            nextPage(currentItem);
            return;
        }

        final Dialog dialog = new Dialog(ContentActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_question_complete);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dialog.findViewById(R.id.continueButton).setOnClickListener(view -> {
            dialog.dismiss();
            nextPage(currentItem);
        });

        TextView titleViewText = dialog.findViewById(R.id.titleViewText);
        TextView descriptionTextView = dialog.findViewById(R.id.descriptionTextView);

        if (isCorrect) {
            Log.d(TAG, "onQuestionComplete: answer was correct");

            titleViewText.setText(RandomSelector.selectRandomString(new String[]{
                    getString(R.string.congratulations_1),
                    getString(R.string.congratulations_2),
                    getString(R.string.congratulations_3),
                    getString(R.string.congratulations_4),
                    getString(R.string.congratulations_5),
                    getString(R.string.congratulations_6),
                    getString(R.string.congratulations_7),
                    getString(R.string.congratulations_8),
                    getString(R.string.congratulations_9)
            }));

        } else {
            Log.d(TAG, "onQuestionComplete: answer was incorrect");

            titleViewText.setText(RandomSelector.selectRandomString(new String[]{
                    getString(R.string.failure_1),
                    getString(R.string.failure_2),
                    getString(R.string.failure_3),
                    getString(R.string.failure_4),
                    getString(R.string.failure_5),
                    getString(R.string.failure_6),
                    getString(R.string.failure_7),
                    getString(R.string.failure_8)
            }));
        }

        descriptionTextView.setText(description);
        dialog.show();
    }

    @Override
    public void onVideoComplete() {
        nextPage(viewPager.getCurrentItem());
    }

    private void nextPage(int currentItem) {
        contentAdapter.showNextItemsUpToNextQuestion(currentItem);
        if (currentItem < contentAdapter.getItemCount() - 1) {
            viewPager.setCurrentItem(currentItem + 1, true);
        }
        Log.d(TAG, "nextPage: Ben:", new Exception("Ben Stack trace"));
    }
}