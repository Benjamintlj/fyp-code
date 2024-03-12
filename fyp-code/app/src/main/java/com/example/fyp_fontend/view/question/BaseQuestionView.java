package com.example.fyp_fontend.view.question;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.example.fyp_fontend.Interfaces.Question;

public abstract class BaseQuestionView extends LinearLayout {

    public BaseQuestionView(Context context) {
        super(context);
    }

    public BaseQuestionView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public abstract void setQuestion(Question question);
}
