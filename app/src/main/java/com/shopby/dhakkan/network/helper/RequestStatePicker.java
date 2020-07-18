package com.shopby.dhakkan.network.helper;

import android.content.Context;
import android.util.Log;

import com.shopby.dhakkan.network.http.BaseHttp;
import com.shopby.dhakkan.network.http.ResponseListener;
import com.shopby.dhakkan.network.params.HttpParams;
import com.shopby.dhakkan.network.parser.StateParser;

/**
 * Created by Nasir on 4/07/17.
 */

public class RequestStatePicker extends BaseHttp {
    private Object object;
    private ResponseListener responseListener;

    public RequestStatePicker(Context context, String countryCode) {
        super(context, HttpParams.API_GET_All_COUNTRIES + countryCode + HttpParams.API_GET_RELATED_STATES1);
    }

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    @Override
    public void onBackgroundTask(String response) {

        if (response != null && !response.isEmpty()) {
            Log.e("Response : ", response);
            object = new StateParser().getStates(response);
        }
    }

    @Override
    public void onTaskComplete() {
        if (responseListener != null) {
            responseListener.onResponse(object);
        }
    }
}
