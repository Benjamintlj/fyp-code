package com.example.fyp_fontend.network.callback;

import com.example.fyp_fontend.network.CognitoNetwork;

public interface ResendCodeCallback {
    void onResendCodeSuccess();
    void onResendCodeFailure(CognitoNetwork.Responses response);
}
