package com.shopby.dhakkan.network.helper;

import android.content.Context;
import android.util.Log;

import com.shopby.dhakkan.network.http.BaseHttp;
import com.shopby.dhakkan.network.http.ResponseListener;
import com.shopby.dhakkan.network.params.HttpParams;

/**
 * Created by Nasir on 5/11/17.
 */

public class RequestCustomerExistence extends BaseHttp {
    private Object object;
    private ResponseListener responseListener;

    public RequestCustomerExistence(Context context, int customerId) {
        super(context, HttpParams.API_EDIT_CUSTOMER + customerId);
    }

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    @Override
    public void onBackgroundTask(String response) {
        if (response != null && !response.isEmpty()) {
            Log.e("Response", ": " + response);
            object = response;
        }
    }

    @Override
    public void onTaskComplete() {
        if (responseListener != null) {
            responseListener.onResponse(object);
        }
    }
}
