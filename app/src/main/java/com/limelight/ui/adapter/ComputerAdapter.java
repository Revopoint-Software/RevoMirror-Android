package com.limelight.ui.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.limelight.PcViewActivity;
import com.limelight.R;
import com.limelight.nvstream.http.ComputerDetails;
import com.limelight.nvstream.http.PairingManager;
import com.limelight.utils.LanguageUtils;
import com.limelight.utils.ScreenUtil;
import com.limelight.utils.SharedPreferenceUtil;
import com.limelight.utils.ViewUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ComputerAdapter extends RecyclerView.Adapter<ComputerAdapter.VH> {
    public interface Listener {
        void onItemClick(View view, int position, PcViewActivity.ComputerObject info);

        void onItemLongClick(View view, int position, PcViewActivity.ComputerObject info);

        void onAddItem();
    }

    private static final String TAG = ComputerAdapter.class.getSimpleName();
    private final Listener mListener;
    private final List<PcViewActivity.ComputerObject> mList = new ArrayList<>();

    public ComputerAdapter(Listener listener) {
        mListener = listener;
    }

    /**
     * 清空数据
     */
    public void clearDatas() {
        mList.clear();
        notifyDataSetChanged();
    }

    /**
     * 设置列表数据
     */
    public void setNewDatas(List<PcViewActivity.ComputerObject> datas) {
        mList.clear();
        if (datas != null) {
            mList.addAll(datas);
        }
        sortList();
        notifyDataSetChanged();
    }

    /**
     * 添加列表数据
     */
    public void addItem(PcViewActivity.ComputerObject info) {
        for (PcViewActivity.ComputerObject item : mList) {
            if (info.equals(item)) {
                return;
            }
        }
        mList.add(info);
        sortList();
        notifyDataSetChanged();
    }

    /**
     * 添加列表数据
     */
    public void addItem(int position, PcViewActivity.ComputerObject info) {
        if (position < 0 || position > mList.size()) {
            return;
        }
        mList.add(position, info);
        sortList();
        notifyDataSetChanged();
    }

    private void sortList() {
        Collections.sort(mList, new Comparator<PcViewActivity.ComputerObject>() {
            @Override
            public int compare(PcViewActivity.ComputerObject lhs, PcViewActivity.ComputerObject rhs) {
                return lhs.details.name.toLowerCase().compareTo(rhs.details.name.toLowerCase());
            }
        });
        String lastDeviceUuid = SharedPreferenceUtil.getLastDeviceUuid();
        if (!TextUtils.isEmpty(lastDeviceUuid)) {
            int findIndex = -1;
            PcViewActivity.ComputerObject findItem = null;
            for (int i = 0; i < mList.size(); i++) {
                PcViewActivity.ComputerObject item = mList.get(i);
                if (TextUtils.equals(item.details.uuid, lastDeviceUuid)) {
                    findIndex = i;
                    findItem = item;
                    break;
                }
            }
            if (findIndex >= 0) {
                mList.remove(findIndex);
                mList.add(0, findItem);
            }
        }
    }

    /**
     * 列表数据
     */
    public void updateItem(int position, PcViewActivity.ComputerObject info) {
        if (position < 0 || position > mList.size()) {
            return;
        }
        mList.set(position, info);
        sortList();
        notifyDataSetChanged();
    }

    /**
     * 移除列表数据
     */
    public void removeItem(int position) {
        mList.remove(position);
        notifyDataSetChanged();
    }

    public List<PcViewActivity.ComputerObject> getList() {
        return mList;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pc_grid_item, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, int position) {
        if (position < mList.size()) {
            PcViewActivity.ComputerObject obj = mList.get(position);
            holder.layoutItemContent.setVisibility(View.VISIBLE);
            holder.layoutItemAdd.setVisibility(View.GONE);

            if (obj.details.state == ComputerDetails.State.UNKNOWN) {
                holder.prgView.setVisibility(View.VISIBLE);
            } else {
                holder.prgView.setVisibility(View.INVISIBLE);
            }

            String lastDeviceUuid = SharedPreferenceUtil.getLastDeviceUuid();
            if (!TextUtils.isEmpty(lastDeviceUuid)) {
                if (TextUtils.equals(obj.details.uuid, lastDeviceUuid)) {
                    holder.tvLastFlag.setVisibility(View.VISIBLE);
                    holder.tvLastFlag.setText(LanguageUtils.getString(R.string.Last));
                    holder.tvName.setMaxWidth(ScreenUtil.dp2px(holder.imgView.getContext(), 87));
                } else {
                    holder.tvLastFlag.setVisibility(View.GONE);
                    holder.tvName.setMaxWidth(ScreenUtil.dp2px(holder.imgView.getContext(), 133));
                }
            } else {
                holder.tvLastFlag.setVisibility(View.GONE);
                holder.tvName.setMaxWidth(ScreenUtil.dp2px(holder.imgView.getContext(), 133));
            }

            holder.tvName.setText(obj.details.name);
            if (obj.details.state == ComputerDetails.State.ONLINE) {
                holder.tvName.setAlpha(1.0f);
            } else {
                holder.tvName.setAlpha(0.4f);
            }

            if (obj.details.state == ComputerDetails.State.OFFLINE) {
                holder.imgView.setImageResource(R.drawable.ic_mirror_offline);
            }
            // We must check if the status is exactly online and unpaired
            // to avoid colliding with the loading spinner when status is unknown
            else if (obj.details.state == ComputerDetails.State.ONLINE &&
                    obj.details.pairState == PairingManager.PairState.NOT_PAIRED) {
                holder.imgView.setImageResource(R.drawable.ic_mirror_nopaired);
            } else {
                holder.imgView.setImageResource(R.drawable.ic_mirror_normal);
            }

            holder.layoutItemContent.setTag(holder.layoutItemContent.getId(), position);
            holder.layoutItemContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewUtil.setAvoidRepeatClick(v, 1000);

                    if (mListener != null) {
                        int pos = (int) v.getTag(v.getId());
                        if (pos >= 0 && pos < mList.size()) {
                            mListener.onItemClick(v, pos, mList.get(pos));
                        }
                    }
                }
            });
            holder.layoutItemContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mListener != null) {
                        int pos = (int) v.getTag(v.getId());
                        if (pos >= 0 && pos < mList.size()) {
                            mListener.onItemLongClick(v, pos, mList.get(pos));
                            return true;
                        }
                    }
                    return false;
                }
            });
        } else {
            //添加
            holder.layoutItemContent.setVisibility(View.GONE);
            holder.layoutItemAdd.setVisibility(View.VISIBLE);
            holder.layoutItemAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onAddItem();
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    public int getItemCountReally() {
        return mList.size();
    }

    public PcViewActivity.ComputerObject getItem(int position) {
        if (position < mList.size()) {
            return mList.get(position);
        }
        return null;
    }

    class VH extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View layoutItemContent;
        final View layoutItemAdd;
        final ImageView imgView;
        final ProgressBar prgView;
        final TextView tvName;
        final TextView tvLastFlag;

        VH(final View itemView) {
            super(itemView);
            layoutItemContent = itemView.findViewById(R.id.layoutItemContent);
            layoutItemAdd = itemView.findViewById(R.id.layoutItemAdd);
            imgView = itemView.findViewById(R.id.imgView);
            prgView = itemView.findViewById(R.id.prgView);
            tvName = itemView.findViewById(R.id.tvName);
            tvLastFlag = itemView.findViewById(R.id.tvLastFlag);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Log.d(TAG, "onClick, getAdapterPosition=" + pos);
            if (pos >= 0 && pos < mList.size()) {

            }
        }
    }

}
