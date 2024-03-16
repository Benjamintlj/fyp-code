package com.example.fyp_fontend.activity.lesson;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.model.Question.SingleWord;
import com.example.fyp_fontend.utils.ContentManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class QuestionSingleWordActivity extends AppCompatActivity {
    private static final String TAG = "SingleWordQuestionActivity";
    TextView titleTextView, descriptionTextView;
    TextInputEditText answerTextInputEditText;
    Button button;
    SingleWord singleWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_single_word);

        initView();
        getQuestion();
        initListener();
    }

    private void initView() {
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        button = findViewById(R.id.button);
        answerTextInputEditText = findViewById(R.id.answerTextInputEditText);
    }

    private void getQuestion() {
        Log.d(TAG, "getQuestion: " + ContentManager.getCurrentItem().itemType.toString());
        singleWord = (SingleWord) ContentManager.getCurrentItem().getQuestion();
        if (null != singleWord) {
            titleTextView.setText(singleWord.getTitle());
            descriptionTextView.setText(singleWord.getDescription());
        } else {
            Log.e(TAG, "getQuestion: was passed a non-singleWord question.");
            Toast.makeText(getApplicationContext(), "Something was wrong with the lessons contents", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initListener() {
        button.setOnClickListener(view -> {
            if (answerTextInputEditText.getText().length() == 0) {
                Toast.makeText(getApplicationContext(), R.string.please_answer_the_question, Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getApplicationContext(), QuestionResultActivity.class);

            if (Objects.equals(Objects.requireNonNull(answerTextInputEditText.getText()).toString(), singleWord.getAnswer())) {
                intent.putExtra(QuestionResultActivity.ARG_IS_CORRECT, true);
                intent.putExtra(QuestionResultActivity.ARG_INCREASE_SCORE, ContentManager.getCurrentItem().getQuestion().getScore());
            } else {
                intent.putExtra(QuestionResultActivity.ARG_IS_CORRECT, false);
            }

            startActivity(intent);
            finish();
        });
    }
}