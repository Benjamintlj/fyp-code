package com.example.fyp_fontend.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.model.content_selection.TopicModel;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    private List<TopicModel> topicModelList;
    private Context context;
    private Activity activity;

    public TopicAdapter(List<TopicModel> topicModelList, Context context, Activity activity) {
        this.topicModelList = topicModelList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_topic, parent, false);
        return new TopicViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        TopicModel topicModel = topicModelList.get(position);
        holder.topicNameTextView.setText(topicModel.getName());

        SubtopicAdapter subtopicAdapter = new SubtopicAdapter(context, activity, topicModel.getSubTopicModelList());
        holder.subTopicsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.subTopicsRecyclerView.setAdapter(subtopicAdapter);
    }

    @Override
    public int getItemCount() {
        return topicModelList.size();
    }

    public void setTopicModelList(List<TopicModel> topicModelList) {
        this.topicModelList = topicModelList;
        this.notifyDataSetChanged();
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView topicNameTextView;
        RecyclerView subTopicsRecyclerView;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            topicNameTextView = itemView.findViewById(R.id.topicNameTextView);
            subTopicsRecyclerView = itemView.findViewById(R.id.subTopicsRecyclerView);
        }
    }
}
