package com.example.fyp_fontend.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.model.content_selection.SubtopicModel;

import java.util.List;

public class SubtopicAdapter extends RecyclerView.Adapter<SubtopicAdapter.SubtopicViewHolder> {

    private Context context;
    private List<SubtopicModel> subtopicModelList;
    private Activity activity;

    public SubtopicAdapter(Context context, Activity activity, List<SubtopicModel> subtopicModelList) {
        this.context = context;
        this.subtopicModelList = subtopicModelList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public SubtopicAdapter.SubtopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_subtopic, parent, false);
        return new SubtopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubtopicAdapter.SubtopicViewHolder holder, int position) {
        SubtopicModel subtopicModel = subtopicModelList.get(position);
        holder.subtopicNameTextView.setText(subtopicModel.getName());

        LessonAdapter lessonAdapter = new LessonAdapter(context, activity, subtopicModel.getLessonModelList());
        holder.lessonsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.lessonsRecyclerView.setAdapter(lessonAdapter);

        holder.subtopicCardView.setOnClickListener(view -> {
            if (holder.lessonsRecyclerView.getVisibility() == View.GONE) {
                holder.lessonsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                holder.lessonsRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subtopicModelList.size();
    }

    public static class SubtopicViewHolder extends RecyclerView.ViewHolder {
        TextView subtopicNameTextView;
        CardView subtopicCardView;
        RecyclerView lessonsRecyclerView;

        public SubtopicViewHolder(@NonNull View itemView) {
            super(itemView);
            subtopicNameTextView = itemView.findViewById(R.id.subtopicNameTextView);
            subtopicCardView = itemView.findViewById(R.id.subtopicCardView);
            lessonsRecyclerView = itemView.findViewById(R.id.lessonsRecyclerView);
        }
    }
}
