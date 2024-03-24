package com.example.fyp_fontend.network.callback;

import com.example.fyp_fontend.model.content_selection.LessonModel;
import com.example.fyp_fontend.model.content_selection.SpacedRepetition;

import java.util.List;

public interface LessonModelsCallback {
    void onSuccess(List<LessonModel> lessonModelList);
    void onFailure();
}
