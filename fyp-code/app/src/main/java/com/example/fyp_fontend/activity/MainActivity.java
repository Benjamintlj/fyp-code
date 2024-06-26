package com.example.fyp_fontend.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.activity.content_selection.HomeActivity;
import com.example.fyp_fontend.activity.login_flow.ForgotPasswordSendCodeActivity;
import com.example.fyp_fontend.activity.login_flow.RegisterActivity;
import com.example.fyp_fontend.activity.login_flow.SendVerificationCodeActivity;
import com.example.fyp_fontend.activity.login_flow.callback.IsUserSignedInCallback;
import com.example.fyp_fontend.network.CognitoNetwork;
import com.example.fyp_fontend.network.callback.SessionValidationCallback;
import com.example.fyp_fontend.network.callback.SignInCallback;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Button registerActivityButton, signInButton, verifyEmailActivityButton, forgotPasswordButton;
    TextInputEditText usernameEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_loading);
        TextView titleTextView = findViewById(R.id.loadingTitleTextView);
        titleTextView.setText(R.string.loading_login_splash_screen);

        isUserSignedIn(new IsUserSignedInCallback() {
            @Override
            public void onUserSignedIn() {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onUserNotSignedIn() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setContentView(R.layout.activity_main);
                        initViews();
                        initListeners();
                    }
                });
            }
        });
    }

    private void initViews() {
        registerActivityButton = findViewById(R.id.registerButton);
        signInButton = findViewById(R.id.signInButton);
        verifyEmailActivityButton = findViewById(R.id.verifyEmailActivityButton);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
    }

    private void initListeners() {
        registerActivityButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });

        verifyEmailActivityButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SendVerificationCodeActivity.class);
            String username = Objects.requireNonNull(usernameEditText.getText()).toString();
            intent.putExtra("username", username);
            startActivity(intent);
        });

        forgotPasswordButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ForgotPasswordSendCodeActivity.class);
            String username = Objects.requireNonNull(usernameEditText.getText()).toString();
            intent.putExtra("username", username);
            startActivity(intent);
        });

        signInButton.setOnClickListener(this::signIn);
    }

    private void signIn(View view) {
        String username = Objects.requireNonNull(usernameEditText.getText()).toString();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.please_enter_username_and_password, Toast.LENGTH_SHORT).show();
        }

        CognitoNetwork.getInstance().signIn(username, password, getApplicationContext(), new SignInCallback() {
            @Override
            public void onSignInSuccess(Boolean isUserSignedIn) {
                if (isUserSignedIn) {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.failed_to_sign_in_please_try_again, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSignInFailure(CognitoNetwork.Responses response) {
                Toast.makeText(getApplicationContext(), R.string.failed_to_sign_in_please_try_again, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void isUserSignedIn(IsUserSignedInCallback callback) {
        CognitoNetwork.getInstance().validateUserSession(getApplicationContext(), new SessionValidationCallback() {
            @Override
            public void onSessionValidationValid() {
                callback.onUserSignedIn();
            }

            @Override
            public void onSessionValidationInvalid() {
                callback.onUserNotSignedIn();
            }

            @Override
            public void onSessionValidationFailure(CognitoNetwork.Responses response) {
                callback.onUserNotSignedIn();
            }
        });
    }
}