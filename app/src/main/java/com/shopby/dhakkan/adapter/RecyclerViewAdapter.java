package com.shopby.dhakkan.adapter;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shopby.dhakkan.R;
import com.shopby.dhakkan.listener.OnItemClickListener;
import com.shopby.dhakkan.model.Category;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Category> dataList;

    // Listener
    public static OnItemClickListener mListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(final View itemView, int viewType) {
            super(itemView);
        }
    }
    public RecyclerViewAdapter(Context activity, ArrayList<Category> dataList) {
        this.mContext = activity;
        this.dataList = dataList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main, parent, false);
            return new ViewHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Category category = dataList.get(position);

        // listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mListener.onItemListener(view,position);
            }
        });
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
