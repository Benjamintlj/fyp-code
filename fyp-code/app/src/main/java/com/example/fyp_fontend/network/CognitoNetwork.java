package com.example.fyp_fontend.network;
import static com.example.fyp_fontend.utils.Globals.clientId;
import static com.example.fyp_fontend.utils.Globals.region;
import static com.example.fyp_fontend.utils.Globals.userPoolId;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.UpdateAttributesHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult;
import com.example.fyp_fontend.network.callback.UserAttributeCallback;
import com.example.fyp_fontend.network.callback.RegisterCallback;
import com.example.fyp_fontend.network.callback.ResendCodeCallback;
import com.example.fyp_fontend.network.callback.ResetPasswordCallback;
import com.example.fyp_fontend.network.callback.SessionValidationCallback;
import com.example.fyp_fontend.network.callback.SignInCallback;
import com.example.fyp_fontend.network.callback.StartForgotPasswordCallback;
import com.example.fyp_fontend.network.callback.VerifyEmailCallback;

import java.util.List;


public class CognitoNetwork {
    private static final String TAG = "CognitoNetwork";

    public enum Responses {
        SUCCESS,
        USER_ALREADY_EXISTS,
        INVALID_PARAMETER,
        INVALID_PASSWORD,
        INVALID_EMAIL,
        CODE_MISMATCH,
        CODE_EXPIRED,
        NETWORK_ERROR,
        USER_NOT_FOUND,
        RATE_LIMIT_EXCEEDED,
        VERIFICATION_FAILURE,
        GENERAL_FAILURE
    }

    private static CognitoNetwork instance;

    private CognitoNetwork() {}

    public static synchronized CognitoNetwork getInstance() {
        if (instance == null) {
            instance = new CognitoNetwork();
        }
        return instance;
    }

    public void register(String username, String email, String password, Context context, final RegisterCallback callback) {
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
        userAttributes.addAttribute("email", email);

        userAttributes.addAttribute("custom:leagueRank", "bronze");
        userAttributes.addAttribute("custom:currentLeaderboardId", "none");
        userAttributes.addAttribute("custom:seenWelcome", String.valueOf(false));
        userAttributes.addAttribute("custom:rankChanged", String.valueOf(false));

        CognitoUserPool userPool = new CognitoUserPool(context, userPoolId, clientId, null, region);

        userPool.signUpInBackground(username, password, userAttributes, null, new SignUpHandler() {
            @Override
            public void onSuccess(CognitoUser user, SignUpResult signUpResult) {
                callback.onRegisterSuccess();
            }

            @Override
            public void onFailure(Exception exception) {
                String error = exception.getLocalizedMessage();
                Responses response = Responses.GENERAL_FAILURE; // Default failure response

                if (error != null) {
                    Log.d("Registration Failure", error);

                    if (error.toLowerCase().contains("user already exists")) {
                        response = Responses.USER_ALREADY_EXISTS;
                    } else if (error.toLowerCase().contains("invalid parameter")) {
                        response = Responses.INVALID_PARAMETER;
                    } else if (error.toLowerCase().contains("password")) {
                        response = Responses.INVALID_PASSWORD;
                    } else if (error.toLowerCase().contains("email")) {
                        response = Responses.INVALID_EMAIL;
                    }
                }
                callback.onRegisterFailure(response);
            }
        });
    }

    public void verifyEmailConfirmationCode(String username, String code, Context context, final VerifyEmailCallback callback) {
        CognitoUserPool userPool = new CognitoUserPool(context, userPoolId, clientId, null, region);
        CognitoUser user = userPool.getUser(username);

        user.confirmSignUpInBackground(code, false, new GenericHandler() {
            @Override
            public void onSuccess() {
                callback.onVerifySuccess();
            }

            @Override
            public void onFailure(Exception exception) {
                String error = exception.getLocalizedMessage();
                Log.e(TAG, "onFailure: " + error);
                Responses response = Responses.GENERAL_FAILURE;

                if (error != null) {
                    if (error.toLowerCase().contains("mismatch")) {
                        response = Responses.CODE_MISMATCH;
                    } else if (error.toLowerCase().contains("expired")) {
                        response = Responses.CODE_EXPIRED;
                    }
                }

                callback.onVerifyFailure(response);
            }
        });
    }

    public void resendCode(String username, Context context, final ResendCodeCallback callback) {
        CognitoUserPool userPool = new CognitoUserPool(context, userPoolId, clientId, null, region);
        CognitoUser user = userPool.getUser(username);

        user.resendConfirmationCodeInBackground(new VerificationHandler() {
            @Override
            public void onSuccess(CognitoUserCodeDeliveryDetails verificationCodeDeliveryMedium) {
                callback.onResendCodeSuccess();
            }

            @Override
            public void onFailure(Exception exception) {
                String error = exception.getLocalizedMessage();

                Responses response = Responses.GENERAL_FAILURE;

                if (error.contains("code mismatch")) {
                    response = Responses.CODE_MISMATCH;
                } else if (error.contains("expired")) {
                    response = Responses.CODE_EXPIRED;
                } else if (error.contains("network")) {
                    response = Responses.NETWORK_ERROR;
                } else if (error.contains("user not found")) {
                    response = Responses.USER_NOT_FOUND;
                } else if (error.contains("limit exceeded")) {
                    response = Responses.RATE_LIMIT_EXCEEDED;
                } else if (error.contains("invalid parameter")) {
                    response = Responses.INVALID_PARAMETER;
                }

                callback.onResendCodeFailure(response);
            }
        });
    }

    public void signIn(String username, String password, Context context, final SignInCallback callback) {
        CognitoUserPool userPool = new CognitoUserPool(context, userPoolId, clientId, null, region);
        CognitoUser user = userPool.getUser(username);

        AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                Log.d("login", "success");
                callback.onSignInSuccess(userSession.isValid());
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                Log.d("login", "auth details set");
                AuthenticationDetails authenticationDetails = new AuthenticationDetails(username, password, null);
                authenticationContinuation.setAuthenticationDetails(authenticationDetails);
                authenticationContinuation.continueTask();
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
                Log.d("login", "MFA");
                // No MFA
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                Log.d("login", "challenge raised");
                // TODO: implement auth challenge
            }

            @Override
            public void onFailure(Exception exception) {
                // TODO: more detailed failure response
                Log.d("login", "on failure");
                callback.onSignInFailure(Responses.GENERAL_FAILURE);
            }
        };

        user.getSessionInBackground(authenticationHandler);
    }

    public void signOut(Context context) {
        CognitoUserPool userPool = new CognitoUserPool(context, userPoolId, clientId, null, region);
        CognitoUser user = userPool.getCurrentUser();

        if (user != null) {
            user.signOut();
        }
    }

    public void validateUserSession(Context context, final SessionValidationCallback callback) {
        CognitoUserPool userPool = new CognitoUserPool(context, userPoolId, clientId, null, region);
        CognitoUser user = userPool.getCurrentUser();

        if (user == null) {
            callback.onSessionValidationInvalid();
        }

        user.getSessionInBackground(new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                if (userSession.isValid()) {
                    callback.onSessionValidationValid();
                } else {
                    callback.onSessionValidationInvalid();
                }
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                // Not applicable to user verification
                callback.onSessionValidationInvalid();
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
                // No MFA
                callback.onSessionValidationInvalid();
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                callback.onSessionValidationInvalid();
            }

            @Override
            public void onFailure(Exception exception) {
                callback.onSessionValidationFailure(Responses.VERIFICATION_FAILURE);
            }
        });
    }

    public void sendForgotPasswordEmail(String username, Context context, final StartForgotPasswordCallback callback) {
        CognitoUserPool userPool = new CognitoUserPool(context, userPoolId, clientId, null, region);
        CognitoUser user = userPool.getUser(username);

        ForgotPasswordHandler forgotPasswordHandler = new ForgotPasswordHandler() {
            @Override
            public void onSuccess() {
                callback.onCodeSent();
            }

            @Override
            public void getResetCode(ForgotPasswordContinuation continuation) {
                callback.onCodeDeliveryDetails(continuation.getParameters());
            }

            @Override
            public void onFailure(Exception exception) {
                String error = exception.getLocalizedMessage();
                Log.e("ForgotPassword", error);

                Responses response = Responses.GENERAL_FAILURE;

                if (error.contains("LimitExceededException")) {
                    response = Responses.RATE_LIMIT_EXCEEDED;
                } else if (error.contains("InvalidParameterException")) {
                    response = Responses.INVALID_PARAMETER;
                }

                callback.onFailure(response);
            }
        };

        user.forgotPasswordInBackground(forgotPasswordHandler);
    }

    public void resetPassword(String username, String newPassword, String code, Context context, final ResetPasswordCallback callback) {
        CognitoUserPool userPool = new CognitoUserPool(context, userPoolId, clientId, null, region);
        CognitoUser user = userPool.getUser(username);

        user.confirmPasswordInBackground(code, newPassword, new ForgotPasswordHandler() {
            @Override
            public void onSuccess() {
                callback.onResetPasswordSuccess();
            }

            @Override
            public void getResetCode(ForgotPasswordContinuation continuation) {
                // Can be ignored since this is completed in the step before
            }

            @Override
            public void onFailure(Exception exception) {
                String error = exception.getLocalizedMessage();
                Log.e("ResetPassword", error);

                Responses response = Responses.GENERAL_FAILURE;

                if (error.contains("CodeMismatchException")) {
                    response = Responses.CODE_MISMATCH;
                } else if (error.contains("ExpiredCodeException")) {
                    response = Responses.CODE_EXPIRED;
                } else if (error.contains("InvalidPasswordException")) {
                    response = Responses.INVALID_PASSWORD;
                } else if (error.contains("LimitExceededException")) {
                    response = Responses.RATE_LIMIT_EXCEEDED;
                }

                callback.onResetPasswordFailure(response);
            }
        });
    }

    public void getCurrentUserAttribute(Context context, final UserAttributeCallback callback, String attribute) {
        CognitoUserPool userPool = new CognitoUserPool(context, userPoolId, clientId, null, region);
        CognitoUser user = userPool.getCurrentUser();

        if (user != null) {
            user.getDetailsInBackground(new GetDetailsHandler() {
                @Override
                public void onSuccess(CognitoUserDetails cognitoUserDetails) {
                    CognitoUserAttributes attributes = cognitoUserDetails.getAttributes();
                    String value = attributes.getAttributes().get("custom:" + attribute);
                    if (value != null) {
                        callback.onSuccess(value);
                    } else {
                        callback.onFailure(new Exception("No value exists."));
                    }
                }

                @Override
                public void onFailure(Exception exception) {
                    callback.onFailure(exception);
                }
            });
        } else {
            callback.onFailure(new Exception("User not signed in."));
        }
    }

    public void setCurrentUserAttribute(Context context, final UserAttributeCallback callback, String attribute, String value) {
        CognitoUserPool userPool = new CognitoUserPool(context, userPoolId, clientId, null, region);
        CognitoUser user = userPool.getCurrentUser();

        if (user != null) {
            CognitoUserAttributes userAttributes = new CognitoUserAttributes();
            userAttributes.addAttribute("custom:" + attribute, value);

            user.updateAttributesInBackground(userAttributes, new UpdateAttributesHandler() {
                @Override
                public void onSuccess(List<CognitoUserCodeDeliveryDetails> attributesVerificationList) {
                    callback.onSuccess(null);
                }

                @Override
                public void onFailure(Exception exception) {
                    callback.onFailure(exception);
                }
            });
        } else {
            callback.onFailure(new Exception("User not signed in."));
        }
    }

    public String getCurrentUsername(Context context) {
        CognitoUserPool userPool = new CognitoUserPool(context, userPoolId, clientId, null, region);
        CognitoUser currentUser = userPool.getCurrentUser();

        if (currentUser != null) {
            return currentUser.getUserId();
        } else {
            return null;
        }
    }
}

