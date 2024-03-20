package com.example.fyp_fontend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media3.common.C;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.model.LeaderboardModel;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {
    private static final String TAG = "LeaderboardAdapter";
    private List<LeaderboardModel> leaderboardModelList;
    private Context context;

    public LeaderboardAdapter(Context context, List<LeaderboardModel> leaderboardModelList) {
        this.leaderboardModelList = leaderboardModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_leaderboard, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        LeaderboardModel item = leaderboardModelList.get(position);
        holder.usernameTextView.setText(item.getUsername());
        holder.scoreTextView.setText(String.valueOf(item.getScore()));
    }

    @Override
    public int getItemCount() {
        return leaderboardModelList.size();
    }

    public static class LeaderboardViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView, scoreTextView;
        ImageView gemImageView;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
            gemImageView = itemView.findViewById(R.id.gemImageView);
        }
    }
}
