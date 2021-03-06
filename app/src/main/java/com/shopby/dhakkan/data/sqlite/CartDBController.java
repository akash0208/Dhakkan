package com.shopby.dhakkan.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.model.CartItem;
import java.util.ArrayList;

public class CartDBController {

    private DatabaseHelper dbHelper;
    private Context mContext;
    private SQLiteDatabase database;

    public CartDBController(Context context) {
        mContext = context;
    }

    public CartDBController open() throws SQLException {
        dbHelper = new DatabaseHelper(mContext);
        database = dbHelper.getWritableDatabase();
        return this;
    }
    public void close() {
        dbHelper.close();
    }

    public long insertCartItem(int productId, String name, String images, float price, int quantity, String attribute) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.KEY_PRODUCT_ID, productId);
        contentValue.put(DatabaseHelper.KEY_NAME, name);
        contentValue.put(DatabaseHelper.KEY_IMAGES, images);
        contentValue.put(DatabaseHelper.KEY_PRICE, price);
        contentValue.put(DatabaseHelper.KEY_QUANTITY, quantity);
        contentValue.put(DatabaseHelper.KEY_ATTRIBUTE, attribute);

        return database.insert(DatabaseHelper.TABLE_CART, null, contentValue);
    }

    public ArrayList<CartItem> getAllCartData()
    {
        ArrayList<CartItem> cartList = new ArrayList<>();

        Cursor cursor = database.rawQuery("select * from "+DatabaseHelper.TABLE_CART, null);
        if (cursor != null && cursor.getCount() > 0) {
            try {
                cursor.moveToFirst();
                while (cursor.isAfterLast() == false) {

                    int productId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_PRODUCT_ID));
                    String name   = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_NAME));
                    String images = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_IMAGES));
                    float price   = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.KEY_PRICE));
                    int quantity = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_QUANTITY));
                    String attribute= cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ATTRIBUTE));
                    int isSelected= cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_IS_SELECTED));

                    if (productId > AppConstants.VALUE_ZERO) {
                        cartList.add(new CartItem(productId, name, images, price, quantity, attribute, isSelected));
                    }
                    cursor.moveToNext();
                }
            } catch (Exception ex) {
            }
        }
        return cartList;
    }

    public int updateCartItem(int productId, int isSelected) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_IS_SELECTED, isSelected);

        int updateStatus = database.update(DatabaseHelper.TABLE_CART, contentValues,
                DatabaseHelper.KEY_PRODUCT_ID + " = " + productId, null);
        return updateStatus;
    }
    public int updateAllCartItemSelection(int isSelected) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_IS_SELECTED, isSelected);
        int updateStatus = database.update(DatabaseHelper.TABLE_CART, contentValues,null, null);
        return updateStatus;
    }
    public boolean isAlreadyAddedToCart(int productId) {
        Cursor cursor = database.rawQuery("select "+DatabaseHelper.KEY_PRODUCT_ID+" from " + DatabaseHelper.TABLE_CART + " where " + DatabaseHelper.KEY_PRODUCT_ID + "=" + productId + "", null);
        if(cursor!=null && cursor.getCount()>0){
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }
    public int getItemQuantity(int productId) {
        Cursor cursor = database.rawQuery("select "+DatabaseHelper.KEY_QUANTITY+" from " + DatabaseHelper.TABLE_CART + " where " + DatabaseHelper.KEY_PRODUCT_ID + "=" + productId + "", null);
        if(cursor!=null && cursor.getCount()>0){
            int quantity = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_QUANTITY));
            cursor.close();
            return quantity;
        }
        cursor.close();
        return 0;
    }
    public void deleteCartItemById(long productId) {
        database.delete(DatabaseHelper.TABLE_CART, DatabaseHelper.KEY_PRODUCT_ID + "=" + productId, null);
    }

    public void deleteAllCartData() {
        database.delete(DatabaseHelper.TABLE_CART, null, null);
    }

    public int countCartProduct(){
        int numOfRows = (int) DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE_CART);
        return numOfRows;
    }

    private void dropCartTable() {
        String sql = "drop table " + DatabaseHelper.TABLE_CART;
        try {
            database.execSQL(sql);
        } catch (SQLException e) {

        }
    }
//    public class CustomComparator implements Comparator<ProductResponseModel> {
//        @Override
//        public int compare(ProductResponseModel p1, ProductResponseModel p2) {
//            Long t1 = p1.postTimeStamp;
//            Long t2 = p2.postTimeStamp;
//            return t1.compareTo(t2);
//        }
//    }
}
