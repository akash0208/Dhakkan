package com.shopby.dhakkan.adapter;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopby.dhakkan.R;
import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.listener.OnItemClickListener;
import com.shopby.dhakkan.model.CouponItem;

import java.util.ArrayList;

public class CouponListAdapter extends RecyclerView.Adapter<CouponListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<CouponItem> dataList;

    // Listener
    public static OnItemClickListener mListener;

    public CouponListAdapter(Context activity, ArrayList<CouponItem> dataList) {
        this.mContext = activity;
        this.dataList = dataList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCouponCode, tvAmount, tvDescription, tvExpireDate;

        public ViewHolder(final View itemView, int viewType) {
            super(itemView);

            tvCouponCode = (TextView) itemView.findViewById(R.id.tvCouponCode);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            tvAmount = (TextView) itemView.findViewById(R.id.tvAmount);
            tvExpireDate = (TextView) itemView.findViewById(R.id.tvExpireDate);

            // listener
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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon_list, parent, false);

        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        CouponItem couponItem = dataList.get(position);

        holder.tvCouponCode.setText(mContext.getResources().getString(R.string.coupon_code) + couponItem.couponCode);
        holder.tvDescription.setText(couponItem.description);
        holder.tvAmount.setText(AppConstants.CURRENCY + String.valueOf(couponItem.amount));
        holder.tvExpireDate.setText(mContext.getResources().getString(R.string.expire_date) + couponItem.expireDate);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public void setItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }
}
