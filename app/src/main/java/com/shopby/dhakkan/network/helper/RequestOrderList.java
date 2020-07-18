package com.shopby.dhakkan.network.helper;

import android.content.Context;
import android.util.Log;

import com.shopby.dhakkan.network.http.BaseHttp;
import com.shopby.dhakkan.network.http.ResponseListener;
import com.shopby.dhakkan.network.params.HttpParams;
import com.shopby.dhakkan.network.parser.OrderItemParser;

/**
 * Created by Nasir on 5/11/17.
 */

public class RequestOrderList extends BaseHttp {
    private Object object;
    private ResponseListener responseListener;

    public RequestOrderList(Context context, String customerId) {
        super(context, HttpParams.BASE_URL+HttpParams.API_ORDER_LIST +customerId);
    }

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    @Override
    public void onBackgroundTask(String response) {

        if (response != null && !response.isEmpty()) {
            Log.e("Response", "orders list: " + response);
            object = new OrderItemParser().getOrderList(response);
        }
    }

    @Override
    public void onTaskComplete() {
        if (responseListener != null) {
            responseListener.onResponse(object);
        }
    }
}
