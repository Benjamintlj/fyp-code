package com.example.fyp_fontend.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.activity.lesson.LessonActivity;
import com.example.fyp_fontend.model.LessonModel;

import java.util.List;

public class LessonSelectionAdapter extends RecyclerView.Adapter<LessonSelectionAdapter.LessonSelectionViewHolder> {

    private List<LessonModel> lessonModelList;
    private Context context;

    public LessonSelectionAdapter(List<LessonModel> lessonModelList, Context context) {
        this.lessonModelList = lessonModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public LessonSelectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_lesson, parent, false);
        return new LessonSelectionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonSelectionViewHolder holder, int position) {
        LessonModel lessonModel = lessonModelList.get(position);
        holder.subjectTextView.setText(lessonModel.getTitle());
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, LessonActivity.class);
            intent.putExtra("s3Url", lessonModelList.get(position).getS3Url());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lessonModelList.size();
    }

    public void setLessonModelList(List<LessonModel> lessonModelList) {
        this.lessonModelList = lessonModelList;
        this.notifyDataSetChanged();
    }

    public static class LessonSelectionViewHolder extends RecyclerView.ViewHolder {
        public TextView subjectTextView;

        public LessonSelectionViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectTextView = itemView.findViewById(R.id.subjectTextView);
        }
    }
}
