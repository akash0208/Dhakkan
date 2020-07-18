package com.shopby.dhakkan.network.parser;

import com.shopby.dhakkan.model.CouponItem;
import com.shopby.dhakkan.model.LineItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nasir on 8/13/2016.
 */
public class CouponItemParser {

    public CouponItem getCouponItem(String response) {
        if (response != null) {
            try {

                JSONObject jsonObject = new JSONObject(response);

                int couponId = 0;
                float amount = 0;
                String couponCode = null, description = null, discountType = null, expireDate = null;
                ArrayList<Integer> productIds = new ArrayList<>();

                if (jsonObject.has(ParserKey.KEY_ID)) {
                    couponId = jsonObject.getInt(ParserKey.KEY_ID);
                }
                if (jsonObject.has(ParserKey.KEY_COUPON_CODE)) {
                    couponCode = jsonObject.getString(ParserKey.KEY_COUPON_CODE);
                }
                if (jsonObject.has(ParserKey.KEY_COUPON_AMOUNT)) {
                    amount = Float.parseFloat(jsonObject.getString(ParserKey.KEY_COUPON_AMOUNT));
                }
                if (jsonObject.has(ParserKey.KEY_DESCRIPTION)) {
                    description = jsonObject.getString(ParserKey.KEY_DESCRIPTION);
                }
                if (jsonObject.has(ParserKey.KEY_DISCOUNT_TYPE)) {
                    discountType = jsonObject.getString(ParserKey.KEY_DISCOUNT_TYPE);
                }
                if (jsonObject.has(ParserKey.KEY_EXPIRE_DATE)) {
                    String tempExpireDate = jsonObject.getString(ParserKey.KEY_EXPIRE_DATE);

                    // split one date portion by character 'T'
                    String[] parts = tempExpireDate.split("T");
                    expireDate = parts[0];

                }
                if (jsonObject.has(ParserKey.KEY_PRODUCT_IDS)) {
                    //couponItems.addAll(getOrderItemList(jsonObject.getJSONArray(ParserKey.KEY_LINE_ITEMS)));
                }
                return new CouponItem(couponId, couponCode, amount, description, discountType, productIds, expireDate);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // return order list
    public ArrayList<CouponItem> getCouponList(String response) {
        if (response != null) {
            ArrayList<CouponItem> couponItems = new ArrayList<>();
            //JSONObject jsonObject;

            try {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        //jsonObject = jsonArray.getJSONObject(i);
                        CouponItem couponItem = getCouponItem(jsonArray.get(i).toString());

                        if (couponItem != null) {
                            couponItems.add(couponItem);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return couponItems;
        }
        return null;
    }

    // return order items
    public ArrayList<LineItem> getOrderItemList(JSONArray jsonArray) {
        if (jsonArray != null) {
            ArrayList<LineItem> dataList = new ArrayList<>();
            JSONObject jsonObject;

            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        jsonObject = jsonArray.getJSONObject(i);

                        int id = 0, quantity = 0;
                        float price = 0;
                        String name = null;

                        if (jsonObject.has(ParserKey.KEY_PRODUCT_ID)) {
                            id = jsonObject.getInt(ParserKey.KEY_PRODUCT_ID);
                        }
                        if (jsonObject.has(ParserKey.KEY_NAME)) {
                            name = jsonObject.getString(ParserKey.KEY_NAME);
                        }
                        if (jsonObject.has(ParserKey.KEY_PRICE)) {
                            price = Float.parseFloat(jsonObject.getString(ParserKey.KEY_PRICE));
                        }
                        if (jsonObject.has(ParserKey.KEY_QUANTITY)) {
                            quantity = jsonObject.getInt(ParserKey.KEY_QUANTITY);
                        }

                        if (name != null) {
                            // TODO: Check its dependency
                            dataList.add(new LineItem("", "", id, quantity));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dataList;
        }
        return null;
    }
}
