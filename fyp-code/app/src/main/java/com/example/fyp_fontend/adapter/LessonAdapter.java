package com.example.fyp_fontend.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.model.FeedItemModel;
import com.example.fyp_fontend.model.content_selection.LessonModel;
import com.example.fyp_fontend.network.S3Handler;
import com.example.fyp_fontend.utils.ContentManager;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {
    private static final String TAG = "LessonAdapter";
    private Context context;
    private List<LessonModel> lessonModelList;
    private Activity activity;

    public LessonAdapter(Context context, Activity activity, List<LessonModel> lessonModelList) {
        this.context = context;
        this.lessonModelList = lessonModelList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public LessonAdapter.LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonAdapter.LessonViewHolder holder, int position) {
        LessonModel lessonModel = lessonModelList.get(position);
        holder.lessonTextView.setText(lessonModel.getTitle());

        holder.lessonCardView.setOnClickListener(view -> {
            Executor executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                List<FeedItemModel> feedItemsList = S3Handler.getInstance(context).getLesson(lessonModel.getS3Url());
                handler.post(() -> {
                    ContentManager.init(feedItemsList);
                    ContentManager.nextItem(activity);
                });
            });
        });
    }

    @Override
    public int getItemCount() {
        return lessonModelList.size();
    }

    public static class LessonViewHolder extends RecyclerView.ViewHolder {
        CardView lessonCardView;
        TextView lessonTextView;
        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);

            lessonCardView = itemView.findViewById(R.id.lessonCardView);
            lessonTextView = itemView.findViewById(R.id.usernameTextView);
        }
    }
}
