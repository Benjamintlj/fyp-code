package com.example.fyp_fontend.activity.login_flow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.network.CognitoNetwork;
import com.example.fyp_fontend.network.callback.ResendCodeCallback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class SendVerificationCodeActivity extends AppCompatActivity {

    TextInputEditText usernameTextInputEditText;
    MaterialButton sendEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_verification_code);

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
        sendEmailButton.setOnClickListener(view -> {
            String username = Objects.requireNonNull(usernameTextInputEditText.getText()).toString();

            if (username.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter your username.", Toast.LENGTH_SHORT).show();
                return;
            }

            CognitoNetwork.getInstance().resendCode(username, getApplicationContext(), new ResendCodeCallback() {
                @Override
                public void onResendCodeSuccess() {
                    Intent intent = new Intent(getApplicationContext(), VerifyEmailActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }

                @Override
                public void onResendCodeFailure(CognitoNetwork.Responses response) {
                    switch (response) {
                        case RATE_LIMIT_EXCEEDED:
                            Toast.makeText(getApplicationContext(), "Too many attempts made, try again later", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            });
        });
    }
}