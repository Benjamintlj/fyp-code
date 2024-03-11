package com.example.fyp_fontend.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.model.FeedItemModel;

import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FeedItemModel> feedItemsList;

    public ContentAdapter(List<FeedItemModel> feedItemsList) {
        this.feedItemsList = feedItemsList;
    }

    @Override
    public int getItemViewType(int position) {
        return feedItemsList.get(position).itemType.ordinal();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == FeedItemModel.ItemType.VIDEO.ordinal()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_video, parent, false);
            return new VideoViewHolder(view);
        } else {
            // Assumed to be a question
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_question, parent, false);
            return new QuestionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FeedItemModel item = feedItemsList.get(position);

        if (item.itemType == FeedItemModel.ItemType.VIDEO) {
            // Video stuff
        } else {
            // Question stuff
        }
    }

    @Override
    public int getItemCount() {
        return feedItemsList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder {
        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
