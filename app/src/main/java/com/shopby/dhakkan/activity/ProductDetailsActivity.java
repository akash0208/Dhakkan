package com.shopby.dhakkan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shopby.dhakkan.R;
import com.shopby.dhakkan.adapter.AttributeAdapter;
import com.shopby.dhakkan.adapter.ProductSliderAdapter;
import com.shopby.dhakkan.adapter.ReviewListAdapter;
import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.data.preference.AppPreference;
import com.shopby.dhakkan.data.preference.PrefKey;
import com.shopby.dhakkan.data.sqlite.CartDBController;
import com.shopby.dhakkan.data.sqlite.WishDBController;
import com.shopby.dhakkan.model.LineItem;
import com.shopby.dhakkan.model.ProductAttribute;
import com.shopby.dhakkan.model.ProductDetail;
import com.shopby.dhakkan.model.ProductReview;
import com.shopby.dhakkan.network.helper.RequestProductReviews;
import com.shopby.dhakkan.network.helper.RequestProductDetails;
import com.shopby.dhakkan.utils.ActivityUtils;
import com.shopby.dhakkan.utils.AppUtility;
import com.shopby.dhakkan.utils.RVEmptyObserver;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Nasir on 5/17/17.
 */

public class ProductDetailsActivity extends BaseActivity {

    // init variables
    private Context mContext;
    private Activity mActivity;

    private int quantityCounter = 1;

    // recycler view
    private RecyclerView rvReviews, rvAttributeSize;
    private ArrayList<ProductAttribute> attributeList;
    private ArrayList<ProductReview> reviewList;
    private ReviewListAdapter reviewListAdapter;
    private AttributeAdapter attributeAdapter;
    private LinearLayoutManager reviewLytManager, sizeLytManager;

    // init view
    private TextView tvProductName, tvProductQuantity, tvOrderCount,
            tvAverageRating, tvRatingCount, priceSeparator,
            emptyView, tvRegularPrice, tvSalesPrice, tvShortDescription;
    private ProductDetail product = null;
    private Button btnAddToCart, btnBuyNow;
    private ImageButton btnQuantityPlus, btnQuantityMinus, cartList, addWishList, searchButton;
    private RatingBar ratingBar;
    private WebView wvDescription;

    private ViewPager mPager;
    private ProductSliderAdapter productSliderAdapter;

    private boolean addedToWishList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        initToolbar();
        loadData();
        initListener();
    }

    private void initVariable() {
        mContext = getApplicationContext();
        mActivity = ProductDetailsActivity.this;

        attributeList = new ArrayList<>();
        reviewList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_product_details);

        initToolbar();
        enableBackButton();
        initLoader();

        tvProductName = findViewById(R.id.tvProductName);
        tvShortDescription = findViewById(R.id.tvShortDescription);
        tvProductQuantity = findViewById(R.id.tvProductQuantity);
        tvOrderCount = findViewById(R.id.tvOrderCount);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        tvRatingCount = findViewById(R.id.tvRatingCount);
        tvRegularPrice = findViewById(R.id.tvRegularPrice);
        tvSalesPrice = findViewById(R.id.tvSalesPrice);
        priceSeparator = findViewById(R.id.priceSeparator);
        wvDescription = findViewById(R.id.wvDescription);
        mPager = findViewById(R.id.vpImageSlider);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        ratingBar = findViewById(R.id.ratingBar);
        cartList = findViewById(R.id.cartList);
        addWishList = findViewById(R.id.addWishList);
        searchButton = findViewById(R.id.searchButton);
        btnQuantityPlus = findViewById(R.id.btnQuantityPlus);
        btnQuantityMinus = findViewById(R.id.btnQuantityMinus);

        // products attribute
        rvAttributeSize = findViewById(R.id.rvProductAttribute);
        rvAttributeSize.setHasFixedSize(true);
        rvAttributeSize.setNestedScrollingEnabled(false);
        sizeLytManager = new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false);
        rvAttributeSize.setLayoutManager(sizeLytManager);
        attributeAdapter = new AttributeAdapter(mContext, attributeList);
        rvAttributeSize.setAdapter(attributeAdapter);

        // init review list
        rvReviews = findViewById(R.id.rvReviews);
        reviewLytManager = new LinearLayoutManager(mActivity);
        rvReviews.setLayoutManager(reviewLytManager);
        reviewListAdapter = new ReviewListAdapter(reviewList);
        emptyView = findViewById(R.id.emptyView);
        reviewListAdapter.registerAdapterDataObserver(new RVEmptyObserver(rvReviews, emptyView));
        rvReviews.setAdapter(reviewListAdapter);

        wvDescription.getSettings();
        wvDescription.getSettings().setTextZoom(85);
        wvDescription.setBackgroundColor(Color.TRANSPARENT);
        wvDescription.setVerticalScrollBarEnabled(false);
    }


    private void loadData() {
        Intent intent = getIntent();
        int productId = intent.getIntExtra(AppConstants.PRODUCT_ID, AppConstants.VALUE_ZERO);

        if (productId > 0) {
            // Load product details

            updateWishButton(productId);
            updateAddToCartButton(productId);

            RequestProductDetails requestProduct = new RequestProductDetails(mActivity, productId);
            requestProduct.setResponseListener(data -> {
                if (data != null) {

                    product = (ProductDetail) data;

                    // set image
                    loadProductPhoto(product.imageList);
                    // set other properties
                    tvProductName.setText(product.name);
                    tvShortDescription.setText(Html.fromHtml(Html.fromHtml(product.shortDescription).toString()));//Html.fromHtml(product.shortDescription));
                    tvOrderCount.setText(product.totalSale + " orders");
                    if (product.onSaleStatus) {
                        tvRegularPrice.setText(AppConstants.CURRENCY + product.regularPrice);
                        tvSalesPrice.setText(AppConstants.CURRENCY + product.sellPrice);
                        tvRegularPrice.setPaintFlags(tvRegularPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        priceSeparator.setVisibility(View.INVISIBLE);
                        tvRegularPrice.setVisibility(View.INVISIBLE);
                        tvSalesPrice.setText(AppConstants.CURRENCY + product.regularPrice);
                    }

                    wvDescription.loadData(product.description, "text/html; charset=utf-8", "UTF-8");
                    tvAverageRating.setText(String.valueOf(product.averageRating));
                    tvRatingCount.setText(getResources().getString(R.string.total_review) + product.totalRating + ")");
                    ratingBar.setRating(product.averageRating);

                    // load product attributes
                    if (product.attributes.size() > 0) {
                        rvAttributeSize.setVisibility(View.VISIBLE);
                        attributeList.addAll(product.attributes);
                        attributeAdapter.notifyDataSetChanged();
                    } else {
                        rvAttributeSize.setVisibility(View.GONE);
                    }
                }
                hideLoader();

            });
            requestProduct.execute();

            // Load product reviews data
            RequestProductReviews productReviews = new RequestProductReviews(mActivity, productId);
            productReviews.setResponseListener(data -> {
                if (data != null) {
                    reviewList.addAll((ArrayList<ProductReview>) data);
                    if (reviewList.size() > 0) {
                        reviewListAdapter.notifyDataSetChanged();
                    }
                }
            });
            productReviews.execute();
        }
    }


    private void initListener() {

        btnBuyNow.setOnClickListener(v -> {

            if (validateOrder()) {

                quantityCounter = Integer.valueOf(tvProductQuantity.getText().toString());
                float price = 0;
                if (product.onSaleStatus) {
                    price = product.sellPrice * quantityCounter;
                } else {
                    price = product.regularPrice * quantityCounter;
                }

                AppPreference.getInstance(mContext).setString(PrefKey.PAYMENT_TOTAL_PRICE, String.valueOf(price));

                // build lineItemList
                ArrayList<LineItem> lineItemList = new ArrayList<>();
                lineItemList.add(new LineItem(product.name, attributeAdapter.getSelectedAttributes(), product.id, quantityCounter));

                boolean isRegistered = AppPreference.getInstance(mContext).getBoolean(PrefKey.REGISTERED);
                if (isRegistered) {
                    ActivityUtils.getInstance().invokeAddressActivity(mActivity, lineItemList, false, true);
                } else {
                    ActivityUtils.getInstance().invokeLoginAndOrder(mActivity, lineItemList);
                }
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            // Add to cart list
            if (validateOrder()) {
                try {
                    CartDBController cartController = new CartDBController(mContext);
                    cartController.open();

                    if (cartController.isAlreadyAddedToCart(product.id)) {
                        AppUtility.showToast(mContext, getString(R.string.already_in_cart));
                    } else {
                        quantityCounter = Integer.valueOf(tvProductQuantity.getText().toString());

                        float price;
                        if (product.onSaleStatus) {
                            price = product.sellPrice;
                        } else {
                            price = product.regularPrice;
                        }

                        cartController.insertCartItem(product.id, product.name, product.imageList.get(AppConstants.INDEX_ZERO),
                                price, quantityCounter, attributeAdapter.getSelectedAttributes());
                        btnAddToCart.setText(getString(R.string.added_to_cart));
                        AppUtility.showToast(mContext, getString(R.string.added_to_cart));
                    }
                    cartController.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnQuantityPlus.setOnClickListener(v -> {
            quantityCounter++;
            tvProductQuantity.setText(String.valueOf(quantityCounter));
        });

        btnQuantityMinus.setOnClickListener(v -> {
            if (quantityCounter > 1) {
                quantityCounter--;
                tvProductQuantity.setText(String.valueOf(quantityCounter));
            }
        });

        cartList.setOnClickListener(view -> ActivityUtils.getInstance().invokeActivity(mActivity, CartListActivity.class, false));

        addWishList.setOnClickListener(view -> toggleWishList());

        searchButton.setOnClickListener(view -> ActivityUtils.getInstance().invokeSearchActivity(mActivity, AppConstants.EMPTY_STRING));

    }

    private void loadProductPhoto(final ArrayList<String> images) {

        if (images != null && !images.isEmpty()) {
            productSliderAdapter = new ProductSliderAdapter(mContext, images);
            mPager = findViewById(R.id.vpImageSlider);
            mPager.setAdapter(productSliderAdapter);
            CircleIndicator indicator = findViewById(R.id.sliderIndicator);
            indicator.setViewPager(mPager);

            productSliderAdapter.setItemClickListener((view, position) -> ActivityUtils.getInstance().invokeImage(mActivity, images.get(position)));
        }
    }

    private void updateWishButton(int prodId) {
        try {
            WishDBController wishController = new WishDBController(mContext);
            wishController.open();
            addedToWishList = wishController.isAlreadyWished(prodId);
            wishController.close();

            if (addedToWishList) {
                addWishList.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_wish));
            } else {
                addWishList.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_un_wish));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAddToCartButton(int productId) {
        try {
            CartDBController cartController = new CartDBController(mContext);
            cartController.open();

            //quantityCounter = cartController.getItemQuantity(productId);
            if (cartController.isAlreadyAddedToCart(productId)) {
                btnAddToCart.setText("Added to cart");
            }
            //tvProductQuantity.setText(String.valueOf(quantityCounter));

            cartController.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
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

    private boolean validateOrder() {
        if (attributeList.isEmpty()) {
            return true;
        } else if (!attributeAdapter.getSelectedAttributes().isEmpty()) {
            return true;
        } else {
            Toast.makeText(mContext, getString(R.string.select_attribute), Toast.LENGTH_LONG).show();
            return false;
        }

    }

    private void toggleWishList() {
        if (!addedToWishList) {
            try {
                WishDBController wishController = new WishDBController(mContext);
                wishController.open();
                wishController.insertWishItem(product.id, product.name, product.imageList.get(AppConstants.INDEX_ZERO), product.sellPrice,
                        product.averageRating, product.totalSale);
                wishController.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                WishDBController wishController = new WishDBController(mContext);
                wishController.open();
                wishController.deleteWishItemById(product.id);
                wishController.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        updateWishButton(product.id);
    }

}
