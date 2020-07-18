package com.shopby.dhakkan.registration;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.shopby.dhakkan.R;

/**
 * Created by ashiq on 8/27/2017.
 */

public class BaseLoginActivity extends AppCompatActivity {

    private static final int FB_REQUEST_CODE = 99,
            PERMISSION_REQ = 100,
            GOOGLE_REQUEST_CODE = 101;
    private LoginListener loginListener;
    private CallbackManager callbackManager;

    private SignInButton btnLoginGPlus;
    private GoogleSignInClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private boolean grantPermission() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.RECEIVE_SMS}, PERMISSION_REQ);

            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                invokePhoneLogin();
            } else {
                Toast.makeText(BaseLoginActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void invokePhoneLogin() {
        if (grantPermission()) {
            final Intent intent = new Intent(this, AccountKitActivity.class);
            AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                    new AccountKitConfiguration.AccountKitConfigurationBuilder(
                            LoginType.PHONE,
                            AccountKitActivity.ResponseType.TOKEN);


            // enable receive auto sms
            configurationBuilder.setReadPhoneStateEnabled(true);
//            configurationBuilder.setReceiveSMS(true);
            configurationBuilder.setEnableSms(true);
            configurationBuilder.setDefaultCountryCode("IN");

            intent.putExtra(
                    AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                    configurationBuilder.build());
            startActivityForResult(intent, FB_REQUEST_CODE);
        }
    }

    public void invokeEmailLogin() {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.EMAIL,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, FB_REQUEST_CODE);
    }

    public void initFbLogin() {

        LoginButton loginButton = findViewById(R.id.btnLoginFacebook);
        loginButton.setVisibility(View.VISIBLE);
        loginButton.setReadPermissions("email");

        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getUserInfo(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(BaseLoginActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(BaseLoginActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initGoogleLogin() {
        btnLoginGPlus = findViewById(R.id.btnLoginGPlus);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, connectionResult -> Toast.makeText(BaseLoginActivity.this, "Failed", Toast.LENGTH_SHORT).show())
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();

        mGoogleApiClient = GoogleSignIn.getClient(this, gso);

        btnLoginGPlus.setOnClickListener(v -> {
//            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            Intent signInIntent = mGoogleApiClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_REQUEST_CODE);

            // analytics event trigger
            // AnalyticsUtils.getAnalyticsUtils(mContext).trackEvent("Login with google");
        });

    }

    public void setGoogleButtonText(String buttonText) {
        if (btnLoginGPlus != null) {
            for (int i = 0; i < btnLoginGPlus.getChildCount(); i++) {
                View v = btnLoginGPlus.getChildAt(i);

                if (v instanceof TextView) {
                    TextView tv = (TextView) v;
                    tv.setText(buttonText);
                    return;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FB_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (loginResult.getError() != null) {
                Toast.makeText(BaseLoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            } else if (loginResult.wasCancelled()) {
                Toast.makeText(BaseLoginActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                getUserInfo();
            }
        } else if (requestCode == GOOGLE_REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Toast.makeText(this, "g signined in ", Toast.LENGTH_SHORT).show();
                getUserInfo(result);
            } else {
                Toast.makeText(BaseLoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getUserInfo(GoogleSignInResult result) {
        GoogleSignInAccount account = result.getSignInAccount();

        if (loginListener != null) {
            assert account != null;
            Uri uri = account.getPhotoUrl();
            String photo = "";
            if (uri != null) {
                photo = uri.toString();
            }
            loginListener.onLoginResponse(new LoginModel(account.getId(), account.getDisplayName(), photo, account.getEmail()));
        }
    }

    private void getUserInfo() {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                String accountKitId = account.getId();

                // Get phone number
                PhoneNumber phoneNumber = account.getPhoneNumber();
                String phoneNumberString = null;
                if (phoneNumber != null) {
                    phoneNumberString = phoneNumber.toString();
                    Log.e("AccKit", "Phone: " + phoneNumberString);
                }

                // Get email
                String email = account.getEmail();

                if (loginListener != null) {
                    loginListener.onLoginResponse(new LoginModel(accountKitId, email, phoneNumberString));
                }
            }

            @Override
            public void onError(final AccountKitError error) {
                Log.e("AccKit", "Error: " + error.toString());
            }
        });
    }

    private void getUserInfo(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                (object, response) -> {
                    try {
                        String id = object.getString("id");
                        String name = object.getString("name");
                        String email = object.getString("email");
                        String profilePic = "https://graph.facebook.com/" + id + "/picture?width=200&height=150";

                        if (loginListener != null) {
                            loginListener.onLoginResponse(new LoginModel(id, name, profilePic, email));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void logout() {
        AccountKit.logOut();
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

}
