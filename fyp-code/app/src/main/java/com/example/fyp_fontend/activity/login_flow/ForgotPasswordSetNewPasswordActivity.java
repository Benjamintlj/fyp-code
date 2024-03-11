package com.example.fyp_fontend.activity.login_flow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.activity.MainActivity;
import com.example.fyp_fontend.network.CognitoNetwork;
import com.example.fyp_fontend.network.callback.ResetPasswordCallback;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class ForgotPasswordSetNewPasswordActivity extends AppCompatActivity {

    String username;
    TextInputEditText codeTextInputEditText, passwordTextInputLayout;
    Button setPasswordButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_set_new_password);

        username = getIntent().getStringExtra("username");

        initViews();
        initListeners();
    }

    private void initViews() {
        codeTextInputEditText = findViewById(R.id.codeTextInputEditText);
        passwordTextInputLayout = findViewById(R.id.passwordTextInputEditText);
        setPasswordButton = findViewById(R.id.setPasswordButton);
    }

    private void initListeners() {
        setPasswordButton.setOnClickListener(this::setNewPassword);
    }

    private void setNewPassword(View view) {
        String code = Objects.requireNonNull(codeTextInputEditText.getText()).toString();
        String password = Objects.requireNonNull(passwordTextInputLayout.getText()).toString();

        if (code.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter password and code.", Toast.LENGTH_SHORT).show();
        }

        CognitoNetwork.getInstance().resetPassword(username, password, code, getApplicationContext(), new ResetPasswordCallback() {
            @Override
            public void onResetPasswordSuccess() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }

            @Override
            public void onResetPasswordFailure(CognitoNetwork.Responses response) {
                switch (response) {
                    case CODE_MISMATCH:
                        Toast.makeText(getApplicationContext(), "The code entered is invalid.", Toast.LENGTH_SHORT).show();
                        break;
                    case CODE_EXPIRED:
                        Toast.makeText(getApplicationContext(), "The code entered has expired, please try again.", Toast.LENGTH_SHORT).show();
                        break;
                    case INVALID_PASSWORD:
                        Toast.makeText(getApplicationContext(), "Password must contain: 8 char, symbol, number, upper and lower case.", Toast.LENGTH_SHORT).show();
                        break;
                    case RATE_LIMIT_EXCEEDED:
                        Toast.makeText(getApplicationContext(), "You have tried too many times, try again later.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}