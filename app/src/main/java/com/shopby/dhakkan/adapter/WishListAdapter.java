package com.shopby.dhakkan.adapter;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.R;
import com.shopby.dhakkan.listener.OnItemClickListener;
import com.shopby.dhakkan.model.WishItem;

import java.util.ArrayList;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<WishItem> dataList;

    // Listener
    public static OnItemClickListener mListener;

    public WishListAdapter(Context context, ArrayList<WishItem> dataList) {
        this.mContext = context;
        this.dataList = dataList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProductImage;
        private TextView tvProductName, tvProductPrice, tvRattingCount;
        private RatingBar ratingBar;
        private ImageButton ivRemoveWish;

        public ViewHolder(final View itemView, int viewType) {
            super(itemView);

            ivProductImage = (ImageView) itemView.findViewById(R.id.ivProductImage);
            ivRemoveWish = (ImageButton) itemView.findViewById(R.id.ivRemoveWish);
            tvProductName = (TextView) itemView.findViewById(R.id.tvProductName);
            tvProductPrice = (TextView) itemView.findViewById(R.id.tvProductPrice);
            tvRattingCount = (TextView) itemView.findViewById(R.id.tvRattingCount);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);

            ivRemoveWish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemListener(view, getLayoutPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemListener(view, getLayoutPosition());
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wish_list, parent, false);

        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final WishItem wishItem = dataList.get(position);

        holder.tvProductName.setText(wishItem.name);
        holder.tvProductPrice.setText(AppConstants.CURRENCY + String.valueOf(wishItem.price));
        holder.tvRattingCount.setText(String.valueOf(wishItem.ratting));
        holder.ratingBar.setRating(wishItem.ratting);


        if (!wishItem.images.isEmpty()) {
            Glide.with(mContext)
                    .load(wishItem.images)
                    .placeholder(R.color.imgPlaceholder)
                    .centerCrop()
                    .into(holder.ivProductImage);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return AppConstants.VALUE_ZERO;
    }

    public void setItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }
}
