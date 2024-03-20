package com.example.fyp_fontend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.media3.common.C;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.model.LeaderboardModel;
import com.example.fyp_fontend.network.CognitoNetwork;

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

        int numberOfUsersToProgress = 3;
        int numberOfUsersToRelegate = 2;

        if (position < numberOfUsersToProgress) {
            holder.userCardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.highlight1CardBg));
            holder.userCardView.setOnClickListener(view -> {
                Toast.makeText(context.getApplicationContext(), R.string.top_3_players, Toast.LENGTH_LONG).show();
            });
        }

        if (CognitoNetwork.getInstance().getCurrentUsername(context.getApplicationContext()).equals(item.getUsername())) {
            holder.userCardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.highlight2CardBg));
        }

        if (position > (leaderboardModelList.size() - (1 + numberOfUsersToRelegate))
                && leaderboardModelList.size() > (numberOfUsersToProgress + numberOfUsersToRelegate)) {

            holder.userCardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.failureRed));

            holder.userCardView.setOnClickListener(view -> {
                Toast.makeText(context.getApplicationContext(), R.string.players_at_risk, Toast.LENGTH_LONG).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return leaderboardModelList.size();
    }

    public static class LeaderboardViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView, scoreTextView;
        ImageView gemImageView;
        CardView userCardView;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
            gemImageView = itemView.findViewById(R.id.gemImageView);
            userCardView = itemView.findViewById(R.id.userCardView);
        }
    }
}
