package com.example.fyp_fontend.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.model.Stats.UserStats;


public class AchievementFragment extends Fragment {

    private static final String ARG_CURRENT = "current";
    private static final String ARG_BRONZE_RANGE = "bronzeRange";
    private static final String ARG_SILVER_RANGE = "silverRange";
    private static final String ARG_GOLD_RANGE = "goldRange";
    private static final String ARG_BADGE = "badge";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_TITLE = "title";

    int current, bronzeRange, silverRange, goldRange;
    UserStats.Badge badge;
    String description, title;

    ImageView badgeImageView;
    TextView colourTextView, titleTextView, descriptionTextView, progressTextView;
    ProgressBar progressBar;
    

    public AchievementFragment() {}

    public static AchievementFragment newInstance(int current, int bronzeRange, int silverRange, int goldRange, UserStats.Badge badge, String description, String title) {
        AchievementFragment fragment = new AchievementFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_CURRENT, current);
        args.putInt(ARG_BRONZE_RANGE, bronzeRange);
        args.putInt(ARG_SILVER_RANGE, silverRange);
        args.putInt(ARG_GOLD_RANGE, goldRange);
        args.putSerializable(ARG_BADGE, badge);
        args.putString(ARG_DESCRIPTION, description);
        args.putString(ARG_TITLE, title);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            current = getArguments().getInt(ARG_CURRENT);
            bronzeRange = getArguments().getInt(ARG_BRONZE_RANGE);
            silverRange = getArguments().getInt(ARG_SILVER_RANGE);
            goldRange = getArguments().getInt(ARG_GOLD_RANGE);
            badge = (UserStats.Badge) getArguments().getSerializable(ARG_BADGE);
            description = getArguments().getString(ARG_DESCRIPTION);
            title = getArguments().getString(ARG_TITLE);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achevement, container, false);

        initViews(view);
        setContent();

        return view;
    }

    private void setContent() {
        int upperRange = 0;

        switch (badge) {
            case GOLD:
                badgeImageView.setImageResource(R.drawable.gold);
                colourTextView.setText(R.string.gold);
                break;
            case SILVER:
                badgeImageView.setImageResource(R.drawable.silver);
                colourTextView.setText(R.string.silver);
                upperRange = goldRange;
                break;
            case BRONZE:
                badgeImageView.setImageResource(R.drawable.bronze);
                colourTextView.setText(R.string.bronze);
                upperRange = silverRange;
                break;
            default:
                badgeImageView.setImageResource(R.drawable.unknown);
                colourTextView.setText("");
                upperRange = bronzeRange;
                break;
        }

        titleTextView.setText(title);
        descriptionTextView.setText(description);

        if (!badge.equals(UserStats.Badge.GOLD)) {
            progressTextView.setText(current + "/" + upperRange);
            progressBar.setProgress(current);
            progressBar.setMax(current);
        } else {
            progressBar.setVisibility(View.GONE);
            progressTextView.setText(current + "/??");
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) progressTextView.getLayoutParams();
            layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            progressTextView.setLayoutParams(layoutParams);
        }
    }

    private void initViews(View view) {
        badgeImageView = view.findViewById(R.id.badgeImageView);
        colourTextView = view.findViewById(R.id.colourTextView);
        titleTextView = view.findViewById(R.id.titleTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);
        progressTextView = view.findViewById(R.id.progressTextView);
        progressBar = view.findViewById(R.id.progressBar);
    }
}