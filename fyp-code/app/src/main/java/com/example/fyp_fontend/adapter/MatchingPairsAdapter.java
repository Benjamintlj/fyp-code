package com.example.fyp_fontend.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.model.Question.MatchingPairs;
import com.example.fyp_fontend.model.Question.PairItem;
import com.example.fyp_fontend.utils.ContentManager;

import java.util.List;

public class MatchingPairsAdapter extends RecyclerView.Adapter<MatchingPairsAdapter.ColumnViewHolder> {

    MatchingPairs matchingPairs;
    boolean isLeft;
    int answered;

    public interface MatchingPairsAdapterInterface {
        void disableItem(boolean isLeft, int pairId);
        void finished(boolean isCorrect);
    }

    private MatchingPairsAdapterInterface matchingPairsAdapterInterface;

    public MatchingPairsAdapter(boolean isLeft, MatchingPairsAdapterInterface matchingPairsAdapterInterface) {
        this.isLeft = isLeft;
        matchingPairs = ((MatchingPairs) ContentManager.getCurrentItem().getQuestion());
        this.matchingPairsAdapterInterface = matchingPairsAdapterInterface;
        answered = 0;
    }

    @NonNull
    @Override
    public ColumnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_matching_pairs, parent, false);
        return new ColumnViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ColumnViewHolder holder, int position) {
        PairItem item = matchingPairs.getColumn(isLeft).get(position);

        holder.pairTextView.setText(item.getName());

        if (item.isActive()) {
            holder.pairCardView.setOnClickListener(view -> {
                matchingPairs.setActiveIndex(isLeft, position);

                Boolean match = matchingPairs.checkMatch();

                if (match == null) return;

                if (match) {
                    notifyItemChanged(position);
                    item.setActive(false);
                    matchingPairsAdapterInterface.disableItem(!isLeft ,item.getPairId());
                    answered++;

                    if (answered >= (matchingPairs.getColumn(isLeft).size() - 1)) {
                        matchingPairsAdapterInterface.finished(true);
                    }
                } else {
                    matchingPairsAdapterInterface.finished(false);
                }
            });

        } else {
            holder.pairCardView.setCardBackgroundColor(ContextCompat.getColor(holder.pairCardView.getContext(), R.color.disabledCardBg));
            holder.pairCardView.setOnClickListener(v -> {});
        }
    }

    @Override
    public int getItemCount() {
        return matchingPairs.getColumn(isLeft).size();
    }

    public static class ColumnViewHolder extends RecyclerView.ViewHolder {
        TextView pairTextView;
        CardView pairCardView;

        public ColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            pairTextView = itemView.findViewById(R.id.pairTextView);
            pairCardView = itemView.findViewById(R.id.pairCardView);
        }
    }
}
