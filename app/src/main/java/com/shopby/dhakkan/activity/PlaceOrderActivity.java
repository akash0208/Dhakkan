package com.shopby.dhakkan.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.R;
import com.shopby.dhakkan.data.preference.AppPreference;
import com.shopby.dhakkan.data.preference.PrefKey;
import com.shopby.dhakkan.data.sqlite.CartDBController;
import com.shopby.dhakkan.model.BillingModel;
import com.shopby.dhakkan.model.LineItem;
import com.shopby.dhakkan.model.OrderItem;
import com.shopby.dhakkan.model.OrderModel;
import com.shopby.dhakkan.model.ShippingModel;
import com.shopby.dhakkan.network.helper.RequestOrder;
import com.shopby.dhakkan.network.helper.RequestOrderNote;
import com.shopby.dhakkan.network.http.ResponseListener;
import com.shopby.dhakkan.utils.ActivityUtils;
import com.shopby.dhakkan.utils.DialogUtils;

import java.util.ArrayList;

/**
 * Created by Nasir on 7/4/17.
 */

public class PlaceOrderActivity extends BaseActivity {

    // Variables
    private Context mContext;
    private Activity mActivity;

    // UI
    private TextView tvTotalPrice;
    private Button btnPlaceOrder;
    private RadioGroup rgPaymentMethod;
    private EditText transactionIDInput;

    private String paymentMethod, paymentMethodTitle;
    private String transactionID = AppConstants.EMPTY_STRING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        loadData();
        initToolbar();
        initListener();

    }

    private void initVariable() {
        mContext = getApplicationContext();
        mActivity = PlaceOrderActivity.this;
        paymentMethod = getString(R.string.title_cash_on_delivery);
    }

    private void initView() {
        setContentView(R.layout.activity_place_order);

        initToolbar();
        setToolbarTitle(getString(R.string.finalize_order));
        enableBackButton();

        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        transactionIDInput = findViewById(R.id.transactionID);

    }


    private void initListener() {
        btnPlaceOrder.setOnClickListener(v -> placeOrder());

        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rdCashOnDelivery:
                    paymentMethodTitle = getString(R.string.title_cash_on_delivery);
                    paymentMethod = getString(R.string.key_cash_on_delivery);
                    transactionIDInput.setVisibility(View.GONE);
                    break;
                case R.id.rdStripe:
                    paymentMethodTitle = getString(R.string.title_payment);
                    paymentMethod = getString(R.string.key_gateway);
                    transactionIDInput.setVisibility(View.GONE);
                    startActivityForResult(new Intent(mActivity, StripeActivity.class), 1);
                    break;
            }
        });
    }


    private void loadData() {
        tvTotalPrice.setText("Total: " + AppConstants.CURRENCY + AppPreference.getInstance(mContext).getString(PrefKey.PAYMENT_TOTAL_PRICE));
    }

    private void placeOrder() {

        transactionID = transactionIDInput.getText().toString();

        String email = AppPreference.getInstance(mContext).getString(PrefKey.EMAIL);
        String customerId = AppPreference.getInstance(mContext).getString(PrefKey.CUSTOMER_ID);

        // billing
        String firstName = AppPreference.getInstance(mContext).getString(PrefKey.FIRST_NAME);
        String lastName = AppPreference.getInstance(mContext).getString(PrefKey.LAST_NAME);
        String companyName = AppPreference.getInstance(mContext).getString(PrefKey.COMPANY_NAME);
        String address = AppPreference.getInstance(mContext).getString(PrefKey.ADDRESS);
        String city = AppPreference.getInstance(mContext).getString(PrefKey.CITY);
        String stateName = AppPreference.getInstance(mContext).getString(PrefKey.STATE_NAME);
        String postCode = AppPreference.getInstance(mContext).getString(PrefKey.POST_CODE);
        String country = AppPreference.getInstance(mContext).getString(PrefKey.COUNTRY_NAME);
        String phoneNumber = AppPreference.getInstance(mContext).getString(PrefKey.PHONE_NUMBER);

        BillingModel billingModel = new BillingModel(firstName, lastName, companyName, address, city, stateName, postCode, country, email, phoneNumber);

        // shipping
        String fistNameShip = AppPreference.getInstance(mContext).getString(PrefKey.FIRST_NAME_SHIP);
        String lastNameShip = AppPreference.getInstance(mContext).getString(PrefKey.LAST_NAME_SHIP);
        String companyNameShip = AppPreference.getInstance(mContext).getString(PrefKey.COMPANY_NAME_SHIP);
        String addressShip = AppPreference.getInstance(mContext).getString(PrefKey.ADDRESS_SHIP);
        String cityShip = AppPreference.getInstance(mContext).getString(PrefKey.CITY_SHIP);
        String stateNameShip = AppPreference.getInstance(mContext).getString(PrefKey.STATE_NAME_SHIP);
        String postCodeShip = AppPreference.getInstance(mContext).getString(PrefKey.POST_CODE_SHIP);
        String countryShip = AppPreference.getInstance(mContext).getString(PrefKey.COUNTRY_NAME_SHIP);

        ShippingModel shippingModel = new ShippingModel(fistNameShip, lastNameShip, companyNameShip, addressShip, cityShip, stateNameShip,
                postCodeShip, countryShip);

        // receive line items
        Intent intent = getIntent();
        final ArrayList<LineItem> lineItems = intent.getParcelableArrayListExtra(AppConstants.KEY_LINE_ITEM_LIST);

        if (!customerId.isEmpty()) {
            OrderModel orderModel = new OrderModel(customerId, email, paymentMethod, paymentMethodTitle, billingModel, shippingModel, lineItems, transactionID);

            final ProgressDialog progressDialog = DialogUtils.showProgressDialog(mActivity, getString(R.string.msg_loading), false);

            RequestOrder requestOrder = new RequestOrder(mActivity, orderModel);
            requestOrder.buildParams();
            requestOrder.setResponseListener(data -> {
                if (data != null) {

                    OrderItem order = (OrderItem) data;

                    if (order.orderId != null) {
                        postOderNote(order, lineItems);

                        // omit ordered cart item from cart db
                        try {
                            CartDBController cartController = new CartDBController(mContext);
                            cartController.open();
                            for (int i = 0; i < order.lineItems.size(); i++) {
                                cartController.deleteCartItemById(order.lineItems.get(i).productId);
                            }
                            cartController.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        ActivityUtils.getInstance().invokeOrder(mActivity, order.orderId);
                    } else {
                        finish();
                        Toast.makeText(mContext, getString(R.string.order_failed), Toast.LENGTH_LONG).show();
                    }
                } else {
                    finish();
                    Toast.makeText(mContext, getString(R.string.order_failed), Toast.LENGTH_LONG).show();
                }

                DialogUtils.dismissProgressDialog(progressDialog);

            });
            requestOrder.execute();
        } else {
            System.out.print("Customer id is empty...");
        }
    }

    // post product attribute as a note
    private void postOderNote(OrderItem order, ArrayList<LineItem> arrayList) {

        StringBuilder note = new StringBuilder("Product attributes: ");
        for (LineItem lineItem : arrayList) {
            note.append(lineItem.productName).append("-").append(lineItem.productAttribute).append(" | ");
        }

        RequestOrderNote requestOrderNote = new RequestOrderNote(mContext, order.orderId);
        requestOrderNote.buildParams(note.toString());
        requestOrderNote.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                transactionIDInput.setText(data.getStringExtra("stripe_token"));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
