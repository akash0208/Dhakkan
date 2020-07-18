package com.shopby.dhakkan.adapter;


import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopby.dhakkan.R;
import com.shopby.dhakkan.listener.OnItemClickListener;
import com.shopby.dhakkan.model.AttributeValueModel;

import java.util.ArrayList;

public class AttributeValueAdapter extends RecyclerView.Adapter<AttributeValueAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<AttributeValueModel> dataList;

    // Listener
    public OnItemClickListener mListener;

    public AttributeValueAdapter(Context context, ArrayList<AttributeValueModel> mDataList) {
        this.mContext = context;
        this.dataList = mDataList;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAttributeValue;

        public ViewHolder(final View itemView, int viewType, final OnItemClickListener mListener) {
            super(itemView);
            tvAttributeValue = itemView.findViewById(R.id.tvAttributeValue);

            tvAttributeValue.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemListener(v, getLayoutPosition());
                }
            });

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_atribute_value, parent, false);
        return new ViewHolder(view, viewType, mListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.tvAttributeValue.setText(dataList.get(position).getName());

        if (dataList.get(position).isSelect()) {
            holder.tvAttributeValue.setBackgroundResource(R.drawable.bg_attr_selected);
            holder.tvAttributeValue.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        } else {
            holder.tvAttributeValue.setBackgroundResource(R.drawable.bg_attr_normal);
            holder.tvAttributeValue.setTextColor(ContextCompat.getColor(mContext, R.color.product_name_color));
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
