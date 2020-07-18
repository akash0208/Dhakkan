package com.shopby.dhakkan.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.shopby.dhakkan.R;
import com.shopby.dhakkan.data.cache.ProfileData;
import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.data.preference.AppPreference;
import com.shopby.dhakkan.data.preference.PrefKey;
import com.shopby.dhakkan.model.Customer;
import com.shopby.dhakkan.model.LineItem;
import com.shopby.dhakkan.network.helper.GetCustomerByEmail;
import com.shopby.dhakkan.network.helper.RequestCreateCustomer;
import com.shopby.dhakkan.registration.BaseLoginActivity;
import com.shopby.dhakkan.utils.ActivityUtils;
import com.shopby.dhakkan.utils.AnalyticsUtils;
import com.shopby.dhakkan.utils.AppUtility;
import com.shopby.dhakkan.utils.DialogUtils;

import java.util.ArrayList;

public class LoginActivity extends BaseLoginActivity {

    // initialize variables
    private Context mContext;
    private Activity mActivity;

    // initialize UI
    private TextView tvSkipLogin, tvWelcome;
//    private Button btnLoginFbEmail;

    private boolean shouldOrder = false;
    private ArrayList<LineItem> lineItems;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFbLogin();
        initGoogleLogin();
        setGoogleButtonText(getString(R.string.google_cap));
        initListener();
    }

    private void initVar() {
        // variables instance
        mContext = getApplicationContext();
        mActivity = LoginActivity.this;

        lineItems = new ArrayList<>();

        Intent intent = getIntent();
        if (intent.hasExtra(AppConstants.KEY_LINE_ITEM_LIST)) {
            lineItems = intent.getParcelableArrayListExtra(AppConstants.KEY_LINE_ITEM_LIST);
        }
        if (intent.hasExtra(AppConstants.KEY_LOGIN_ORDER)) {
            shouldOrder = intent.getBooleanExtra(AppConstants.KEY_LOGIN_ORDER, false);
        }

        // analytics event trigger
        AnalyticsUtils.getAnalyticsUtils(mContext).trackEvent("Login Activity");
    }

    private void initView() {
        setContentView(R.layout.activity_login);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.loginColor));


        View decorView = this.getWindow().getDecorView();
        int systemUiVisibilityFlags = decorView.getSystemUiVisibility();
        systemUiVisibilityFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

        decorView.setSystemUiVisibility(systemUiVisibilityFlags);


        ImageView imageView = findViewById(R.id.gif);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(this).load(R.raw.christmastree).into(imageViewTarget);

        tvSkipLogin = findViewById(R.id.tvSkipLogin);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setOnClickListener(v -> {
            Intent intent = this.getIntent();
            Uri uri = intent.getData();
            if (uri != null) {
                Toast.makeText(mContext, "Source: " + uri.getQueryParameter("utm_source"), Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, "complete uri: " + uri.toString(), Toast.LENGTH_SHORT).show();
            }
        });
//        btnLoginFbEmail = findViewById(R.id.btnLoginFbEmail);

        AppUtility.noInternetWarning(tvSkipLogin, mContext);

    }


    private void initListener() {
        tvSkipLogin.setOnClickListener(v -> {
            AppPreference.getInstance(mContext).setBoolean(PrefKey.SKIPPED, true);
            ActivityUtils.getInstance().invokeActivity(mActivity, MainActivity.class, true);

            // analytics event trigger
            AnalyticsUtils.getAnalyticsUtils(mContext).trackEvent("Skipped login process");
        });

//        btnLoginFbEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                invokeEmailLogin();
//            }
//        });

        setLoginListener(loginModel -> {
            if (loginModel.getUserId() != null) {

                ProfileData.storeProfileData(mContext,
                        AppConstants.EMPTY_STRING, loginModel.getName(),
                        AppConstants.EMPTY_STRING, loginModel.getEmail(),
                        AppConstants.EMPTY_STRING, loginModel.getProfilePic());

                Customer customer = new Customer(AppConstants.EMPTY_STRING,
                        loginModel.getEmail(), loginModel.getName(),
                        AppConstants.EMPTY_STRING, AppConstants.EMPTY_STRING,
                        AppConstants.EMPTY_STRING, AppConstants.EMPTY_STRING);

                createNewCustomer(customer);
            }
        });
    }


    private void createNewCustomer(final Customer mCustomer) {
        // start loading progress dialog
        progressDialog = DialogUtils.showProgressDialog(mActivity, getString(R.string.msg_loading), false);

        RequestCreateCustomer createCustomer = new RequestCreateCustomer(mContext, mCustomer);
        createCustomer.buildParams();
        createCustomer.setResponseListener(data -> {
            // dismiss dialog

            if (data != null) {
                Customer customer = (Customer) data;

                if (customer.customerId != null) {
                    DialogUtils.dismissProgressDialog(progressDialog);
                    AppPreference.getInstance(mContext).setBoolean(PrefKey.REGISTERED, true);

                    // store user info
                    ProfileData.storeProfileData(mContext, customer.customerId, customer.firstName, customer.lastName, customer.email,
                            customer.userName, "");
                    whatNext();
                } else if (customer.responseCode.equalsIgnoreCase(AppConstants.REGISTRATION_ERROR_EMAIL_EXIST) ||
                        customer.responseCode.equalsIgnoreCase(AppConstants.REGISTRATION_ERROR_USER_EXIST)) {
                    getExitCustomerInfo(mCustomer.email);
                } else {
                    DialogUtils.dismissProgressDialog(progressDialog);
                    AppUtility.showToast(mContext, getString(R.string.failed) + " yess");
                }
            } else {
                DialogUtils.dismissProgressDialog(progressDialog);
                AppUtility.showToast(mContext, getString(R.string.failed) + "ye hai");
            }

        });
        createCustomer.execute();
    }

    private void getExitCustomerInfo(String email) {
        GetCustomerByEmail requestCustomers = new GetCustomerByEmail(mContext, email);
        requestCustomers.setResponseListener(data -> {
            if (data != null) {
                DialogUtils.dismissProgressDialog(progressDialog);
                Customer customer = ((Customer) data);

                // store user info
                ProfileData.storeProfileData(mContext, customer.customerId, customer.firstName, customer.lastName, customer.email,
                        customer.userName, AppConstants.EMPTY_STRING);

                // set user login status
                AppPreference.getInstance(mContext).setBoolean(PrefKey.REGISTERED, true);
                whatNext();
            }
        });
        requestCustomers.execute();
    }


    private void whatNext() {
        if (shouldOrder) {
            ActivityUtils.getInstance().invokeAddressActivity(mActivity, lineItems, false, true);
        } else {
            ActivityUtils.getInstance().invokeActivity(mActivity, MainActivity.class, true);
        }
    }

}
