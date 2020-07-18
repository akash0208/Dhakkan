package com.shopby.dhakkan.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.R;
import com.shopby.dhakkan.utils.ActivityUtils;
import com.shopby.dhakkan.utils.AnalyticsUtils;
import com.shopby.dhakkan.utils.AppUtility;

import java.util.Objects;

/**
 * Created by Nasir on 5/14/17.
 */

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Context mContext;
    private Activity mActivity;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

    private LinearLayout loadingView, noDataView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable();
    }

    private void initVariable() {
        mContext = getApplicationContext();
        mActivity = BaseActivity.this;
    }

    public void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    public void enableBackButton() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public Toolbar getToolbar() {
        if (mToolbar == null) {
            mToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
        }
        return mToolbar;
    }

    public void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    public void initDrawer() {

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView mNavigationView = findViewById(R.id.navigationView);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setItemIconTintList(null);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        //mToolbar.setNavigationIcon(R.drawable.ic_toggle_24);
        //mToolbar.setTitleTextColor(getResources().getColor(R.color.blue));

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_my_address) {

            // analytics event trigger
            AnalyticsUtils.getAnalyticsUtils(mContext).trackEvent("Drawer : My account");

            ActivityUtils.getInstance().invokeAddressActivity(mActivity, null, true, false);

        } else if (id == R.id.action_cart) {

            // analytics event trigger
            AnalyticsUtils.getAnalyticsUtils(mContext).trackEvent("Drawer : Cart list");

            ActivityUtils.getInstance().invokeActivity(mActivity, CartListActivity.class, false);
        } else if (id == R.id.action_wish) {
            // analytics event trigger
            AnalyticsUtils.getAnalyticsUtils(mContext).trackEvent("Drawer : Wish list");

            ActivityUtils.getInstance().invokeActivity(mActivity, WishListActivity.class, false);
        } else if (id == R.id.action_orders) {
            // analytics event trigger
            AnalyticsUtils.getAnalyticsUtils(mContext).trackEvent("Drawer : Order list");

            ActivityUtils.getInstance().invokeActivity(mActivity, OrderListActivity.class, false);
        }

        // support
//        else if (id == R.id.action_call) {
//            AppUtility.makePhoneCall(mActivity, AppConstants.CALL_NUMBER);
//        } else if (id == R.id.action_message) {
//            AppUtility.sendSMS(mActivity, AppConstants.SMS_NUMBER, AppConstants.SMS_TEXT);
//        }
        else if (id == R.id.action_whatsapp) {
            AppUtility.invokeWhatsappBot(mActivity);
        } else if (id == R.id.action_email) {
            AppUtility.sendEmail(mActivity, AppConstants.EMAIL_ADDRESS, AppConstants.EMAIL_SUBJECT, AppConstants.EMAIL_BODY);
        }

        // others
        else if (id == R.id.action_share) {
            AppUtility.shareApp(mActivity);
        } else if (id == R.id.action_rate_app) {
            AppUtility.rateThisApp(mActivity); // this feature will only work after publish the app
        } else if (id == R.id.action_settings) {
            ActivityUtils.getInstance().invokeActivity(mActivity, SettingsActivity.class, false);
        } else if (id == R.id.terms_conditions) {
            ActivityUtils.getInstance().invokeWebPageActivity(mActivity, getResources().getString(R.string.terms), getResources().getString(R.string.terms_url));
        } else if (id == R.id.privacy_policy) {
            ActivityUtils.getInstance().invokeWebPageActivity(mActivity, getResources().getString(R.string.privacy), getResources().getString(R.string.privacy_url));
        }
//        else if (id == R.id.faq) {
//            ActivityUtils.getInstance().invokeWebPageActivity(mActivity, getResources().getString(R.string.faq), getResources().getString(R.string.faq_url));
//        }

        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }


    public void initLoader() {
        loadingView = findViewById(R.id.loadingView);
        noDataView = findViewById(R.id.noDataView);
    }

    public void showLoader() {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }

        if (noDataView != null) {
            noDataView.setVisibility(View.GONE);
        }
    }

    public void hideLoader() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (noDataView != null) {
            noDataView.setVisibility(View.GONE);
        }
    }

    public void showEmptyView() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (noDataView != null) {
            noDataView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        if (PermissionUtils.isPermissionResultGranted(grantResults)) {
//             if (requestCode == PermissionUtils.REQUEST_CALL) {
//                AppUtility.makePhoneCall(mActivity, AppConstants.CALL_NUMBER);
//            }
//        } else {
//            AppUtility.showToast(mActivity, getString(R.string.permission_not_granted));
//        }

    }

    public void showAdThenActivity(final Class<?> activity) {
        ActivityUtils.getInstance().invokeActivity(mActivity, activity, false);

    }


}
