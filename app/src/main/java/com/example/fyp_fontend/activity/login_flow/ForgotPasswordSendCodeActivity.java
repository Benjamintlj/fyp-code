package com.example.fyp_fontend.activity.login_flow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.example.fyp_fontend.R;
import com.example.fyp_fontend.network.CognitoNetwork;
import com.example.fyp_fontend.network.callback.StartForgotPasswordCallback;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class ForgotPasswordSendCodeActivity extends AppCompatActivity {

    TextInputEditText usernameTextInputEditText;
    Button sendEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_send_code);

        initViews();
        initListeners();

        String username = getIntent().getStringExtra("username");
        if (username != null && !username.isEmpty()) {
            usernameTextInputEditText.setText(username);
        }
    }

    private void initViews() {
        usernameTextInputEditText = findViewById(R.id.usernameTextInputEditText);
        sendEmailButton = findViewById(R.id.sendEmailButton);
    }

    private void initListeners() {
        sendEmailButton.setOnClickListener(this::sendEmail);
    }

    private void sendEmail(View view) {
        String username = Objects.requireNonNull(usernameTextInputEditText.getText()).toString();

        if (username.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter your username.", Toast.LENGTH_SHORT).show();
            return;
        }

        CognitoNetwork.getInstance().sendForgotPasswordEmail(username, getApplicationContext(), new StartForgotPasswordCallback() {
            @Override
            public void onCodeSent() {
                Toast.makeText(getApplicationContext(), "Sent.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), ForgotPasswordSetNewPasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }

            @Override
            public void onCodeDeliveryDetails(CognitoUserCodeDeliveryDetails deliveryDetails) {
                String destination = deliveryDetails.getDestination();
                Toast.makeText(getApplicationContext(), "If your account exists an email has been sent to " + destination + ".", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), ForgotPasswordSetNewPasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }

            @Override
            public void onFailure(CognitoNetwork.Responses response) {

                switch (response) {
                    case RATE_LIMIT_EXCEEDED:
                        Toast.makeText(getApplicationContext(), "Too many attempts, try again later.", Toast.LENGTH_SHORT).show();
                        break;
                    case INVALID_PARAMETER:
                        Toast.makeText(getApplicationContext(), "Value passed is invalid.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Failed, please try again.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}