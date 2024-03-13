package com.example.fyp_fontend.view.question;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.example.fyp_fontend.Interfaces.Question;
import com.example.fyp_fontend.model.Question.QuestionCompleteListener;

public abstract class BaseQuestionView extends LinearLayout {

    public BaseQuestionView(Context context) {
        super(context);
    }

    public BaseQuestionView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public abstract void setQuestion(Question question);
    public abstract void setQuestionCompleteListener(QuestionCompleteListener listener);
}
