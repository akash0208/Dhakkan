package com.shopby.dhakkan.adapter;


import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shopby.dhakkan.R;
import com.shopby.dhakkan.data.constant.AppConstants;
import com.shopby.dhakkan.listener.OnItemClickListener;
import com.shopby.dhakkan.model.OrderItem;

import java.util.ArrayList;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<OrderItem> dataList;

    // Listener
    public static OnItemClickListener mListener;

    public OrderListAdapter(Context activity, ArrayList<OrderItem> dataList) {
        this.mContext = activity;
        this.dataList = dataList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageButton cancelOrder;
        private TextView tvOrderNumber, tvPrice, tvOrderDate, tvOrderStatus;

        public ViewHolder(final View itemView, int viewType) {
            super(itemView);

            cancelOrder = (ImageButton) itemView.findViewById(R.id.cancelOrder);
            tvOrderNumber = (TextView) itemView.findViewById(R.id.tvOrderNumber);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvOrderStatus = (TextView) itemView.findViewById(R.id.tvOrderStatus);
            tvOrderDate = (TextView) itemView.findViewById(R.id.tvOrderDate);

            // listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemListener(view, getLayoutPosition());
                }
            });
            cancelOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemListener(view, getLayoutPosition());
                }
            });

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_list, parent, false);

        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        OrderItem orderHistory = dataList.get(position);

        holder.tvOrderNumber.setText(mContext.getResources().getString(R.string.order_id) + orderHistory.orderId);
        holder.tvPrice.setText(AppConstants.CURRENCY + String.valueOf(orderHistory.totalPrice));
        holder.tvOrderDate.setText(orderHistory.orderDate);
        holder.tvOrderStatus.setText(mContext.getResources().getString(R.string.status) + orderHistory.status);

        if(orderHistory.status != null && orderHistory.status.equals(AppConstants.ORDER_STATUS_PENDING)) {
            holder.cancelOrder.setVisibility(View.VISIBLE);
            holder.tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorPending));
        } else if(orderHistory.status != null && orderHistory.status.equals(AppConstants.ORDER_STATUS_COMPLETED)) {
            holder.cancelOrder.setVisibility(View.INVISIBLE);
            holder.tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorCompleted));
        } else {
            holder.cancelOrder.setVisibility(View.INVISIBLE);
            holder.tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorProcessing));
        }
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
