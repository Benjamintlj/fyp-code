package com.example.fyp_fontend.activity.login_flow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.network.CognitoNetwork;
import com.example.fyp_fontend.network.callback.RegisterCallback;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    static final String TAG = "RegisterActivity";

    TextInputLayout usernameTextInputLayout, emailTextInputLayout, passwordTextInputLayout;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initListeners();
    }

    private void initViews() {
        usernameTextInputLayout = findViewById(R.id.usernameTextInputLayout);
        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
        registerButton = findViewById(R.id.registerButton);
    }

    private void initListeners() {
        registerButton.setOnClickListener(this::sendRegistrationRequest);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void sendRegistrationRequest(View view) {
        // TODO: reduce code size
        EditText username = usernameTextInputLayout.getEditText();
        EditText email = emailTextInputLayout.getEditText();
        EditText password = passwordTextInputLayout.getEditText();

        if (username == null || email == null || password == null) {
            showToast("Error entries are null.");
            return;
        }

        String usernameText = username.getText().toString();
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();

        if (usernameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty()) {
            showToast("Please fill all fields.");
            return;
        }

        CognitoNetwork.getInstance().register(
                usernameText,
                emailText,
                passwordText,
                getApplicationContext(),
                new RegisterCallback() {
                    @Override
                    public void onRegisterSuccess() {
                        Log.d(TAG, "success");
                        Intent intent = new Intent(getApplicationContext(), VerifyEmailActivity.class);
                        intent.putExtra("username", usernameText);
                        startActivity(intent);
                    }

                    @Override
                    public void onRegisterFailure(CognitoNetwork.Responses response) {
                        Log.d(TAG, "failure");

                        switch (response) {
                            case USER_ALREADY_EXISTS:
                                showToast("User already exists.");
                                break;
                            case INVALID_PARAMETER:
                                showToast("Invalid parameter.");
                                break;
                            case INVALID_PASSWORD:
                                showToast("Password must contain: 8 char, symbol, number, upper and lower case.");
                                break;
                            case INVALID_EMAIL:
                                showToast("Invalid email.");
                                break;
                            case GENERAL_FAILURE:
                            default:
                                showToast("Something went wrong.");
                                break;
                        }
                    }
                });
    }
}