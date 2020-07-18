package com.shopby.dhakkan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.R;

/**
 * Created by Nasir on 7/5/17.
 */

public class OrderConfirmationPage extends BaseActivity {

    private Context mContext;
    private Activity mActivity;

    // UI
    private TextView tvOrderId;
    private Button btnClose;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        loadData();
        initToolbar();
        initListener();
    }

    private void initVariable() {
        mContext = getApplicationContext();
        mActivity = OrderConfirmationPage.this;
    }

    private void initView() {
        setContentView(R.layout.activity_order_confirm);

        initToolbar();
        enableBackButton();
        enableBackButton();
        setToolbarTitle(getString(R.string.order_successful));

        tvOrderId = findViewById(R.id.tvOrderId);
        btnClose = findViewById(R.id.btnClose);
    }


    private void loadData() {

        Intent intent = getIntent();
        if (intent.hasExtra(AppConstants.ORDER_ID)) {
            orderId = intent.getStringExtra(AppConstants.ORDER_ID);
        }

        tvOrderId.setText("Order ID: " + orderId);
    }

    private void initListener() {

        btnClose.setOnClickListener(v -> finish());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
