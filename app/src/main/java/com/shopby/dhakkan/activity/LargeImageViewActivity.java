package com.shopby.dhakkan.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.R;
import com.shopby.dhakkan.view.TouchImageView;

/**
 * Created by Nasir on 6/8/17.
 */

public class LargeImageViewActivity extends AppCompatActivity {

    private Context mContext;
    private TouchImageView touchImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initFunctionality();
    }

    private void initView() {
        setContentView(R.layout.activity_large_image);

        mContext = LargeImageViewActivity.this;
        touchImageView = findViewById(R.id.largeImageView);
    }

    private void initFunctionality() {
        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra(AppConstants.KEY_IMAGE_URL);

        Glide.with(mContext).
                load(imageUrl).
                placeholder(R.color.imgPlaceholder).
                into(touchImageView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
