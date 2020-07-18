package com.shopby.dhakkan.model;

import java.util.ArrayList;

/**
 * Created by Nasir on 3/29/17.
 *
 */

//TODO Need to add more product details attributes for parsing more data

public class ProductDetail {
    public int id = 0;
    public String name;
    public String shortDescription;
    public int categoryId;
    public String categoryName;
    public String linkAddress;
    public ArrayList<String> imageList;
    public ArrayList<Integer> relatedProdIds;
    public ArrayList<Category> relatedCategoryList;
    public float regularPrice;
    public float sellPrice;
    public int totalSale;
    public String priceHtml;
    public String type;
    public String createdDateTime;
    public String modifiedDateTime;
    public String description;
    public int totalRating;
    public float averageRating;
    public boolean publishStatus;
    public boolean onSaleStatus;
    public boolean inStockStatus;
    public boolean isReviewAllow;
    public ArrayList<ProductAttribute> attributes;

    public ProductDetail(int id, String name, String shortDescription, int categoryId, String categoryName, String linkAddress, ArrayList<String> imageList, ArrayList<Integer> relatedProdIds,
                         ArrayList<Category> relatedCategoryList, float regularPrice, float sellPrice, int totalSale, String priceHtml,
                         String type, String createdDateTime, String modifiedDateTime, String description, int totalRating, float averageRating,
                         boolean publishStatus, boolean onSaleStatus, boolean inStockStatus, boolean isReviewAllow, ArrayList<ProductAttribute> attributes) {
        this.id = id;
        this.name = name;
        this.shortDescription = shortDescription;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.linkAddress = linkAddress;
        this.imageList = imageList;
        this.relatedProdIds = relatedProdIds;
        this.relatedCategoryList = relatedCategoryList;
        this.regularPrice = regularPrice;
        this.sellPrice = sellPrice;
        this.totalSale = totalSale;
        this.priceHtml = priceHtml;
        this.type = type;
        this.createdDateTime = createdDateTime;
        this.modifiedDateTime = modifiedDateTime;
        this.description = description;
        this.totalRating = totalRating;
        this.averageRating = averageRating;
        this.publishStatus = publishStatus;
        this.onSaleStatus = onSaleStatus;
        this.inStockStatus = inStockStatus;
        this.isReviewAllow = isReviewAllow;
        this.attributes = attributes;
    }
}
