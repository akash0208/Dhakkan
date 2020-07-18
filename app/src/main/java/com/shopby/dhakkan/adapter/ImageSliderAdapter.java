package com.shopby.dhakkan.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.shopby.dhakkan.R;
import com.shopby.dhakkan.listener.OnItemClickListener;

public class ImageSliderAdapter extends PagerAdapter {
 
    private ArrayList<String> images;
    private LayoutInflater inflater;
    private Context mContext;

    // Listener
    private OnItemClickListener mListener;
 
    public ImageSliderAdapter(Context context, ArrayList<String> images) {
        this.mContext = context;
        this.images=images;
        inflater = LayoutInflater.from(context);
    }
 
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
 
    @Override
    public int getCount() {
        return images.size();
    }
 
    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup view, final int position) {

        View imageLayout = inflater.inflate(R.layout.item_image_slider, view, false);
        final ImageView imageView = imageLayout.findViewById(R.id.image);


        Glide.with(mContext)
                .load(images.get(position))
                .placeholder(R.color.imgPlaceholder)
                .into(imageView);

        view.addView(imageLayout);

        imageView.setOnClickListener(v -> {
            if (mListener!=null) {
                mListener.onItemListener(view, position);
            }
        });

        return imageLayout;
    }
 
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    public void setItemClickListener(OnItemClickListener mListener){
        this.mListener = mListener;
    }

}