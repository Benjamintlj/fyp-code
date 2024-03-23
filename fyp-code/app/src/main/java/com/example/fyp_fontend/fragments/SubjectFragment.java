package com.example.fyp_fontend.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.activity.content_selection.LessonSelectionActivity;

import java.util.Objects;

public class SubjectFragment extends Fragment {

    private static final String ARG_SUBJECT_NAME = "subjectName";
    private static final String ARG_BACKGROUND = "background";
    private static final String ARG_PUT_EXTRA_LESSON = "putExtraLesson";
    private String mSubjectName;
    private int mBackgroundId;
    private String mPutExtraLesson;

    TextView subjectTextView;
    ImageButton imageButton;

    public SubjectFragment() {}

    public static SubjectFragment newInstance(String subjectName, @DrawableRes int resId, String putExtraLesson) {
        SubjectFragment fragment = new SubjectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SUBJECT_NAME, subjectName);
        args.putInt(ARG_BACKGROUND, resId);
        args.putString(ARG_PUT_EXTRA_LESSON, putExtraLesson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSubjectName = getArguments().getString(ARG_SUBJECT_NAME);
            mBackgroundId = getArguments().getInt(ARG_BACKGROUND);
            mPutExtraLesson = getArguments().getString(ARG_PUT_EXTRA_LESSON);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subject, container, false);

        initViews(view);
        initListeners();
        subjectTextView.setText(mSubjectName);
        imageButton.setImageResource(mBackgroundId);

        return view;
    }

    private void initViews(View view) {
        subjectTextView = view.findViewById(R.id.subjectTextView);
        imageButton = view.findViewById(R.id.imageButton);
    }

    private void initListeners() {
        imageButton.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), LessonSelectionActivity.class);
            intent.putExtra("subject", mPutExtraLesson);
            intent.putExtra("title", mSubjectName);
            startActivity(intent);
        });
    }
}