package com.example.fyp_fontend.view.question;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.model.Question.Acknowledge;
import com.example.fyp_fontend.Interfaces.Question;
import com.example.fyp_fontend.model.Question.QuestionCompleteListener;

public class AcknowledgeView extends BaseQuestionView {

    private static final String TAG = "AcknowledgeView";
    private QuestionCompleteListener questionCompleteListener;
    TextView titleTextView, descriptionTextView;
    Button button;

    public AcknowledgeView(Context context) {
        super(context);
        initView(context);
    }

    public AcknowledgeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_question_acknowledge, this, true);
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        button = findViewById(R.id.button);
    }

    @Override
    public void setQuestion(Question question) {
        if (question instanceof Acknowledge) {
            Acknowledge acknowledge = (Acknowledge) question;
            titleTextView.setText(acknowledge.getTitle());
            descriptionTextView.setText(acknowledge.getDescription());
            button.setText(acknowledge.getButtonText());
        } else {
            Log.e(TAG, "setQuestion: was passed a non-acknowledge question.");
        }
    }

    public void setQuestionCompleteListener(QuestionCompleteListener listener) {
        this.questionCompleteListener = listener;
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(questionCompleteListener != null) {
                    questionCompleteListener.onQuestionComplete();
                }
            }
        });
    }
}
