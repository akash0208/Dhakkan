package com.shopby.dhakkan.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.shopby.dhakkan.R;

import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.data.preference.AppPreference;
import com.shopby.dhakkan.data.preference.PrefKey;
import com.shopby.dhakkan.adapter.CartListAdapter;
import com.shopby.dhakkan.data.sqlite.CartDBController;
import com.shopby.dhakkan.model.CartItem;
import com.shopby.dhakkan.model.LineItem;
import com.shopby.dhakkan.utils.ActivityUtils;
import com.shopby.dhakkan.utils.DialogUtils;

import java.util.ArrayList;

/**
 * Created by Nasir on 7/11/17.
 */

public class CartListActivity extends BaseActivity {

    // initialize variables
    private Context mContext;
    private Activity mActivity;
    private RecyclerView rvCartList;
    private ArrayList<CartItem> cartList;
    private CartListAdapter cartListAdapter;
    private int selectedCounter = 0;
    private float totalPrice = 0;

    // initialize View
    private TextView tvTotalPrice, tvSelectionCount, emptyView;
    private CheckBox checkBoxAll;
    private Button btnBuy;
    private LinearLayout lytSelectionAll, footerView;

    private TextView info_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
        initView();
        loadCartData();
        loadUiData();
        initLister();
    }

    private void initVariables() {
        mContext = getApplicationContext();
        mActivity = CartListActivity.this;
        cartList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_cart_list);

        initToolbar();
        enableBackButton();
        setToolbarTitle(getString(R.string.cart_list));
        initLoader();

        rvCartList = findViewById(R.id.rvCartList);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvSelectionCount = findViewById(R.id.tvSelectionCount);
        emptyView = findViewById(R.id.emptyView);
        checkBoxAll = findViewById(R.id.checkBoxAll);
        lytSelectionAll = findViewById(R.id.lytSelectionAll);
        footerView = findViewById(R.id.footerView);
        btnBuy = findViewById(R.id.btnBuy);
        info_text = findViewById(R.id.info_text);

        // init RecyclerView
        rvCartList.setHasFixedSize(true);

        rvCartList.setLayoutManager(new LinearLayoutManager(mActivity));
        cartListAdapter = new CartListAdapter(mContext, cartList);

        rvCartList.setAdapter(cartListAdapter);

    }

    private void initLister() {

        cartListAdapter.setItemClickListener((viewItem, position) -> {

            if (viewItem.getId() == R.id.remove) {// Remove from cart list
                deleteCartItemDialog(cartList.get(position).productId);
            } else {
                ActivityUtils.getInstance().invokeProductDetails(mActivity, cartList.get(position).productId);
            }

        });
        cartListAdapter.setItemCheckedListener((view, position, isChecked) -> {
            if (isChecked) {
                try {
                    CartDBController cartController = new CartDBController(mContext);
                    cartController.open();
                    cartController.updateCartItem(cartList.get(position).productId, AppConstants.VALUE_SELECTED);
                    cartController.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    CartDBController cartController = new CartDBController(mContext);
                    cartController.open();
                    cartController.updateCartItem(cartList.get(position).productId, AppConstants.VALUE_NOT_SELECTED);
                    cartController.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            loadUiData();

        });

        checkBoxAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    CartDBController cartController = new CartDBController(mContext);
                    cartController.open();
                    cartController.updateAllCartItemSelection(AppConstants.VALUE_SELECTED);
                    cartController.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    CartDBController cartController = new CartDBController(mContext);
                    cartController.open();
                    cartController.updateAllCartItemSelection(AppConstants.VALUE_NOT_SELECTED);
                    cartController.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            loadUiData();
            cartListAdapter.notifyDataSetChanged();
        });
        btnBuy.setOnClickListener(v -> {
            if (selectedCounter > 0) {

                // store price into preference
                AppPreference.getInstance(mContext).setString(PrefKey.PAYMENT_TOTAL_PRICE, String.valueOf(totalPrice));

                boolean isRegistered = AppPreference.getInstance(mContext).getBoolean(PrefKey.REGISTERED);
                if(isRegistered) {
                    ActivityUtils.getInstance().invokeAddressActivity(mActivity, buildLineItems(), false, true);
                } else {
                    ActivityUtils.getInstance().invokeLoginAndOrder(mActivity, buildLineItems());
                }

            } else {
                Toast.makeText(mContext, "Select product", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCartData() {
        if (!cartList.isEmpty()) {
            cartList.clear();
        }
        try {
            CartDBController cartController = new CartDBController(mContext);
            cartController.open();
            cartList.addAll(cartController.getAllCartData());
            cartController.close();

            cartListAdapter.notifyDataSetChanged();
            if (cartList.isEmpty()) {
                lytSelectionAll.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(cartList.isEmpty()) {
            showEmptyView();
            info_text.setText(getString(R.string.empty_cart));
        } else {
            hideLoader();
        }
    }

    private void loadUiData() {
        if (!cartList.isEmpty()) {
            cartList.clear();
        }
        totalPrice = 0;
        selectedCounter = 0;
        try {
            CartDBController cartController = new CartDBController(mContext);
            cartController.open();
            cartList.addAll(cartController.getAllCartData());
            for (int i = 0; i < cartList.size(); i++) {
                if (cartList.get(i).isSelected == AppConstants.VALUE_SELECTED) {
                    totalPrice += cartList.get(i).price * cartList.get(i).quantity;
                    selectedCounter++;
                }
            }
            cartController.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // refresh footer view
        if (selectedCounter > 0) {
            tvTotalPrice.setText("Total: " + AppConstants.CURRENCY + totalPrice);
        } else {
            footerView.setVisibility(View.GONE);
        }

        // refresh checkbox
        if (selectedCounter > 0) {
            tvSelectionCount.setText(selectedCounter + " selected");
            footerView.setVisibility(View.VISIBLE);
            if (cartList.size() == selectedCounter) {
                checkBoxAll.setChecked(true);
            }
        } else {
            tvSelectionCount.setText(AppConstants.EMPTY_STRING);
            checkBoxAll.setChecked(false);
            footerView.setVisibility(View.GONE);
        }
    }

    private ArrayList<LineItem> buildLineItems() {
        ArrayList<LineItem> lineItemList = new ArrayList<>();
        for (int i = 0; i < cartList.size(); i++) {
            if (cartList.get(i).isSelected == AppConstants.VALUE_SELECTED) {
                lineItemList.add(new LineItem(cartList.get(i).name, cartList.get(i).attribute, cartList.get(i).productId, cartList.get(i).quantity));
            }
        }
        return lineItemList;
    }

    private void deleteCartItemDialog(final int productId) {

        DialogUtils.showDialogPrompt(mActivity, null, getString(R.string.delete_cart_item), getString(R.string.dialog_btn_yes), getString(R.string.dialog_btn_no), true, () -> {
            try {
                CartDBController cartController = new CartDBController(mContext);
                cartController.open();
                cartController.deleteCartItemById(productId);
                cartController.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadCartData();
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
