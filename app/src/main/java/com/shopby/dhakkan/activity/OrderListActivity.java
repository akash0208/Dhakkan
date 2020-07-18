package com.shopby.dhakkan.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.R;
import com.shopby.dhakkan.adapter.OrderListAdapter;
import com.shopby.dhakkan.data.preference.AppPreference;
import com.shopby.dhakkan.data.preference.PrefKey;
import com.shopby.dhakkan.listener.OnItemClickListener;
import com.shopby.dhakkan.model.LineItem;
import com.shopby.dhakkan.model.OrderItem;
import com.shopby.dhakkan.network.helper.RequestCancelOrder;
import com.shopby.dhakkan.network.helper.RequestOrderItemList;
import com.shopby.dhakkan.network.helper.RequestOrderList;
import com.shopby.dhakkan.network.http.ResponseListener;
import com.shopby.dhakkan.utils.DialogUtils;

import java.util.ArrayList;

/**
 * Created by Nasir on 7/02/17.
 */

public class OrderListActivity extends BaseActivity {

    // initialize variables
    private Context mContext;
    private Activity mActivity;

    private RecyclerView rvOrderList;
    private ArrayList<OrderItem> orderList;
    private OrderListAdapter orderListAdapter;

    private TextView info_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        loadOrderList();
        initListener();
    }

    private void initVariable() {
        mContext = getApplicationContext();
        mActivity = OrderListActivity.this;
        orderList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_order_list);

        initToolbar();
        enableBackButton();
        initLoader();

        rvOrderList = (RecyclerView) findViewById(R.id.rvOrderList);
        info_text = (TextView) findViewById(R.id.info_text);

        // init RecyclerView
        rvOrderList.setHasFixedSize(true);

        rvOrderList.setLayoutManager(new LinearLayoutManager(mActivity));
        orderListAdapter = new OrderListAdapter(mActivity, orderList);
        rvOrderList.setAdapter(orderListAdapter);

    }


    private void loadOrderList() {

        boolean isRegistered = AppPreference.getInstance(mContext).getBoolean(PrefKey.REGISTERED);

        if (isRegistered) {
            String customerId = AppPreference.getInstance(mContext).getString(PrefKey.CUSTOMER_ID);

            if (!customerId.isEmpty()) {
                showLoader();

                RequestOrderList requestOrderList = new RequestOrderList(mContext, customerId);
                requestOrderList.setResponseListener(new ResponseListener() {
                    @Override
                    public void onResponse(Object data) {
                        if (data != null) {
                            if (!orderList.isEmpty()) {
                                orderList.clear();
                            }

                            orderList.addAll((ArrayList<OrderItem>) data);
                            orderListAdapter.notifyDataSetChanged();

                            if (orderList.isEmpty()) {
                                showEmptyView();
                                info_text.setText(getString(R.string.empty));
                            } else {
                                hideLoader();
                            }

                        } else {
                            showEmptyView();
                            info_text.setText(getString(R.string.empty));
                        }

                    }
                });
                requestOrderList.execute();
            }
        } else {
            showEmptyView();
            info_text.setText(getString(R.string.register_to_show_order));
        }

    }

    private void initListener() {

        orderListAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemListener(View viewItem, int position) {
                switch (viewItem.getId()) {

                    case R.id.cancelOrder:
                        // Remove from cart list
                        cancelOrderDialog(orderList.get(position).orderId);
                        break;

                    default:
                        final ProgressDialog progressDialog = DialogUtils.showProgressDialog(mActivity, getString(R.string.msg_loading), false);

                        RequestOrderItemList requestOrderItems = new RequestOrderItemList(mActivity, orderList.get(position).orderId);
                        requestOrderItems.setResponseListener(new ResponseListener() {
                            @Override
                            public void onResponse(Object data) {
                                if (data != null) {

                                    OrderItem orderItem = (OrderItem) data;

                                    ArrayList<LineItem> lineItems = orderItem.lineItems;

                                    if (!lineItems.isEmpty()) {
                                        Intent intent = new Intent(mActivity, OrderDetailsActivity.class);
                                        intent.putParcelableArrayListExtra(AppConstants.KEY_LINE_ITEM_LIST, lineItems);
                                        startActivity(intent);
                                    }

                                }
                                // dismiss progress dialog
                                DialogUtils.dismissProgressDialog(progressDialog);

                            }
                        });
                        requestOrderItems.execute();
                        break;
                }
            }
        });
    }

    private void cancelOrderDialog(final String orderId) {

        DialogUtils.showDialogPrompt(mActivity, null, getString(R.string.cancel_order_item), getString(R.string.dialog_btn_yes), getString(R.string.dialog_btn_no), true, new DialogUtils.DialogActionListener() {
            @Override
            public void onPositiveClick() {
                // start progress dialog
                final ProgressDialog progressDialog = DialogUtils.showProgressDialog(mActivity, getString(R.string.msg_loading), false);

                RequestCancelOrder cancelOrder = new RequestCancelOrder(mContext, orderId);
                cancelOrder.setResponseListener(new ResponseListener() {
                    @Override
                    public void onResponse(Object data) {
                        // dismiss progress dialog
                        DialogUtils.dismissProgressDialog(progressDialog);

                        if (data != null) {

                            OrderItem orderItem = (OrderItem) data;

                            if (!orderItem.orderId.isEmpty()) {
                                loadOrderList();
                            } else {
                                Toast.makeText(mContext, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
                cancelOrder.execute();
            }
        });

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
