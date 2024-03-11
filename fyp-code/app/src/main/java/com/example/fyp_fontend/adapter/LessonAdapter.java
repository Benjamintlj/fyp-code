package com.example.fyp_fontend.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.activity.lesson.LessonActivity;
import com.example.fyp_fontend.model.LessonModel;

import java.util.List;
import java.util.StringTokenizer;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {
    private Context context;
    private List<LessonModel> lessonModelList;

    public LessonAdapter(Context context, List<LessonModel> lessonModelList) {
        this.context = context;
        this.lessonModelList = lessonModelList;
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
            Intent intent = new Intent(context, LessonActivity.class);
            intent.putExtra("url", lessonModel.getS3Url());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
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
            lessonTextView = itemView.findViewById(R.id.lessonTextView);
        }
    }
}
