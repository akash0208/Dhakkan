package com.shopby.dhakkan.network.helper;

import android.content.Context;

import com.shopby.dhakkan.network.http.BaseHttp;
import com.shopby.dhakkan.network.params.HttpParams;
import com.shopby.dhakkan.network.parser.ParserKey;

import org.json.JSONObject;

/**
 * Created by Nasir on 4/07/17.
 */

public class RequestOrderNote extends BaseHttp {

    public RequestOrderNote(Context context, String orderId) {
        super(context, HttpParams.API_ORDER_DETAILS + orderId + HttpParams.API_ORDER_NOTES);
    }

    public void buildParams(String orderNote){
        JSONObject jObjOrder = new JSONObject();
        try {
            jObjOrder.put(ParserKey.KEY_NOTE,  orderNote);
            jObjOrder.put(ParserKey.KEY_FROM_CUSTOMER,  true);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        postJsonParams(jObjOrder.toString());
    }

    @Override
    public void onBackgroundTask(String response) {
        //Log.e("Response : ", response);
    }

    @Override
    public void onTaskComplete() {

    }
}
