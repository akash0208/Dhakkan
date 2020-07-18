package com.shopby.dhakkan.network.helper;

import android.content.Context;
import android.util.Log;

import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.model.Customer;
import com.shopby.dhakkan.network.http.BaseHttp;
import com.shopby.dhakkan.network.http.ResponseListener;
import com.shopby.dhakkan.network.params.HttpParams;
import com.shopby.dhakkan.network.parser.CustomerParser;
import com.shopby.dhakkan.network.parser.ParserKey;
import com.shopby.dhakkan.utils.AppUtility;

import org.json.JSONObject;

/**
 * Created by Nasir on 4/07/17.
 */

public class RequestCreateCustomer extends BaseHttp {
    private Object object;
    private ResponseListener responseListener;
    private Customer customer;

    public RequestCreateCustomer(Context context, Customer customer) {
        super(context, HttpParams.API_CUSTOMER);
        this.customer = customer;
    }

    public void buildParams() {
        JSONObject jObjCustomer = new JSONObject();
        try {
            jObjCustomer.put(ParserKey.KEY_EMAIL, customer.email);
            jObjCustomer.put(ParserKey.KEY_PASSWORD, AppConstants.DEFAULT_PASSWORD);
            jObjCustomer.put(ParserKey.KEY_FIRST_NAME, customer.firstName);
            jObjCustomer.put(ParserKey.KEY_LAST_NAME, customer.lastName);
            jObjCustomer.put(ParserKey.KEY_USER_NAME, AppUtility.getUserFromEmail(customer.email));

        } catch (Exception e) {
            e.printStackTrace();
        }

        postJsonParams(jObjCustomer.toString());
    }

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    @Override
    public void onBackgroundTask(String response) {
        if (response != null && !response.isEmpty()) {
            Log.e("Response : ", response);
            object = new CustomerParser().getCustomer(response);
        }
    }

    @Override
    public void onTaskComplete() {
        if (responseListener != null) {
            responseListener.onResponse(object);
        }
    }

}
