package com.shopby.dhakkan.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.shopby.dhakkan.adapter.NotificationAdapter;
import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.data.sqlite.NotificationDBController;
import com.shopby.dhakkan.listener.OnItemClickListener;
import com.shopby.dhakkan.model.NotificationModel;
import com.shopby.dhakkan.R;
import com.shopby.dhakkan.utils.ActivityUtils;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private Context mContext;
    private Activity mActivity;

    private Toolbar mToolbar;
    private RecyclerView recyclerView;
    private NotificationAdapter mAdapter;
    private ArrayList<NotificationModel> dataList;

    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = NotificationActivity.this;
        mContext = mActivity.getApplicationContext();

        initVars();
        initialView();
        initFunctionality();
        initialListener();
    }

    private void initVars() {
        dataList = new ArrayList<>();
    }

    private void initialView() {
        setContentView(R.layout.activity_notification);
        mToolbar = findViewById(R.id.toolbar);
        emptyView = findViewById(R.id.emptyView);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.notifications));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //productList
        recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new NotificationAdapter(mActivity, dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(mAdapter);
    }

    private void initFunctionality() {
        NotificationDBController notifyController = new NotificationDBController(mContext);
        notifyController.open();
        dataList.addAll(notifyController.getAllNotification());
        notifyController.close();

        if (dataList != null && !dataList.isEmpty()) {
            emptyView.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void initialListener() {

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemListener(View view, int position) {

                String notificationType = dataList.get(position).notificationType;

                if (notificationType.equals(AppConstants.NOTIFY_TYPE_MESSAGE)) {
                    ActivityUtils.getInstance().invokeNotifyContentActivity(mActivity, dataList.get(position).title, dataList.get(position).message);
                } else if (notificationType.equals(AppConstants.NOTIFY_TYPE_PRODUCT)) {
                    ActivityUtils.getInstance().invokeProductDetails(mActivity, dataList.get(position).productId);
                } else if (notificationType.equals(AppConstants.NOTIFY_TYPE_URL)) {
                    if (dataList.get(position).url != null && !dataList.get(position).url.isEmpty()) {
                        ActivityUtils.getInstance().invokeWebPageActivity(mActivity, getResources().getString(R.string.app_name), dataList.get(position).url);
                    }
                }
                updateStatus(dataList.get(position).id);
            }
        });

    }

    private void updateStatus(int id) {
        NotificationDBController notifyController = new NotificationDBController(mContext);
        notifyController.open();
        notifyController.updateStatus(id, true);
        notifyController.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
