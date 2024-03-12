package com.example.fyp_fontend.view.question;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.model.Question.Question;

public class AcknowledgeView extends BaseQuestionView {

    TextView titleTextView;

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
    }

    @Override
    public void setQuestion(Question question) {
        titleTextView.setText(question.getTitle());
    }
}
