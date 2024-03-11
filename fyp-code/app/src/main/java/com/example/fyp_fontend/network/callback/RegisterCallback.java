package com.example.fyp_fontend.network.callback;

import com.example.fyp_fontend.network.CognitoNetwork;

public interface RegisterCallback {
    void onRegisterSuccess();
    void onRegisterFailure(CognitoNetwork.Responses response);
}
