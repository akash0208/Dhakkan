package com.shopby.dhakkan.adapter;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shopby.dhakkan.R;
import com.shopby.dhakkan.listener.OnItemClickListener;
import com.shopby.dhakkan.model.Category;

import java.util.ArrayList;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Category> dataList;

    // Listener
    public static OnItemClickListener mListener;

    public CategoryListAdapter(Context context, ArrayList<Category> dataList) {
        this.mContext = context;
        this.dataList = dataList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCategory;
        private TextView tvCategoryName;
        private LinearLayout selectedBoarderView;

        public ViewHolder(final View itemView, int viewType) {
            super(itemView);

            ivCategory = (ImageView) itemView.findViewById(R.id.ivProductImage);
            tvCategoryName = (TextView) itemView.findViewById(R.id.tvCategoryName);
            selectedBoarderView = (LinearLayout) itemView.findViewById(R.id.selectedBoarderView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new ViewHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Category category = dataList.get(position);

        holder.tvCategoryName.setText(Html.fromHtml(category.name));

        Glide.with(mContext)
                .load(category.image)
                .placeholder(R.color.imgPlaceholder)
                .centerCrop()
                .into(holder.ivCategory);

        // listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mListener.onItemListener(view,position);
            }
        });
        if(dataList.get(position).isSelected) {
            holder.selectedBoarderView.setBackgroundResource(R.drawable.bg_selected_img);
        }
        else {
            holder.selectedBoarderView.setBackgroundResource(0);
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
    public void setItemClickListener(OnItemClickListener mListener){
        this.mListener = mListener;
    }
}
