package com.shopby.dhakkan.adapter;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.R;
import com.shopby.dhakkan.listener.OnItemClickListener;
import com.shopby.dhakkan.model.ProductDetail;
import com.shopby.dhakkan.utils.AppUtility;

import java.util.ArrayList;

public class HomeProdListAdapter extends RecyclerView.Adapter<HomeProdListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ProductDetail> dataList;
    private int displaySize = 0;

    // Listener
    private OnItemClickListener mListener;

    public HomeProdListAdapter(Context context, ArrayList<ProductDetail> dataList) {
        this.mContext = context;
        this.dataList = dataList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivCategory;
        private TextView price, tvProductName;

        public ViewHolder(final View itemView, int viewType, final OnItemClickListener mListener) {
            super(itemView);

            ivCategory = (ImageView) itemView.findViewById(R.id.ivProductImage);
            tvProductName = (TextView) itemView.findViewById(R.id.tvProductName);
            price = (TextView) itemView.findViewById(R.id.price);

            // listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemListener(view, getLayoutPosition());
                    }
                }
            });

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rectangle, parent, false);
            return new ViewHolder(view,viewType, mListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final ProductDetail product = dataList.get(position);

        if (product.name != null) {
            holder.tvProductName.setText(AppUtility.showHtml(product.name));
        }
        if (product.onSaleStatus) {
            holder.price.setText(AppConstants.CURRENCY + product.sellPrice);
        } else {
            holder.price.setText(AppConstants.CURRENCY + product.regularPrice);
        }

        Glide.with(mContext)
                .load(product.imageList.get(0))
                .placeholder(R.color.imgPlaceholder)
                .centerCrop()
                .into(holder.ivCategory);

    }

    @Override
    public int getItemCount() {
        if(displaySize == AppConstants.VALUE_ZERO || displaySize > dataList.size()) {
            return dataList.size();
        }
        else {
            return displaySize;
        }
    }

    @Override
    public int getItemViewType(int position) {
            return 0;
    }
    public void setItemClickListener(OnItemClickListener mListener){
        this.mListener = mListener;
    }
    public void setDisplayCount(int numberOfEntries) {
        displaySize = numberOfEntries;
        notifyDataSetChanged();

    }
}
