package com.example.fyp_fontend.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.model.FeedItemModel;
import com.example.fyp_fontend.model.Question.Acknowledge;
import com.example.fyp_fontend.model.Question.Question;
import com.example.fyp_fontend.network.S3Handler;
import com.example.fyp_fontend.view.question.AcknowledgeView;
import com.example.fyp_fontend.view.question.BaseQuestionView;

import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ContentAdapter";
    private List<FeedItemModel> feedItemsList;
    private Context context;
    private String lessonUrl;

    public ContentAdapter(List<FeedItemModel> feedItemsList, String lessonUrl, Context context) {
        this.feedItemsList = feedItemsList;
        this.context = context;
        this.lessonUrl = lessonUrl;
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
        } else if (viewType == FeedItemModel.ItemType.ACKNOWLEDGE.ordinal()) {
            AcknowledgeView acknowledgeView = new AcknowledgeView(parent.getContext());
            acknowledgeView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return new QuestionViewHolder(acknowledgeView);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_video, parent, false);
            return new VideoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FeedItemModel item = feedItemsList.get(position);

        if (item.itemType == FeedItemModel.ItemType.VIDEO) {
            // Video stuff
        } else if (item.itemType == FeedItemModel.ItemType.ACKNOWLEDGE) {
            ((QuestionViewHolder) holder).baseQuestionView.setQuestion(item.getQuestion());
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

        BaseQuestionView baseQuestionView;

        public QuestionViewHolder(@NonNull BaseQuestionView itemView) {
            super(itemView);
            this.baseQuestionView = itemView;
        }
    }
}
