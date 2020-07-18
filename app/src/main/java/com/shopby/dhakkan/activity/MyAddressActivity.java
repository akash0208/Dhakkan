package com.shopby.dhakkan.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.shopby.dhakkan.JSONParser;
import com.shopby.dhakkan.R;

import com.shopby.dhakkan.checksum;
import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.data.preference.AppPreference;
import com.shopby.dhakkan.data.preference.PrefKey;
import com.shopby.dhakkan.data.sqlite.CartDBController;
import com.shopby.dhakkan.listener.ListDialogActionListener;
import com.shopby.dhakkan.model.BillingModel;
import com.shopby.dhakkan.model.Customer;
import com.shopby.dhakkan.model.LineItem;
import com.shopby.dhakkan.model.OrderItem;
import com.shopby.dhakkan.model.OrderModel;
import com.shopby.dhakkan.model.ShippingModel;
import com.shopby.dhakkan.model.Zone;
import com.shopby.dhakkan.network.helper.RequestCountryPicker;
import com.shopby.dhakkan.network.helper.RequestEditCustomer;
import com.shopby.dhakkan.network.helper.RequestOrder;
import com.shopby.dhakkan.network.helper.RequestOrderNote;
import com.shopby.dhakkan.network.helper.RequestStatePicker;
import com.shopby.dhakkan.data.cache.ProfileData;
import com.shopby.dhakkan.utils.ActivityUtils;
import com.shopby.dhakkan.utils.DialogUtils;
import com.shopby.dhakkan.utils.JsonParse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Nasir on 7/4/17.
 */

public class MyAddressActivity extends BaseActivity implements PaytmPaymentTransactionCallback {

    private Context mContext;
    private Activity mActivity;
    private ArrayList<Zone> countryList;
    private ArrayList<String> stateList;

    String mid = "fZmbSK51126174754857";
    String amount = AppPreference.getInstance(this).getString(PrefKey.PAYMENT_TOTAL_PRICE);
    String oid, cid;

    String[] intArray = new String[]{
            "ANDAMAN & NICOBAR ISLANDS", "ANDHRA PRADESH", "ARUNACHAL PRADESH", "ASSAM",
            "BIHAR",
            "CHANDIGARH", "CHHATTISGARH",
            "DADRA & NAGAR HAVELI", "DAMAN & DIU", "DELHI",
            "GOA", "GUJARAT",
            "HARYANA", "HIMACHAL PRADESH",
            "JAMMU & KASHMIR", "JHARKHAND",
            "KARNATAKA", "KERALA",
            "LAKSHADWEEP",
            "MADHYA PRADESH", "MAHARASHTRA", "MANIPUR", "MEGHALAYA", "MIZORAM",
            "NAGALAND",
            "ODISHA",
            "PUDUCHERRY", "PUNJAB",
            "RAJASTHAN",
            "SIKKIM",
            "TAMIL NADU", "TELANGANA", "TRIPURA",
            "UTTAR PRADESH", "UTTARAKHAND",
            "WEST BENGAL"};

    // User info/ billing
    private EditText edtFirstName, edtLastName, edtEmail, edtUserName, edtAddress, edtCity, edtPostCode, edtPhoneNumber;
    private TextView tvStateName, tvCountry, tvTotalPrice;
    private LinearLayout spinnerState;

    // shipping
    private EditText edtFirstNameShip, edtLastNameShip, edtAddressShip, edtCityShip, edtPostCodeShip;
    private TextView tvStateNameShip, tvCountryShip;
    private LinearLayout spinnerStateShip;

    // UI
    private CheckBox chkShipToAddress;
    private RelativeLayout lytShippingAddress;
    private Button btnNext;

    // edit customer info
    private Customer customer = null;
    private boolean editOnly = false;
    private ArrayList<LineItem> lineItems;

    // coupon Layout
    LinearLayout couponLayout, AppliedCoupon;
    BottomSheetDialog dialog;
    View view;
    FirebaseFirestore db;
    String price;
    Button applyCoupon, deletePromo;
    EditText couponCode;
    TextView codeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        loadData();
        initListener();

    }

    private void initVariable() {
        mContext = getApplicationContext();
        mActivity = MyAddressActivity.this;
        countryList = new ArrayList<>();
        stateList = new ArrayList<>();
        lineItems = new ArrayList<>();

        stateList.addAll(Arrays.asList(intArray));

        Intent intent = getIntent();
        if (intent.hasExtra(AppConstants.KEY_LINE_ITEM_LIST)) {
            lineItems = intent.getParcelableArrayListExtra(AppConstants.KEY_LINE_ITEM_LIST);
        }
        if (intent.hasExtra(AppConstants.KEY_EDIT_ONLY)) {
            editOnly = intent.getBooleanExtra(AppConstants.KEY_EDIT_ONLY, false);
        }
        db = FirebaseFirestore.getInstance();

    }

    @SuppressLint("InflateParams")
    private void initView() {

        setContentView(R.layout.activity_my_address);

        initToolbar();
        enableBackButton();
        setToolbarTitle(getString(R.string.menu_my_address));

        chkShipToAddress = findViewById(R.id.chkShipToAddress);
        lytShippingAddress = findViewById(R.id.lytShippingAddress);
        btnNext = findViewById(R.id.btnNext);

        // billing
        edtFirstName = findViewById(R.id.edtFirstName);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        edtUserName = findViewById(R.id.edtUserName);
        edtAddress = findViewById(R.id.edtAddress);
        edtCity = findViewById(R.id.edtCity);
        edtPostCode = findViewById(R.id.edtPostCode);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        spinnerState = findViewById(R.id.spinnerState);
        tvStateName = findViewById(R.id.tvStateName);
        tvCountry = findViewById(R.id.tvCountry);

        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        // shipping
        edtFirstNameShip = findViewById(R.id.edtFirstNameShip);
        edtLastNameShip = findViewById(R.id.edtLastNameShip);
        edtAddressShip = findViewById(R.id.edtAddressShip);
        edtCityShip = findViewById(R.id.edtCityShip);
        edtPostCodeShip = findViewById(R.id.edtPostCodeShip);
        spinnerStateShip = findViewById(R.id.spinnerStateShip);
        tvStateNameShip = findViewById(R.id.tvStateNameShip);
        tvCountryShip = findViewById(R.id.tvCountryShip);

        couponLayout = findViewById(R.id.applyCoupon);


        codeName = findViewById(R.id.code_name);
        deletePromo = findViewById(R.id.delete_promo);
        AppliedCoupon = findViewById(R.id.appliedCoupon);
        view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);

        applyCoupon = view.findViewById(R.id.apply_coupon_button);
        couponCode = view.findViewById(R.id.enterCoupon);

        price = AppPreference.getInstance(mContext).getString(PrefKey.PAYMENT_TOTAL_PRICE);

        dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

    }

    private void initListener() {

        spinnerState.setOnClickListener(v -> showZoneListDialog(mActivity, getString(R.string.state), stateList, position -> tvStateName.setText(stateList.get(position))));
        spinnerStateShip.setOnClickListener(v -> showZoneListDialog(mActivity, getString(R.string.state), stateList, position -> tvStateNameShip.setText(stateList.get(position))));

        chkShipToAddress.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // hide shipping address
                lytShippingAddress.setVisibility(View.GONE);
            } else {
                lytShippingAddress.setVisibility(View.VISIBLE);
            }
        });


        btnNext.setOnClickListener(v -> {

            if (isValidateInput()) {

                storeUserInput();

                if (editOnly) {
                    if (customer != null) {
                        editCustomer(customer);
                    }
                } else {
//                    ActivityUtils.getInstance().invokePlaceOrder(mActivity, lineItems);

                    String email = edtEmail.getText().toString();
                    String phone = edtPhoneNumber.getText().toString();

                    String buyername = edtFirstName.getText().toString() + edtLastName.getText().toString();

                    final Activity activity = this;

                    StringBuilder note = new StringBuilder();
                    for (LineItem lineItem : lineItems) {
                        note.append(lineItem.productName).append("-").append(lineItem.productAttribute);
                    }

//                    InstamojoPay instamojoPay = new InstamojoPay();
//                    IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
//                    registerReceiver(instamojoPay, filter);
//                    JSONObject pay = new JSONObject();
//                    try {
//                        pay.put("email", email);
//                        pay.put("phone", phone);
//                        pay.put("purpose", note);
//                        pay.put("amount", price);
//                        pay.put("name", buyername);
//                        pay.put("send_sms", false);
//                        pay.put("send_email", false);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    initListenerr();
//                    instamojoPay.start(activity, pay, listener);

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
                    }

//                    PaytmPGService Service = PaytmPGService.getProductionService();
//                    HashMap<String, String> paramMap = new HashMap<>();
//                    paramMap.put( "MID" , "fZmbSK51126174754857");
//                    // Key in your staging and production MID available in your dashboard
//                    paramMap.put( "ORDER_ID" , "order1");
//                    paramMap.put( "CUST_ID" , email);
//                    paramMap.put( "MOBILE_NO" , phone);
//                    paramMap.put( "EMAIL" , email);
//                    paramMap.put( "CHANNEL_ID" , "WAP");
//                    paramMap.put( "TXN_AMOUNT" , amount);
//                    paramMap.put( "WEBSITE" , "DEFAULT");
//                    // This is the staging value. Production value is available in your dashboard
//                    paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
//                    // This is the staging value. Production value is available in your dashboard
//                    paramMap.put( "CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=order1");
//                    paramMap.put( "CHECKSUMHASH" , "w2QDRMgp1234567JEAPCIOmNgQvsi+BhpqijfM9KvFfRiPmGSt3Ddzw+oTaGCLneJwxFFq5mqTMwJXdQE2EzK4px2xruDqKZjHupz9yXev4=");
//                    PaytmOrder Order = new PaytmOrder(paramMap);

                    placeOrder("id");

                }

            } else {
                Toast.makeText(mContext, "Please fill required fields.", Toast.LENGTH_SHORT).show();
            }
        });

        couponLayout.setOnClickListener(v -> dialog.show());

        applyCoupon.setOnClickListener(v -> {
            applyCoupon.setEnabled(false);
            String coupon = couponCode.getText().toString();
            DocumentReference docRef = db.collection("coupon").document("uniqueCode");
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Log.d("document", "DocumentSnapshot data: " + document.getData());
                        String s = Objects.requireNonNull(document.get("C1")).toString();
                        if (s.equals(coupon)) {
                            couponLayout.setVisibility(View.GONE);
                            AppliedCoupon.setVisibility(View.VISIBLE);
                            codeName.setText("'" + coupon + "'" + " COUPON APPLIED!");

                            String intValue = coupon.replaceAll("[^0-9]", "");

                            float val = Float.valueOf(AppPreference.getInstance(mContext).getString(PrefKey.PAYMENT_TOTAL_PRICE));

                            float f = Float.valueOf(intValue.trim());
                            int discountedPrice = (int) (val - (val * (f / 100.0f)));

                            price = String.valueOf(discountedPrice);


                            tvTotalPrice.setText("Total: " + AppConstants.CURRENCY + discountedPrice + ".0");
                            couponCode.setText("");

                            dialog.dismiss();

                        } else {
                            applyCoupon.setEnabled(true);
                            couponCode.setError("Invalid Code!");
                        }
                    } else {
                        applyCoupon.setEnabled(true);
                        Log.d("document", "No such document");
                    }
                } else {
                    applyCoupon.setEnabled(true);
                    Log.d("document", "get failed with ", task.getException());
                }
            });
        });

        deletePromo.setOnClickListener(v -> {
            couponLayout.setVisibility(View.VISIBLE);
            AppliedCoupon.setVisibility(View.GONE);
            applyCoupon.setEnabled(true);
            Toast.makeText(mContext, "Coupon Removed!", Toast.LENGTH_SHORT).show();
            price = AppPreference.getInstance(mContext).getString(PrefKey.PAYMENT_TOTAL_PRICE);
            tvTotalPrice.setText("Total: " + AppConstants.CURRENCY + AppPreference.getInstance(mContext).getString(PrefKey.PAYMENT_TOTAL_PRICE));
        });
    }

    private void storeUserInput() {

        // get User info/ billing address data

        AppPreference.getInstance(mContext).setString(PrefKey.FIRST_NAME, edtFirstName.getText().toString());
        AppPreference.getInstance(mContext).setString(PrefKey.LAST_NAME, edtLastName.getText().toString());
        AppPreference.getInstance(mContext).setString(PrefKey.EMAIL, edtEmail.getText().toString());
        AppPreference.getInstance(mContext).setString(PrefKey.USER_NAME, edtUserName.getText().toString());
        AppPreference.getInstance(mContext).setString(PrefKey.ADDRESS, edtAddress.getText().toString());
        AppPreference.getInstance(mContext).setString(PrefKey.CITY, edtCity.getText().toString());
        AppPreference.getInstance(mContext).setString(PrefKey.STATE_NAME, tvStateName.getText().toString());
        AppPreference.getInstance(mContext).setString(PrefKey.POST_CODE, edtPostCode.getText().toString());
        AppPreference.getInstance(mContext).setString(PrefKey.COUNTRY_NAME, tvCountry.getText().toString());
        AppPreference.getInstance(mContext).setString(PrefKey.PHONE_NUMBER, edtPhoneNumber.getText().toString());

        // get Shipping address data

        //Comment: if checked billing and shipping address will be same.

        if (chkShipToAddress.isChecked()) {

            AppPreference.getInstance(mContext).setString(PrefKey.FIRST_NAME_SHIP, edtFirstName.getText().toString());
            AppPreference.getInstance(mContext).setString(PrefKey.LAST_NAME_SHIP, edtLastName.getText().toString());
            AppPreference.getInstance(mContext).setString(PrefKey.ADDRESS_SHIP, edtAddress.getText().toString());
            AppPreference.getInstance(mContext).setString(PrefKey.CITY_SHIP, edtCity.getText().toString());
            AppPreference.getInstance(mContext).setString(PrefKey.STATE_NAME_SHIP, tvStateName.getText().toString());
            AppPreference.getInstance(mContext).setString(PrefKey.POST_CODE_SHIP, edtPostCode.getText().toString());
            AppPreference.getInstance(mContext).setString(PrefKey.COUNTRY_NAME_SHIP, tvCountry.getText().toString());
        } else {
            AppPreference.getInstance(mContext).setString(PrefKey.FIRST_NAME_SHIP, edtFirstNameShip.getText().toString());
            AppPreference.getInstance(mContext).setString(PrefKey.LAST_NAME_SHIP, edtLastNameShip.getText().toString());
            AppPreference.getInstance(mContext).setString(PrefKey.ADDRESS_SHIP, edtAddressShip.getText().toString());
            AppPreference.getInstance(mContext).setString(PrefKey.CITY_SHIP, edtCityShip.getText().toString());
            AppPreference.getInstance(mContext).setString(PrefKey.STATE_NAME_SHIP, tvStateNameShip.getText().toString());
            AppPreference.getInstance(mContext).setString(PrefKey.POST_CODE_SHIP, edtPostCodeShip.getText().toString());
            AppPreference.getInstance(mContext).setString(PrefKey.COUNTRY_NAME_SHIP, tvCountryShip.getText().toString());
        }

    }

    // load ui data from preference
    private void loadData() {

        if (editOnly) {
            btnNext.setText(getResources().getString(R.string.update));
            couponLayout.setVisibility(View.GONE);
            tvTotalPrice.setVisibility(View.INVISIBLE);
        } else {
            btnNext.setText(getResources().getString(R.string.next));
            couponLayout.setVisibility(View.VISIBLE);
            // set payment price
            tvTotalPrice.setText("Total: " + AppConstants.CURRENCY + AppPreference.getInstance(mContext).getString(PrefKey.PAYMENT_TOTAL_PRICE));
        }

        String email = AppPreference.getInstance(mContext).getString(PrefKey.EMAIL);
        String firstName = AppPreference.getInstance(mContext).getString(PrefKey.FIRST_NAME);
        String lastName = AppPreference.getInstance(mContext).getString(PrefKey.LAST_NAME);
        String userName = AppPreference.getInstance(mContext).getString(PrefKey.USER_NAME);

        // billing
        String address = AppPreference.getInstance(mContext).getString(PrefKey.ADDRESS);
        String city = AppPreference.getInstance(mContext).getString(PrefKey.CITY);
        String stateName = AppPreference.getInstance(mContext).getString(PrefKey.STATE_NAME);
        String postCode = AppPreference.getInstance(mContext).getString(PrefKey.POST_CODE);
        String country = AppPreference.getInstance(mContext).getString(PrefKey.COUNTRY_NAME);
        String phoneNumber = AppPreference.getInstance(mContext).getString(PrefKey.PHONE_NUMBER);


        // shipping
        String fistNameShip = AppPreference.getInstance(mContext).getString(PrefKey.FIRST_NAME_SHIP);
        String lastNameShip = AppPreference.getInstance(mContext).getString(PrefKey.LAST_NAME_SHIP);
        String addressShip = AppPreference.getInstance(mContext).getString(PrefKey.ADDRESS_SHIP);
        String cityShip = AppPreference.getInstance(mContext).getString(PrefKey.CITY_SHIP);
        String stateNameShip = AppPreference.getInstance(mContext).getString(PrefKey.STATE_NAME_SHIP);
        String postCodeShip = AppPreference.getInstance(mContext).getString(PrefKey.POST_CODE_SHIP);
        String countryShip = AppPreference.getInstance(mContext).getString(PrefKey.COUNTRY_NAME_SHIP);

        BillingModel billingModel = new BillingModel(firstName, lastName, AppConstants.EMPTY_STRING, address, city, stateName, postCode, country,
                email, phoneNumber);
        ShippingModel shippingModel = new ShippingModel(fistNameShip, lastNameShip, AppConstants.EMPTY_STRING, addressShip, cityShip, stateNameShip,
                postCodeShip, countryShip);

        String customerId = AppPreference.getInstance(mContext).getString(PrefKey.CUSTOMER_ID);

        customer = new Customer(customerId, email, firstName, lastName, userName, billingModel, shippingModel);

        // preserve value into UI
        edtFirstName.setText(firstName);
        edtLastName.setText(lastName);
        edtEmail.setText(email);
        edtUserName.setText(userName);
        edtAddress.setText(address);
        edtCity.setText(city);
        edtPostCode.setText(postCode);
        tvCountry.setText(country);
        tvStateName.setText(stateName);
        edtPhoneNumber.setText(phoneNumber);

        // get Shipping address data
        edtFirstNameShip.setText(fistNameShip);
        edtLastNameShip.setText(lastNameShip);
        edtAddressShip.setText(addressShip);
        edtCityShip.setText(cityShip);
        tvStateNameShip.setText(stateNameShip);
        edtPostCodeShip.setText(postCodeShip);
        tvCountryShip.setText(countryShip);

        // load countries
        countryPicker();
    }

    private boolean isValidateInput() {
        return !edtUserName.getText().toString().isEmpty()
                && !edtPhoneNumber.getText().toString().isEmpty()
                && !edtAddress.getText().toString().isEmpty()
                && !edtFirstName.getText().toString().isEmpty()
                && !edtCity.getText().toString().isEmpty()
                && !edtPostCode.getText().toString().isEmpty()
                && !tvStateName.toString().equals("")
                && !tvStateName.toString().equals("Select State");
    }

    private void countryPicker() {

        final ProgressDialog progressDialog = DialogUtils.showProgressDialog(mActivity, getString(R.string.msg_loading), false);

        RequestCountryPicker countryPicker = new RequestCountryPicker(mContext);
        countryPicker.setResponseListener(data -> {
            if (data != null) {
                if (!countryList.isEmpty()) {
                    countryList.clear();
                }
                countryList.addAll((ArrayList<Zone>) data);
                if (countryList.size() > AppConstants.VALUE_ZERO) {
                    tvCountry.setText(countryList.get(1).name);
                    tvCountryShip.setText(countryList.get(1).name);
                }
            }
            DialogUtils.dismissProgressDialog(progressDialog);
        });
        countryPicker.execute();
    }


    private void statePicker(String countryCode) {

        final ProgressDialog progressDialog = DialogUtils.showProgressDialog(mActivity, getString(R.string.msg_loading), false);

        RequestStatePicker statePicker = new RequestStatePicker(mContext, countryCode);
        statePicker.setResponseListener(data -> {
            if (data != null) {
                if (!stateList.isEmpty()) {
                    stateList.clear();
                }
                stateList.addAll((ArrayList<String>) data);
                if (stateList.size() > AppConstants.VALUE_ZERO) {
                    tvStateName.setText(stateList.get(AppConstants.INDEX_ZERO));
                    tvStateNameShip.setText(stateList.get(AppConstants.INDEX_ZERO));
                }
            }
            DialogUtils.dismissProgressDialog(progressDialog);
        });
        statePicker.execute();
    }


    private void editCustomer(Customer customer) {
        // start loading progress dialog
        final ProgressDialog progressDialog = DialogUtils.showProgressDialog(mActivity, getString(R.string.msg_loading), false);

        RequestEditCustomer editCustomer = new RequestEditCustomer(mContext, customer);
        editCustomer.buildParams();
        editCustomer.setResponseListener(data -> {

            if (data != null) {

                Customer customer1 = (Customer) data;

                if (customer1.customerId != null) {
                    Toast.makeText(mContext, "Successfully updated.", Toast.LENGTH_SHORT).show();
                    AppPreference.getInstance(mContext).setBoolean(PrefKey.REGISTERED, true);

                    // store user info
                    ProfileData.storeProfileData(mContext, customer1.customerId, customer1.firstName, customer1.lastName, customer1.email,
                            customer1.userName, AppConstants.EMPTY_STRING);

                    finish();
                } else {
                    Toast.makeText(mContext, "Fail to update!", Toast.LENGTH_SHORT).show();
                }
            }

            DialogUtils.dismissProgressDialog(progressDialog);

        });
        editCustomer.execute();
    }

    private void storeUserData() {
        // get User info address data
        AppPreference.getInstance(mContext).setString(PrefKey.FIRST_NAME, edtFirstName.getText().toString());
        AppPreference.getInstance(mContext).setString(PrefKey.LAST_NAME, edtLastName.getText().toString());
        AppPreference.getInstance(mContext).setString(PrefKey.EMAIL, edtEmail.getText().toString());
        AppPreference.getInstance(mContext).setString(PrefKey.USER_NAME, edtUserName.getText().toString());
        AppPreference.getInstance(mContext).setString(PrefKey.ADDRESS, edtAddress.getText().toString());
        AppPreference.getInstance(mContext).setString(PrefKey.CITY, edtCity.getText().toString());
        AppPreference.getInstance(mContext).setString(PrefKey.STATE_NAME, tvStateName.getText().toString());
        AppPreference.getInstance(mContext).setString(PrefKey.POST_CODE, edtPostCode.getText().toString());
        AppPreference.getInstance(mContext).setString(PrefKey.COUNTRY_NAME, tvCountry.getText().toString());
        AppPreference.getInstance(mContext).setString(PrefKey.PHONE_NUMBER, edtPhoneNumber.getText().toString());
    }

    private void placeOrder(String transactionID) {

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
            OrderModel orderModel = new OrderModel(customerId, email, "Paytm", "Paytm", billingModel, shippingModel, lineItems, "id");

            final ProgressDialog progressDialog = DialogUtils.showProgressDialog(mActivity, getString(R.string.msg_loading), false);


            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("NewOrder");
            Date currentTime = Calendar.getInstance().getTime();
            mDatabase.child(currentTime.toString()).setValue(orderModel);

            RequestOrder requestOrder = new RequestOrder(mActivity, orderModel);
            requestOrder.buildParams();
            requestOrder.setResponseListener(data -> {
                if (data != null) {

                    OrderItem order = (OrderItem) data;

                    if (order.orderId != null) {
                        oid = order.orderId;
                        cid = customerId;
                        //Paytm Payment

//                        Intent i = new Intent(MyAddressActivity.this, checksum.class);
//                        i.putExtra("orderid", oid);
//                        i.putExtra("custid", cid);
//                        i.putParcelableArrayListExtra(AppConstants.KEY_LINE_ITEM_LIST, lineItems);
//                        startActivity(i);
                        if(price.equals("0")) {
                            ActivityUtils.getInstance().invokeOrder(mActivity, oid);
                        }
                        else {
                            sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
                            dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }



//                        getCheckSum cs = new getCheckSum();
//                        cs.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

//                        postOderNote(order, lineItems);
//
//                        // omit ordered cart item from cart db
//                        try {
//                            CartDBController cartController = new CartDBController(mContext);
//                            cartController.open();
//                            for (int i = 0; i < order.lineItems.size(); i++) {
//                                cartController.deleteCartItemById(order.lineItems.get(i).productId);
//                            }
//                            cartController.close();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        ActivityUtils.getInstance().invokeOrder(mActivity, order.orderId);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showZoneListDialog(Activity activity, String title, ArrayList<String> list, final ListDialogActionListener listDialogActionListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        if (title != null) {
            alertDialogBuilder.setTitle(title);
        }

        String[] countryArr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            countryArr[i] = list.get(i);
        }

        alertDialogBuilder.setItems(countryArr, (dialog, which) -> {
            if (listDialogActionListener != null) {
                listDialogActionListener.onItemSelected(which);
            }
            dialog.dismiss();
        });
        alertDialogBuilder.show();
    }

    @Override
    public void onTransactionResponse(Bundle inResponse) {
        Log.d("Paytm resppomse", "onTransactionResponse: " + inResponse.getString("STATUS"));
        if (Objects.requireNonNull(inResponse.getString("STATUS")).equals("TXN_FAILURE")) {
            Toast.makeText(mContext, "Payment Failed!", Toast.LENGTH_SHORT).show();
        } else if (Objects.requireNonNull(inResponse.getString("STATUS")).equals("TXN_SUCCESS")) {
            ActivityUtils.getInstance().invokeOrder(mActivity, oid);
        }
    }

    @Override
    public void networkNotAvailable() {

    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {

    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {

    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {

    }

    @Override
    public void onBackPressedCancelTransaction() {

    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {

    }

    @SuppressLint("StaticFieldLeak")
    public class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(MyAddressActivity.this);
        String url = "https://melody.dhakkan.in/paytm/generateChecksum.php";
        String varifyurl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + oid;
        String CHECKSUMHASH = "";

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }

        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(MyAddressActivity.this);
            String param =
                    "MID=" + mid +
                            "&ORDER_ID=" + oid +
                            "&CUST_ID=" + cid +
                            "&CHANNEL_ID=WAP&TXN_AMOUNT=" + price + "&WEBSITE=DEFAULT" +
                            "&CALLBACK_URL=" + varifyurl + "&INDUSTRY_TYPE_ID=Retail";
            JSONObject jsonObject = jsonParser.makeHttpRequest(url, "POST", param);
            if (jsonObject != null) {
                Log.e("CheckSum result >>", jsonObject.toString());
                try {
                    CHECKSUMHASH = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";
                    Log.e("CheckSum result >>", CHECKSUMHASH);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ", "  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
//            PaytmPGService Service = PaytmPGService.getStagingService();
            // when app is ready to publish use production service
            PaytmPGService Service = PaytmPGService.getProductionService();
            // now call paytm service here
            //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
            HashMap<String, String> paramMap = new HashMap<>();
            //these are mandatory parameters
            paramMap.put("MID", mid); //MID provided by paytm
            paramMap.put("ORDER_ID", oid);
            paramMap.put("CUST_ID", cid);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", price);
            paramMap.put("WEBSITE", "DEFAULT");
            paramMap.put("CALLBACK_URL", varifyurl);
            //paramMap.put( "EMAIL" , "abc@gmail.com");   // no need
            // paramMap.put( "MOBILE_NO" , "9144040888");  // no need
            paramMap.put("CHECKSUMHASH", CHECKSUMHASH);
            //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param " + paramMap.toString());
            Service.initialize(Order, null);
            // start payment service call here
            Service.startPaymentTransaction(MyAddressActivity.this, true, true,
                    MyAddressActivity.this);
        }
    }

}