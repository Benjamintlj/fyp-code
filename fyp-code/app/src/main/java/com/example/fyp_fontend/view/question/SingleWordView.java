package com.example.fyp_fontend.view.question;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.fyp_fontend.Interfaces.Question;
import com.example.fyp_fontend.R;
import com.example.fyp_fontend.model.Question.Acknowledge;
import com.example.fyp_fontend.model.Question.QuestionCompleteListener;
import com.example.fyp_fontend.model.Question.SingleWord;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class SingleWordView extends BaseQuestionView {

    private static final String TAG = "AcknowledgeView";
    private QuestionCompleteListener questionCompleteListener;
    TextView titleTextView, descriptionTextView;
    TextInputEditText answerTextInputEditText;
    Button button;
    SingleWord singleWord;
    Context context;

    public SingleWordView(Context context) {
        super(context);
        initView(context);
    }

    public SingleWordView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_question_single_word, this, true);
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        button = findViewById(R.id.button);
        answerTextInputEditText = findViewById(R.id.answerTextInputEditText);
        this.context = context;
    }

    @Override
    public void setQuestion(Question question) {
        if (question instanceof SingleWord) {
            singleWord = (SingleWord) question;
            titleTextView.setText(singleWord.getTitle());
            descriptionTextView.setText(singleWord.getDescription());
        } else {
            Log.e(TAG, "setQuestion: was passed a non-singleWord question.");
        }
    }

    public void setQuestionCompleteListener(@NonNull QuestionCompleteListener listener) {
        this.questionCompleteListener = listener;
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + answerTextInputEditText.getText());
                if (Objects.equals(Objects.requireNonNull(answerTextInputEditText.getText()).toString(), singleWord.getAnswer())) {
                    setQuestionEnabled(false);
                    questionCompleteListener.onQuestionComplete(true, null);
                } else if (answerTextInputEditText.getText().length() == 0) {
                    Toast.makeText(context, R.string.please_answer_the_question, Toast.LENGTH_SHORT).show();
                } else {
                    setQuestionEnabled(false);
                    questionCompleteListener.onQuestionComplete(false, singleWord.getExplanation());
                }
            }
        });
    }

    private void setQuestionEnabled(boolean isEnabled) {
        answerTextInputEditText.setEnabled(isEnabled);
        button.setEnabled(isEnabled);
    }
}
