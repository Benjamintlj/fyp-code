package com.example.fyp_fontend.activity.login_flow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.activity.MainActivity;
import com.example.fyp_fontend.network.CognitoNetwork;
import com.example.fyp_fontend.network.callback.ResendCodeCallback;
import com.example.fyp_fontend.network.callback.VerifyEmailCallback;
import com.google.android.material.textfield.TextInputEditText;

public class VerifyEmailActivity extends AppCompatActivity {

    static final String TAG = "VerifyEmailActivity";
    Button verifyButton, resendCodeButton;
    TextInputEditText codeTextInputEditText;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        username = getIntent().getStringExtra("username");

        initViews();
        initListeners();
    }

    private void initViews() {
        verifyButton = findViewById(R.id.verifyButton);
        resendCodeButton = findViewById(R.id.resendCodeButton);
        codeTextInputEditText = findViewById(R.id.codeTextInputEditText);
    }

    private void initListeners() {
        verifyButton.setOnClickListener(this::verifyCode);
        resendCodeButton.setOnClickListener(this::resendCode);
    }

    private void resendCode(View view) {
        CognitoNetwork.getInstance().resendCode(username, getApplicationContext(), new ResendCodeCallback() {
            @Override
            public void onResendCodeSuccess() {
                Toast.makeText(getApplicationContext(), "Code sent.", Toast.LENGTH_SHORT).show();
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
    }

    private void verifyCode(View view) {
        String code = String.valueOf(codeTextInputEditText.getText());

        if (code.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a code.", Toast.LENGTH_LONG).show();
            return;
        }

        CognitoNetwork.getInstance().verifyEmailConfirmationCode(username, code, getApplicationContext(), new VerifyEmailCallback() {
            @Override
            public void onVerifySuccess() {
                Log.i(TAG, "success");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onVerifyFailure(CognitoNetwork.Responses response) {
                Log.e(TAG, "failure");
                switch (response) {
                    case CODE_EXPIRED:
                        Toast.makeText(getApplicationContext(), "Your code has expired.", Toast.LENGTH_LONG).show();
                        break;
                    case CODE_MISMATCH:
                        Toast.makeText(getApplicationContext(), "This code is a mismatch, please try again.", Toast.LENGTH_LONG).show();
                        break;
                    case GENERAL_FAILURE:
                    default:
                        Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }
}