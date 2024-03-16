package com.example.fyp_fontend.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.model.Question.MultipleChoice;
import com.example.fyp_fontend.utils.ContentManager;

import java.util.Arrays;
import java.util.List;

public class MultipleChoiceAdapter extends RecyclerView.Adapter<MultipleChoiceAdapter.OptionViewHolder> {

    private MultipleChoice multipleChoice;
    private Context context;

    public interface MultipleChoiceAdapterInterface {
        public void answerListener(boolean isCorrect);
    }

    private MultipleChoiceAdapterInterface multipleChoiceAdapterInterface;

    public MultipleChoiceAdapter(Context context, MultipleChoiceAdapterInterface multipleChoiceAdapterInterface) {
        this.context = context;
        this.multipleChoiceAdapterInterface = multipleChoiceAdapterInterface;
        this.multipleChoice = (MultipleChoice) ContentManager.getCurrentItem().getQuestion();
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_multiple_choice_option, parent, false);
        return new OptionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
        holder.optionTextView.setText(multipleChoice.getOptions().get(position));

        holder.optionCardView.setOnClickListener(view -> {
            multipleChoiceAdapterInterface.answerListener(position == multipleChoice.getAnswer());
        });
    }

    @Override
    public int getItemCount() {
        return multipleChoice.getOptions().size();
    }

    public static class OptionViewHolder extends RecyclerView.ViewHolder {
        TextView optionTextView;
        CardView optionCardView;

        public OptionViewHolder(@NonNull View itemView) {
            super(itemView);
            optionTextView = itemView.findViewById(R.id.optionTextView);
            optionCardView = itemView.findViewById(R.id.optionCardView);
        }
    }
}
