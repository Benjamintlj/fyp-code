package com.example.fyp_fontend.network.callback;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.example.fyp_fontend.network.CognitoNetwork;

public interface StartForgotPasswordCallback {
    void onCodeSent();
    void onCodeDeliveryDetails(CognitoUserCodeDeliveryDetails deliveryDetails);
    void onFailure(CognitoNetwork.Responses responses);
}
