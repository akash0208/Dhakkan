package com.shopby.dhakkan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.shopby.dhakkan.R;
import com.shopby.dhakkan.adapter.ProductListAdapter;
import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.listener.OnItemClickListener;
import com.shopby.dhakkan.model.LineItem;
import com.shopby.dhakkan.model.ProductDetail;
import com.shopby.dhakkan.network.helper.RequestProductDetails;
import com.shopby.dhakkan.network.http.ResponseListener;
import com.shopby.dhakkan.utils.ListTypeShow;

import java.util.ArrayList;

/**
 * Created by Nasir on 5/15/17.
 */

public class OrderDetailsActivity extends BaseActivity {

    // initialize variables
    private Activity mActivity;
    private Context mContext;

    private RecyclerView rvProductList;
    private ArrayList<ProductDetail> productList;
    private ProductListAdapter mProductListAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        initToolbar();
        initListener();
        loadProductList();
    }

    private void initVariable() {
        mActivity = OrderDetailsActivity.this;
        mContext = mActivity.getApplicationContext();
        productList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_order_details);

        initToolbar();
        enableBackButton();
        initLoader();

        rvProductList = (RecyclerView) findViewById(R.id.rvProductList);

        // init RecyclerView
        rvProductList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        rvProductList.setLayoutManager(mLayoutManager);
        mProductListAdapter = new ProductListAdapter(mContext, productList, ListTypeShow.LINEAR);
        rvProductList.setAdapter(mProductListAdapter);

    }


    private void initListener() {
        mProductListAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemListener(View view, int position) {
                System.out.print("Nothing.");
            }
        });
    }

    private void loadProductList() {

        // receive line items
        Intent intent = getIntent();
        ArrayList<LineItem> lineItems = intent.getParcelableArrayListExtra(AppConstants.KEY_LINE_ITEM_LIST);

        if (!productList.isEmpty()) {
            productList.clear();
        }

        for (int i = 0; i < lineItems.size(); i++) {
            RequestProductDetails requestProduct = new RequestProductDetails(mActivity, lineItems.get(i).productId);
            requestProduct.setResponseListener(new ResponseListener() {
                @Override
                public void onResponse(Object data) {
                    if (data != null) {
                        productList.add((ProductDetail) data);
                        mProductListAdapter.notifyDataSetChanged();
                    }
                    hideLoader();
                }
            });
            requestProduct.execute();
        }
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
