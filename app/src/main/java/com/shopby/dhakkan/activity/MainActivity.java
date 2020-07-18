package com.shopby.dhakkan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shopby.dhakkan.R;
import com.shopby.dhakkan.adapter.CategoryListAdapter;
import com.shopby.dhakkan.adapter.HomeProdListAdapter;
import com.shopby.dhakkan.adapter.ImageSliderAdapter;
import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.data.sqlite.CartDBController;
import com.shopby.dhakkan.data.sqlite.NotificationDBController;
import com.shopby.dhakkan.model.CartItem;
import com.shopby.dhakkan.model.Category;
import com.shopby.dhakkan.model.NotificationModel;
import com.shopby.dhakkan.model.ProductDetail;
import com.shopby.dhakkan.network.helper.RequestCategory;
import com.shopby.dhakkan.network.helper.RequestProducts;
import com.shopby.dhakkan.network.params.HttpParams;
import com.shopby.dhakkan.utils.ActivityUtils;
import com.shopby.dhakkan.utils.AnalyticsUtils;
import com.shopby.dhakkan.utils.AppUtility;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.branch.referral.Branch;
import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends BaseActivity {

    // variables
    private Context mContext;
    private Activity mActivity;
    private ArrayList<Category> categoryList;
    private ArrayList<ProductDetail> featuredProdList;
    private ArrayList<ProductDetail> recentProdList;
    private ArrayList<ProductDetail> popularProdList;
    private ArrayList<ProductDetail> sampleCategoryProducts;
    private CategoryListAdapter categoryListAdapter;
    private HomeProdListAdapter featuredProdListAdapter, recentProdListAdapter, popularProdListAdapter, sampleCategoryAdapter;
    private static final int COLUMN_SPAN_COUNT = 2;

    // ui declaration
    private RecyclerView rvCategory;
    private RelativeLayout categoryParent, featureParent, recentParent, popularParent, sampleCatParent;
    private ProgressBar featureProgress, recentProgress, popularProgress, sampleProgress;
    private TextView tvFeaturedListTitle, tvFeaturedListAll, tvRecentListTitle, tvRecentListAll, tvPopularListTitle, tvPopularListAll,
            tvSampleCategoryTitle, tvSampleCategoryAll, tvCartCounter, tvNotificationCounter;
    private ImageView imgToolbarCart, imgNotification, ivSearchIcon;
    private EditText edtSearchProduct;


    // view pager image slider
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        initListener();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartCounter();
        loadNotificationCounter();
    }

    private void initVariable() {
        mContext = getApplicationContext();
        mActivity = MainActivity.this;
        categoryList = new ArrayList<>();
        featuredProdList = new ArrayList<>();
        recentProdList = new ArrayList<>();
        popularProdList = new ArrayList<>();
        sampleCategoryProducts = new ArrayList<>();

        // analytics event trigger
        AnalyticsUtils.getAnalyticsUtils(mContext).trackEvent("Main Activity");
    }

    private void initView() {

        // set parent view
        setContentView(R.layout.activity_main);

        // initiate drawer and toolbar
        initToolbar();
        initDrawer();
        initLoader();

        // cart counter
        imgToolbarCart = findViewById(R.id.imgToolbarCart);
        imgNotification = findViewById(R.id.imgNotification);
        ivSearchIcon = findViewById(R.id.ivSearchIcon);
        tvCartCounter = findViewById(R.id.tvCartCounter);
        tvNotificationCounter = findViewById(R.id.tvNotificationCounter);
        edtSearchProduct = findViewById(R.id.edtSearchProduct);

        // category list ui
        RelativeLayout lytCategoryList = findViewById(R.id.lytCategoryList);

        rvCategory = lytCategoryList.findViewById(R.id.homeRecyclerView);
        categoryParent = lytCategoryList.findViewById(R.id.parentPanel);
        lytCategoryList.findViewById(R.id.lytListHeader).setVisibility(View.GONE);
        lytCategoryList.findViewById(R.id.sectionProgress).setVisibility(View.GONE);


        // featured list ui
        RelativeLayout lytFeaturedList = findViewById(R.id.lytFeaturedList);
        RecyclerView rvFeaturedList = lytFeaturedList.findViewById(R.id.homeRecyclerView);
        tvFeaturedListTitle = lytFeaturedList.findViewById(R.id.tvListTitle);
        tvFeaturedListAll = lytFeaturedList.findViewById(R.id.tvSeeAll);
        featureParent = lytFeaturedList.findViewById(R.id.parentPanel);
        featureProgress = lytFeaturedList.findViewById(R.id.sectionProgress);
        //rvFeaturedList.addItemDecoration(new GridSpacingItemDecoration(2, (int) getResources().getDimension(R.dimen.pad_margin_2dp), false));

        // recent list ui
        RelativeLayout lytRecentList = findViewById(R.id.lytRecentList);
        RecyclerView rvRecentList = lytRecentList.findViewById(R.id.homeRecyclerView);
        tvRecentListTitle = lytRecentList.findViewById(R.id.tvListTitle);
        tvRecentListAll = lytRecentList.findViewById(R.id.tvSeeAll);
        recentParent = lytRecentList.findViewById(R.id.parentPanel);
        recentProgress = lytRecentList.findViewById(R.id.sectionProgress);

        // popular list ui
        RelativeLayout lytPopularList = findViewById(R.id.lytPopularList);
        RecyclerView rvPopularList = lytPopularList.findViewById(R.id.homeRecyclerView);
        tvPopularListTitle = lytPopularList.findViewById(R.id.tvListTitle);
        tvPopularListAll = lytPopularList.findViewById(R.id.tvSeeAll);
        popularParent = lytPopularList.findViewById(R.id.parentPanel);
        popularProgress = lytPopularList.findViewById(R.id.sectionProgress);

        // a sample category (you can choose your own category by replacing this)
        RelativeLayout lytSampleCategory = findViewById(R.id.lytSampleCategory);
        RecyclerView rvSampleCategory = lytSampleCategory.findViewById(R.id.homeRecyclerView);
        tvSampleCategoryTitle = lytSampleCategory.findViewById(R.id.tvListTitle);
        tvSampleCategoryAll = lytSampleCategory.findViewById(R.id.tvSeeAll);
        sampleCatParent = lytSampleCategory.findViewById(R.id.parentPanel);
        sampleProgress = lytSampleCategory.findViewById(R.id.sectionProgress);

        // init category RecyclerView
        rvCategory.setHasFixedSize(true);
        rvCategory.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        categoryListAdapter = new CategoryListAdapter(mContext, categoryList);
        rvCategory.setAdapter(categoryListAdapter);

        // init featured product RecyclerView
        rvFeaturedList.setLayoutManager(new GridLayoutManager(mActivity, COLUMN_SPAN_COUNT));
        featuredProdListAdapter = new HomeProdListAdapter(mContext, featuredProdList);
        rvFeaturedList.setAdapter(featuredProdListAdapter);

        // init recent product RecyclerView
        rvRecentList.setLayoutManager(new GridLayoutManager(mActivity, COLUMN_SPAN_COUNT));
        recentProdListAdapter = new HomeProdListAdapter(mContext, recentProdList);
        rvRecentList.setAdapter(recentProdListAdapter);

        // init popular product RecyclerView
        rvPopularList.setLayoutManager(new GridLayoutManager(mActivity, COLUMN_SPAN_COUNT));
        popularProdListAdapter = new HomeProdListAdapter(mContext, popularProdList);
        rvPopularList.setAdapter(popularProdListAdapter);

        // init tech product RecyclerView
        rvSampleCategory.setLayoutManager(new GridLayoutManager(mActivity, COLUMN_SPAN_COUNT));
        sampleCategoryAdapter = new HomeProdListAdapter(mContext, sampleCategoryProducts);
        rvSampleCategory.setAdapter(sampleCategoryAdapter);

        AppUtility.noInternetWarning(findViewById(R.id.parentPanel), mContext);
        if (!AppUtility.isNetworkAvailable(mContext)) {
            showEmptyView();
        }
    }

    private void loadData() {

        // Load category list
        RequestCategory requestCategory = new RequestCategory(this);
        requestCategory.setResponseListener(data -> {
            if (data != null) {
                if (!categoryList.isEmpty()) {
                    categoryList.clear();
                }

                categoryList.addAll((ArrayList<Category>) data);

                if (!categoryList.isEmpty()) {
                    // load slider images

                    categoryParent.setVisibility(View.VISIBLE);
                    categoryListAdapter.notifyDataSetChanged();

                    loadCategorySlider();
                    hideLoader();
                }
            }
        });
        requestCategory.execute();

        // Load featured products
        RequestProducts featuredProducts = new RequestProducts(mActivity, AppConstants.INITIAL_PAGE_NUMBER, AppConstants.MAX_PER_PAGE, HttpParams.KEY_FEATURED, AppConstants.TYPE_FEATURED);
        featuredProducts.setResponseListener(data -> {
            if (data != null) {
                featuredProdList.addAll((ArrayList<ProductDetail>) data);

                if (!featuredProdList.isEmpty()) {
                    featureParent.setVisibility(View.VISIBLE);
                    tvFeaturedListTitle.setText("FEATURED ITEMS (" + featuredProdList.size() + ")");   // set list title
                    featuredProdListAdapter.setDisplayCount(AppConstants.HOME_ITEM_MAX);                                    // set display item limit
                    featuredProdListAdapter.notifyDataSetChanged();
                }
            }
            featureProgress.setVisibility(View.GONE);
            hideLoader();
        });
        featuredProducts.execute();

        // Load recent products
        RequestProducts recentProducts = new RequestProducts(mActivity, AppConstants.INITIAL_PAGE_NUMBER, AppConstants.MAX_PER_PAGE, HttpParams.KEY_RECENT + AppUtility.getRecentProductDateTime(AppConstants.NUMBER_OF_DAYS_BEFORE), AppConstants.TYPE_RECENT);
        recentProducts.setResponseListener(data -> {
            if (data != null) {
                recentProdList.addAll((ArrayList<ProductDetail>) data);

                if (!recentProdList.isEmpty()) {
                    recentParent.setVisibility(View.VISIBLE);
                    tvRecentListTitle.setText("RECENT ITEMS (" + recentProdList.size() + ")");
                    recentProdListAdapter.setDisplayCount(AppConstants.HOME_ITEM_MAX);
                    recentProdListAdapter.notifyDataSetChanged();
                }
            }
            recentProgress.setVisibility(View.GONE);
            hideLoader();
        });
        recentProducts.execute();

        // Load popular product
        RequestProducts popularProducts = new RequestProducts(mContext, AppConstants.TYPE_POPULAR);
        popularProducts.setResponseListener(data -> {
            if (data != null) {
                popularProdList.addAll((ArrayList<ProductDetail>) data);

                if (!popularProdList.isEmpty()) {
                    popularParent.setVisibility(View.VISIBLE);
                    tvPopularListTitle.setText("POPULAR ITEMS (" + popularProdList.size() + ")");
                    popularProdListAdapter.setDisplayCount(AppConstants.HOME_ITEM_MAX);
                    popularProdListAdapter.notifyDataSetChanged();
                }
            }
            popularProgress.setVisibility(View.GONE);
            hideLoader();
        });
        popularProducts.execute();

        // Load tech categories products as sample category
        RequestProducts requestCatProduct = new RequestProducts(mActivity, AppConstants.INITIAL_PAGE_NUMBER, AppConstants.MAX_PER_PAGE, AppConstants.CATEGORY_TEC_PRODUCTS_ID, AppConstants.TYPE_CATEGORY);
        requestCatProduct.setResponseListener(data -> {
            if (data != null) {
                sampleCategoryProducts.addAll((ArrayList<ProductDetail>) data);

                if (!sampleCategoryProducts.isEmpty()) {
                    sampleCatParent.setVisibility(View.VISIBLE);
                    tvSampleCategoryTitle.setText("SPECIAL GIFTS (" + sampleCategoryProducts.size() + ")");
                    sampleCategoryAdapter.setDisplayCount(AppConstants.HOME_ITEM_MAX);
                    sampleCategoryAdapter.notifyDataSetChanged();
                }
            }
            sampleProgress.setVisibility(View.GONE);
            hideLoader();
        });
        requestCatProduct.execute();

        // set cart counter
        loadCartCounter();
    }

    private void initListener() {

        categoryListAdapter.setItemClickListener((view, position) -> ActivityUtils.getInstance().invokeProducts(mActivity, categoryList.get(position).name, AppConstants.TYPE_CATEGORY, categoryList.get(position).id));

        featuredProdListAdapter.setItemClickListener((view, position) -> {
            int productId = featuredProdList.get(position).id;
            if (productId > 0) {
                Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                intent.putExtra(AppConstants.PRODUCT_ID, productId);
                startActivity(intent);
            }
        });

        recentProdListAdapter.setItemClickListener((view, position) -> {
            int productId = recentProdList.get(position).id;
            if (productId > 0) {
                Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                intent.putExtra(AppConstants.PRODUCT_ID, productId);
                startActivity(intent);
            }
        });

        popularProdListAdapter.setItemClickListener((view, position) -> {
            int productId = popularProdList.get(position).id;
            if (productId > 0) {
                Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                intent.putExtra(AppConstants.PRODUCT_ID, productId);
                startActivity(intent);
            }
        });
        sampleCategoryAdapter.setItemClickListener((view, position) -> {
            int productId = sampleCategoryProducts.get(position).id;
            if (productId > 0) {
                Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                intent.putExtra(AppConstants.PRODUCT_ID, productId);
                startActivity(intent);
            }
        });
        // toolbar cart action listener
        imgToolbarCart.setOnClickListener(v -> {

            showAdThenActivity(CartListActivity.class);

            /**
             * if you don't want to show notification then disable
             * disable previous line and use line given bellow
             */
            //ActivityUtils.getInstance().invokeActivity(mActivity, CartListActivity.class, false);

        });
        // toolbar notification action listener
        imgNotification.setOnClickListener(v -> {
            showAdThenActivity(NotificationActivity.class);

            /**
             * if you don't want to show notification then disable
             * disable previous line and use line given bellow
             */
            //ActivityUtils.getInstance().invokeActivity(mActivity, NotificationActivity.class, false);

        });

        // search icon at home action listener
        ivSearchIcon.setOnClickListener(v -> {
            if (edtSearchProduct.getText().toString().isEmpty()) {
                AppUtility.showToast(mContext, getString(R.string.type_something));
            } else {
                ActivityUtils.getInstance().invokeSearchActivity(mActivity, edtSearchProduct.getText().toString());
            }
        });


        edtSearchProduct.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                if (edtSearchProduct.getText().toString().isEmpty()) {
                    AppUtility.showToast(mContext, getString(R.string.type_something));
                } else {
                    ActivityUtils.getInstance().invokeSearchActivity(mActivity, edtSearchProduct.getText().toString());
                }
                return true;
            }
            return false;
        });

        // See all listener
        tvFeaturedListAll.setOnClickListener(v -> ActivityUtils.getInstance().invokeProducts(mActivity, getString(R.string.featured_items), AppConstants.TYPE_FEATURED, AppConstants.NO_CATEGORY));

        tvRecentListAll.setOnClickListener(v -> ActivityUtils.getInstance().invokeProducts(mActivity, getString(R.string.recent_items), AppConstants.TYPE_RECENT, AppConstants.NO_CATEGORY));

        tvPopularListAll.setOnClickListener(v -> ActivityUtils.getInstance().invokeProducts(mActivity, getString(R.string.popular_items), AppConstants.TYPE_POPULAR, AppConstants.NO_CATEGORY));

        tvSampleCategoryAll.setOnClickListener(v -> ActivityUtils.getInstance().invokeProducts(mActivity, getString(R.string.tech_discovery), AppConstants.TYPE_CATEGORY, AppConstants.CATEGORY_TEC_PRODUCTS_ID));
    }

    @Override
    public void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();

        // Branch init
        branch.initSession((referringParams, error) -> {
            if (error == null) {
                // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                // params will be empty if no data found
                // ... insert custom logic here ...
                Log.i("BRANCH SDK", referringParams.toString());
            } else {
                Log.i("BRANCH SDK", error.getMessage());
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }

    private void loadCategorySlider() {
        final ArrayList<String> imageList = new ArrayList<>();
        for (int i = 0; i < categoryList.size(); i++) {
            imageList.add(categoryList.get(i).image);
        }
        ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(mContext, imageList);
        mPager = findViewById(R.id.vpImageSlider);
        mPager.setAdapter(imageSliderAdapter);
        CircleIndicator indicator = findViewById(R.id.sliderIndicator);
        indicator.setViewPager(mPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = () -> {
            int setPosition = mPager.getCurrentItem() + 1;
            if (setPosition == imageList.size()) {
                setPosition = AppConstants.VALUE_ZERO;
            }
            mPager.setCurrentItem(setPosition, true);
            try {
                rvCategory.smoothScrollToPosition(setPosition);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        //  Auto animated timer
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);


        // view page on item click listener
        imageSliderAdapter.setItemClickListener((view, position) -> ActivityUtils.getInstance().invokeProducts(mActivity, categoryList.get(position).name, AppConstants.TYPE_CATEGORY, categoryList.get(position).id));

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setCategoryItemSelection(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void loadCartCounter() {
        try {
            CartDBController cartController = new CartDBController(mContext);
            cartController.open();
            ArrayList<CartItem> cartList = cartController.getAllCartData();
            cartController.close();

            if (cartList.isEmpty()) {
                tvCartCounter.setVisibility(View.GONE);
            } else {
                tvCartCounter.setVisibility(View.VISIBLE);
                tvCartCounter.setText(String.valueOf(cartList.size()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadNotificationCounter() {
        try {
            NotificationDBController notifyController = new NotificationDBController(mContext);
            notifyController.open();
            ArrayList<NotificationModel> notifyList = notifyController.getUnreadNotification();
            notifyController.close();

            if (notifyList.isEmpty()) {
                tvNotificationCounter.setVisibility(View.GONE);
            } else {
                tvNotificationCounter.setVisibility(View.VISIBLE);
                tvNotificationCounter.setText(String.valueOf(notifyList.size()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            tvNotificationCounter.setVisibility(View.GONE);
        }
    }

    private void setCategoryItemSelection(int selectedIndex) {
        rvCategory.smoothScrollToPosition(selectedIndex);
        for (Category pageModel : categoryList) {
            pageModel.isSelected = false;
        }
        categoryList.get(selectedIndex).isSelected = true;
        categoryListAdapter.notifyDataSetChanged();
    }
}
