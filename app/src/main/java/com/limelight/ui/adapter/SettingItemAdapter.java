package com.limelight.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.limelight.R;
import com.limelight.bean.SettingItem;

import java.util.ArrayList;
import java.util.List;

public class SettingItemAdapter extends RecyclerView.Adapter<SettingItemAdapter.ViewHolder> {

    public interface SettingItemCallback {
        void onCallback(SettingItem item);
    }

    private SettingItemCallback callback;
    public int itemValueSelect = 0;
    private final List<SettingItem> mList = new ArrayList<>();

    @NonNull
    @Override
    public SettingItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_item, parent, false);
        return new SettingItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        SettingItem settingItem = mList.get(position);
        holder.tvName.setText(settingItem.getTitle());

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        holder.layoutItem.setLayoutParams(params);

        holder.ivSelected.setVisibility(settingItem.getValue() == itemValueSelect ? View.VISIBLE : View.INVISIBLE);

        holder.layoutItem.setTag(settingItem.getValue());
        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemValueSelect = (Integer) v.getTag();
                notifyDataSetChanged();

                List<SettingItem> localList = getList();
                for (SettingItem item : localList) {
                    if (item.getValue() == itemValueSelect) {
                        if (callback != null) {
                            callback.onCallback(item);
                        }
                    }
                }
            }
        });

        holder.divide_line.setVisibility(position < getItemCount() - 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public List<SettingItem> getList() {
        return mList;
    }

    /**
     * 设置列表数据
     */
    public void setList(List<SettingItem> datas) {
        mList.clear();
        if (datas != null) {
            mList.addAll(datas);
        }
        notifyDataSetChanged();
    }

    public void setItemClickCallback(SettingItemCallback callback) {
        this.callback = callback;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View layoutItem;
        TextView tvName;
        ImageView ivSelected;
        View divide_line;

        ViewHolder(View itemView) {
            super(itemView);

            layoutItem = itemView.findViewById(R.id.layoutItem);
            tvName = itemView.findViewById(R.id.tvName);
            ivSelected = itemView.findViewById(R.id.ivSelected);
            divide_line = itemView.findViewById(R.id.divide_line);
        }
    }
}
