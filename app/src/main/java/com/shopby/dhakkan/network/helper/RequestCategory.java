package com.shopby.dhakkan.network.helper;

import android.content.Context;
import android.util.Log;

import com.shopby.dhakkan.network.http.BaseHttp;
import com.shopby.dhakkan.network.http.ResponseListener;
import com.shopby.dhakkan.network.params.HttpParams;
import com.shopby.dhakkan.network.parser.CategoryParser;

/**
 * Created by Nasir on 5/11/17.
 */

public class RequestCategory extends BaseHttp {
    private Object object;
    private ResponseListener responseListener;

    public RequestCategory(Context context) {
        super(context, HttpParams.API_CATEGORIES);
    }

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    @Override
    public void onBackgroundTask(String response) {
        if (response != null && !response.isEmpty()) {
            Log.e("Response", "Categories: " + response);
            object = new CategoryParser().getCategory(response);
        }
    }

    @Override
    public void onTaskComplete() {
        if (responseListener != null) {
            responseListener.onResponse(object);
        }
    }
}
