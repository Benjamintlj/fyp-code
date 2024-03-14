package com.example.fyp_fontend.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.model.FeedItemModel;
import com.example.fyp_fontend.view.question.AcknowledgeView;
import com.example.fyp_fontend.view.question.BaseQuestionView;
import com.example.fyp_fontend.view.question.SingleWordView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface ContentAdapterInterface {
        void onQuestionComplete(Boolean isCorrect, String description);
        void onVideoComplete();
    }

    private static final String TAG = "ContentAdapter";
    private ContentAdapterInterface contentAdapterInterface;
    private List<FeedItemModel> feedItemsList;
    private List<FeedItemModel> visableFeedItemsList = new ArrayList<>();
    private List<ExoPlayer> exoPlayers = new ArrayList<>();


    public ContentAdapter(List<FeedItemModel> feedItemsList, @NonNull ContentAdapterInterface contentAdapterInterface) {
        this.feedItemsList = feedItemsList;
        this.contentAdapterInterface = contentAdapterInterface;
        this.visableFeedItemsList.add(feedItemsList.get(0));
        if (feedItemsList.get(0).itemType == FeedItemModel.ItemType.VIDEO) {
            showNextItemsUpToNextQuestion(0);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return feedItemsList.get(position).itemType.ordinal();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseQuestionView questionView = null;

        switch (FeedItemModel.ItemType.values()[viewType]) {
            case VIDEO:
                View videoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_video, parent, false);
                return new VideoViewHolder(videoView);

            case ACKNOWLEDGE:
                questionView = new AcknowledgeView(parent.getContext());
                break;

            case SINGLE_WORD:
                questionView = new SingleWordView(parent.getContext());
                break;

            default:
                // TODO: this needs to be something different than a video view
                View defaultView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_video, parent, false);
                Log.e(TAG, "onCreateViewHolder: defaulted");
                return new VideoViewHolder(defaultView);
        }

        // Assumes that all non-question views have returned
        questionView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        questionView.setQuestionCompleteListener(contentAdapterInterface);
        return new QuestionViewHolder(questionView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FeedItemModel item = feedItemsList.get(position);

        switch (item.itemType) {
            case VIDEO:
                ((VideoViewHolder) holder).linkVideo(item.getVideoUrl());
                ((VideoViewHolder) holder).initVideoCompleteListener(() -> {
                    contentAdapterInterface.onVideoComplete();
                });
                break;

            case ACKNOWLEDGE:
            case SINGLE_WORD:
                ((QuestionViewHolder) holder).baseQuestionView.setQuestion(item.getQuestion());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return visableFeedItemsList.size();
    }

    public void showNextItemsUpToNextQuestion(int currentQuestionIndex) {
        boolean nextQuestionFound = false;
        for (int i = currentQuestionIndex + 1; i < feedItemsList.size() && !nextQuestionFound; i++) {
            FeedItemModel item = feedItemsList.get(i);
            if (!visableFeedItemsList.contains(item)) {
                visableFeedItemsList.add(item);
                if (item.itemType == FeedItemModel.ItemType.ACKNOWLEDGE
                        || item.itemType == FeedItemModel.ItemType.SINGLE_WORD) {
                    nextQuestionFound = true;
                }
            }
        }
        notifyDataSetChanged();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {

        PlayerView playerView;
        public int exoPlayerPosition;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.playerView);
            exoPlayers.add(new ExoPlayer.Builder(itemView.getContext()).build());
            exoPlayerPosition = exoPlayers.size() - 1;
            playerView.setPlayer(exoPlayers.get(exoPlayerPosition));
        }

        public void linkVideo(URL streamUrl) {
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(streamUrl.toString()));
            exoPlayers.get(exoPlayerPosition).setMediaItem(mediaItem);
            exoPlayers.get(exoPlayerPosition).prepare();
        }

        public void startVideo() {
            exoPlayers.get(exoPlayerPosition).play();
        }

        public void resetVideo() {
            exoPlayers.get(exoPlayerPosition).seekTo(0);
        }

        public void pauseVideo() {
            if (exoPlayers.get(exoPlayerPosition).isPlaying()) {
                exoPlayers.get(exoPlayerPosition).pause();
            }
        }

        public void initVideoCompleteListener(Runnable onVideoComplete) {
            exoPlayers.get(exoPlayerPosition).addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    Player.Listener.super.onPlaybackStateChanged(playbackState);
                    if (playbackState == Player.STATE_ENDED) {
                        onVideoComplete.run();
                    }
                }
            });

            exoPlayers.get(exoPlayerPosition).addListener(new Player.Listener() {
                @Override
                public void onPlayerError(PlaybackException error) {
                    Log.e("VideoViewHolder", "Player error: " + error.getMessage());
                }
            });

        }

        public void releaseExoPlayer() {
            if (exoPlayers.get(exoPlayerPosition) != null) {
                exoPlayers.get(exoPlayerPosition).release();
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof VideoViewHolder) {
            Log.d(TAG, "onViewAttachedToWindow: BEN: " + String.valueOf(((VideoViewHolder) holder).exoPlayerPosition));
            ((VideoViewHolder) holder).startVideo();
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof VideoViewHolder) {
            Log.d(TAG, "onViewDetachedFromWindow: BEN: " + String.valueOf(((VideoViewHolder) holder).exoPlayerPosition));
            ((VideoViewHolder) holder).pauseVideo();
            ((VideoViewHolder) holder).resetVideo();
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof VideoViewHolder) {
            ((VideoViewHolder) holder).releaseExoPlayer();
        }
    }

    public void releaseAllPlayers() {
        for (ExoPlayer exoPlayer : exoPlayers) {
            if (exoPlayer != null) {
                exoPlayer.release();
            }
        }
        exoPlayers.clear();
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder {

        BaseQuestionView baseQuestionView;

        public QuestionViewHolder(@NonNull BaseQuestionView itemView) {
            super(itemView);
            this.baseQuestionView = itemView;
        }
    }
}