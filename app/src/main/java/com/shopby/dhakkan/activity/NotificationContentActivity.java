package com.shopby.dhakkan.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.R;

public class NotificationContentActivity extends AppCompatActivity {

    private Context mContext;
    private Activity mActivity;

    private Toolbar mToolbar;
    private TextView titleView, messageView;
    private String title, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = NotificationContentActivity.this;
        mContext = mActivity.getApplicationContext();
        initView();
        initVeritable();
        initFunctionality();
        initListeners();
    }

    private void initVeritable() {
        Bundle extras = getIntent().getExtras();
        title = extras.getString(AppConstants.BUNDLE_KEY_TITLE);
        message = extras.getString(AppConstants.BUNDLE_KEY_MESSAGE);
    }

    private void initView() {
        setContentView(R.layout.activity_notification_details);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.notification_details));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        titleView = (TextView) findViewById(R.id.title);
        messageView = (TextView) findViewById(R.id.message);


    }


    private void initFunctionality() {

        titleView.setText(title);
        messageView.setText(message);
    }

    private void initListeners() {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
