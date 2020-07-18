package com.shopby.dhakkan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.view.View;
import android.widget.ImageView;

import com.shopby.dhakkan.R;
import com.shopby.dhakkan.adapter.ProductListAdapter;
import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.listener.OnItemClickListener;
import com.shopby.dhakkan.model.ProductDetail;
import com.shopby.dhakkan.network.helper.RequestProducts;
import com.shopby.dhakkan.network.http.ResponseListener;
import com.shopby.dhakkan.network.params.HttpParams;
import com.shopby.dhakkan.utils.ActivityUtils;
import com.shopby.dhakkan.utils.AppUtility;
import com.shopby.dhakkan.utils.ListTypeShow;

import java.util.ArrayList;

/**
 * Created by Nasir on 5/15/17.
 */

public class ProductListActivity extends BaseActivity {

    // initialize variables
    private Activity mActivity;
    private Context mContext;

    private RecyclerView rvProductList;
    private ArrayList<ProductDetail> productList;
    private ProductListAdapter mProductListAdapter;

    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView.LayoutManager mLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private LinearLayoutManager linearLayoutManager;
    private static final int COLUMN_SPAN_COUNT = 2;
    private ProgressBar loadMoreView;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    // initialize View
    private ImageView viewToggle;

    private String title;
    private int type, categoryId;

    private int pageNumber = AppConstants.INITIAL_PAGE_NUMBER;
    private boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        loadProductList();
        initListener();
    }

    private void initVariable() {
        mActivity = ProductListActivity.this;
        mContext = mActivity.getApplicationContext();

        productList = new ArrayList<>();

        Intent intent = getIntent();
        title = intent.getStringExtra(AppConstants.PAGE_TITLE);
        type = intent.getIntExtra(AppConstants.PAGE_TYPE, AppConstants.VALUE_ZERO);
        categoryId = intent.getIntExtra(AppConstants.CATEGORY_ID, AppConstants.VALUE_ZERO);

    }

    private void initView() {
        setContentView(R.layout.activity_product_list);

        initToolbar();
        enableBackButton();
        setToolbarTitle(title);
        initLoader();

        rvProductList = (RecyclerView) findViewById(R.id.rvProductList);
        viewToggle = (ImageView) findViewById(R.id.viewToggle);
        loadMoreView = (ProgressBar) findViewById(R.id.loadMore);

        // init RecyclerView
        rvProductList.setHasFixedSize(true);
        setRecyclerViewLayoutManager(rvProductList, LayoutManagerType.LINEAR_LAYOUT_MANAGER);
        mProductListAdapter = new ProductListAdapter(mContext, productList, ListTypeShow.LINEAR);
        rvProductList.setAdapter(mProductListAdapter);

    }


    private void loadProductList() {
        loadProductByCategory(AppConstants.INITIAL_PAGE_NUMBER);
    }

    private void initListener() {
        mProductListAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemListener(View view, int position) {
                // invoke product details activity by product id
                ActivityUtils.getInstance().invokeProductDetails(mActivity, productList.get(position).id);
            }
        });


        viewToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleView();
            }
        });


        rvProductList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    onScrolledMore();
                }
            }
        });
    }

    // Generate list and grid layout manager
    public void setRecyclerViewLayoutManager(RecyclerView mRecyclerView, LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                gridLayoutManager = new GridLayoutManager(mActivity, COLUMN_SPAN_COUNT);
                mLayoutManager = gridLayoutManager;
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                linearLayoutManager = new LinearLayoutManager(mActivity);
                mLayoutManager = linearLayoutManager;
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

                break;
            default:
                linearLayoutManager = new LinearLayoutManager(mActivity);
                mLayoutManager = linearLayoutManager;
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    private void loadProductByCategory(int pageNumber) {
        RequestProducts requestProducts = null;

        if (type == AppConstants.TYPE_CATEGORY) {
            requestProducts = new RequestProducts(mActivity, pageNumber, AppConstants.DEFAULT_PER_PAGE, categoryId, type);
        } else if (type == AppConstants.TYPE_FEATURED) {
            requestProducts = new RequestProducts(mActivity, pageNumber, AppConstants.DEFAULT_PER_PAGE, HttpParams.KEY_FEATURED, type);
        } else if (type == AppConstants.TYPE_RECENT) {
            requestProducts = new RequestProducts(mActivity, pageNumber, AppConstants.DEFAULT_PER_PAGE, HttpParams.KEY_RECENT + AppUtility.getRecentProductDateTime(AppConstants.NUMBER_OF_DAYS_BEFORE), type);
        } else if (type == AppConstants.TYPE_POPULAR) {
            requestProducts = new RequestProducts(mActivity, type);
        }

        if (requestProducts != null) {
            requestProducts.setResponseListener(new ResponseListener() {
                @Override
                public void onResponse(Object data) {
                    loadMoreView.setVisibility(View.GONE);
                    hideLoader();

                    if (data != null) {
                        productList.addAll((ArrayList<ProductDetail>) data);
                        if (!productList.isEmpty()) {
                            mProductListAdapter.notifyDataSetChanged();
                        } else {
                            showEmptyView();
                        }
                    } else {
                        showEmptyView();
                    }

                }
            });
            requestProducts.execute();
        }
    }

    private void onScrolledMore() {
        int visibleItemCount, totalItemCount, pastVisiblesItems;
        if (mCurrentLayoutManagerType == LayoutManagerType.GRID_LAYOUT_MANAGER) {
            visibleItemCount = gridLayoutManager.getChildCount();
            totalItemCount = gridLayoutManager.getItemCount();
            pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();
        } else {
            visibleItemCount = linearLayoutManager.getChildCount();
            totalItemCount = linearLayoutManager.getItemCount();
            pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
        }
        if (loading) {
            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                loading = false;
                loadMoreView.setVisibility(View.VISIBLE);
                pageNumber = pageNumber + 1;
                loadProductByCategory(pageNumber);
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

    private void toggleView() {
        if (mCurrentLayoutManagerType == LayoutManagerType.LINEAR_LAYOUT_MANAGER) {
            viewToggle.setImageResource(R.drawable.ic_list);
            setRecyclerViewLayoutManager(rvProductList, LayoutManagerType.GRID_LAYOUT_MANAGER);

            mProductListAdapter = new ProductListAdapter(mContext, productList, ListTypeShow.GRID);
            rvProductList.setAdapter(mProductListAdapter);

        } else {
            viewToggle.setImageResource(R.drawable.ic_grid);
            setRecyclerViewLayoutManager(rvProductList, LayoutManagerType.LINEAR_LAYOUT_MANAGER);

            mProductListAdapter = new ProductListAdapter(mContext, productList, ListTypeShow.LINEAR);
            rvProductList.setAdapter(mProductListAdapter);
        }
    }

}
