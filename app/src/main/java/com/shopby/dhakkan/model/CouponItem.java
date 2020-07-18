package com.shopby.dhakkan.model;

import java.util.ArrayList;

/**
 * Created by Nasir on 3/29/17.
 */


public class CouponItem {

    public int couponId;
    public String couponCode;
    public float amount ;
    public String description;
    public String discountType;
    public ArrayList<Integer> productIds;
    public String expireDate;


    public CouponItem(int couponId, String couponCode, float amount, String description, String discountType, ArrayList<Integer> productIds, String expireDate) {
        this.couponId = couponId;
        this.couponCode = couponCode;
        this.amount = amount;
        this.description = description;
        this.discountType = discountType;
        this.productIds = productIds;
        this.expireDate = expireDate;
    }
}
