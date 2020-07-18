package com.shopby.dhakkan.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.shopby.dhakkan.R;
import com.shopby.dhakkan.app.MyApplication;
import com.shopby.dhakkan.data.preference.AppPreference;
import com.shopby.dhakkan.data.preference.PrefKey;
import com.shopby.dhakkan.utils.ActivityUtils;
import com.shopby.dhakkan.utils.AppUtility;

/**
 * Created by Nasir on 5/28/17.
 */

public class SplashActivity extends AppCompatActivity {

    // init variables
    private Context mContext;
    private Activity mActivity;
    private static final int SPLASH_DURATION = 2500;

    private FirebaseAnalytics mFirebaseAnalytics;

    // init view
    private LinearLayout lytGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "App Started");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Dhakkan APP");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        //Tracker t = ((MyApplication) this.getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        //t.setScreenName("SplashActivity");

        //String campaignData = "https://play.google.com/store/apps/details?id=com.shopby.dhakkan&referrer=utm_source%3Dzettamobi%26utm_medium%3Dcpv%26utm_term%3Dpaid%26utm_content%3Ddsp%26utm_campaign%3Ddsp_zm";

        //t.send(new HitBuilders.ScreenViewBuilder().setCampaignParamsFromUrl(campaignData).build());


        initVariables();
        initView();
        initListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initFunctionality();
    }

    private void initFunctionality() {

        lytGetStarted.setVisibility(View.VISIBLE);

        if (AppUtility.isNetworkAvailable(mContext)) {
            boolean registered = AppPreference.getInstance(mContext).getBoolean(PrefKey.REGISTERED);
            boolean skipped = AppPreference.getInstance(mContext).getBoolean(PrefKey.SKIPPED);

            if (registered || skipped) {
                lytGetStarted.setVisibility(View.INVISIBLE);
                lytGetStarted.postDelayed(() -> ActivityUtils.getInstance().invokeActivity(mActivity, MainActivity.class, true), SPLASH_DURATION);
            }
        } else {
            lytGetStarted.setVisibility(View.INVISIBLE);
        }

        AppUtility.noInternetWarning(lytGetStarted, mContext);

    }

    private void initVariables() {
        mActivity = SplashActivity.this;
        mContext = mActivity.getApplicationContext();
    }

    private void initView() {
        setContentView(R.layout.activity_splash);
        lytGetStarted = findViewById(R.id.lytGetStarted);

        ImageView imageView = findViewById(R.id.ivLogo);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(this).load(R.raw.christmastree).into(imageViewTarget);

    }

    private void initListener() {
        lytGetStarted.setOnClickListener(v -> ActivityUtils.getInstance().invokeActivity(mActivity, LoginActivity.class, true));
    }

}