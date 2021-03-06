package com.shopby.dhakkan.network.helper;

import android.content.Context;
import android.util.Log;

import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.network.http.BaseHttp;
import com.shopby.dhakkan.network.http.ResponseListener;
import com.shopby.dhakkan.network.params.HttpParams;
import com.shopby.dhakkan.network.parser.CustomerParser;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Nasir on 4/07/17.
 */

public class GetCustomerByEmail extends BaseHttp {
    private Object object;
    private ResponseListener responseListener;

    public GetCustomerByEmail(Context context, String customerEmail) {
        super(context, HttpParams.API_SEARCH_CUSTOMER+customerEmail);
    }

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    @Override
    public void onBackgroundTask(String response) {

        if (response != null && !response.isEmpty()) {
            Log.e("Response : ", response);
            try {
                object = new CustomerParser().getCustomer(new JSONArray(response).get(AppConstants.INDEX_ZERO).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onTaskComplete() {
        if (responseListener != null) {
            responseListener.onResponse(object);
        }
    }
}
