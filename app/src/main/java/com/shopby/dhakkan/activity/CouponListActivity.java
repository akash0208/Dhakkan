package com.shopby.dhakkan.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.shopby.dhakkan.R;
import com.shopby.dhakkan.adapter.CouponListAdapter;
import com.shopby.dhakkan.model.CouponItem;
import com.shopby.dhakkan.network.helper.RequestCouponList;

import java.util.ArrayList;

/**
 * Created by Nasir on 7/02/17.
 */

public class CouponListActivity extends BaseActivity {

    // initialize variables
    private Context mContext;
    private Activity mActivity;

    private RecyclerView rvCouponList;
    private ArrayList<CouponItem> couponList;
    private CouponListAdapter couponListAdapter;

    private TextView info_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        loadCouponList();
        initListener();
    }

    private void initVariable() {
        mContext = getApplicationContext();
        mActivity = CouponListActivity.this;
        couponList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_coupon_list);

        initToolbar();
        enableBackButton();
        initLoader();

        rvCouponList = findViewById(R.id.rvCouponList);
        info_text = findViewById(R.id.info_text);

        // init RecyclerView
        rvCouponList.setHasFixedSize(true);

        rvCouponList.setLayoutManager(new LinearLayoutManager(mActivity));
        couponListAdapter = new CouponListAdapter(mActivity, couponList);
        rvCouponList.setAdapter(couponListAdapter);

    }

    private void loadCouponList() {

        showLoader();

        RequestCouponList requestCouponList = new RequestCouponList(mContext);
        requestCouponList.setResponseListener(data -> {
            Log.d("Coupon data", "loadCouponList: " + data);
            if (data != null) {
                if (!couponList.isEmpty()) {
                    couponList.clear();
                }

                couponList.addAll((ArrayList<CouponItem>) data);
                couponListAdapter.notifyDataSetChanged();

                if (couponList.isEmpty()) {
                    showEmptyView();
                    info_text.setText(getString(R.string.empty));
                } else {
                    hideLoader();
                }

            } else {
                showEmptyView();
                info_text.setText(getString(R.string.empty));
            }

        });
        requestCouponList.execute();

    }

    private void initListener() {

        couponListAdapter.setItemClickListener((viewItem, position) -> {


        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(mContext, item.getItemId(), Toast.LENGTH_SHORT).show();
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
