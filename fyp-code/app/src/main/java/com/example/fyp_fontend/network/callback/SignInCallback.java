package com.example.fyp_fontend.network.callback;

import com.example.fyp_fontend.network.CognitoNetwork;

public interface SignInCallback {
    void onSignInSuccess(Boolean isUserSignedIn);
    void onSignInFailure(CognitoNetwork.Responses response);
}
