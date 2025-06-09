package com.mlinyun.gaokaoblessing.ui.blessing.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mlinyun.gaokaoblessing.databinding.ItemRecentBlessingBinding;
import com.mlinyun.gaokaoblessing.ui.blessing.model.BlessingRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 最近祈福记录适配器
 */
public class RecentBlessingsAdapter extends RecyclerView.Adapter<RecentBlessingsAdapter.BlessingViewHolder> {

    private List<BlessingRecord> blessings = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

    public void updateBlessings(List<BlessingRecord> newBlessings) {
        this.blessings.clear();
        this.blessings.addAll(newBlessings);
        notifyDataSetChanged();
    }

    /**
     * 添加新的祈福记录到列表顶部
     */
    public void addBlessing(com.mlinyun.gaokaoblessing.ui.blessing.model.RecentBlessing blessing) {
        // 转换RecentBlessing为BlessingRecord
        BlessingRecord record = new BlessingRecord();
        record.setContent(blessing.getTitle());
        record.setCreateTime(new java.util.Date(blessing.getTime()));
        record.setType("祈福");

        blessings.add(0, record);
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public BlessingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecentBlessingBinding binding = ItemRecentBlessingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BlessingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BlessingViewHolder holder, int position) {
        BlessingRecord blessing = blessings.get(position);
        holder.bind(blessing);
    }

    @Override
    public int getItemCount() {
        return Math.min(blessings.size(), 3); // 最多显示3条
    }

    class BlessingViewHolder extends RecyclerView.ViewHolder {
        private ItemRecentBlessingBinding binding;

        public BlessingViewHolder(ItemRecentBlessingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(BlessingRecord blessing) {
            binding.tvBlessingTitle.setText(blessing.getContent());
            binding.tvBlessingTime.setText(dateFormat.format(blessing.getCreateTime()));

            // 设置祈福类型图标和颜色
            switch (blessing.getType()) {
                case "学业":
                    binding.ivBlessingIcon.setImageResource(android.R.drawable.ic_menu_edit);
                    break;
                case "健康":
                    binding.ivBlessingIcon.setImageResource(android.R.drawable.ic_menu_mylocation);
                    break;
                case "平安":
                    binding.ivBlessingIcon.setImageResource(android.R.drawable.ic_menu_myplaces);
                    break;
                default:
                    binding.ivBlessingIcon.setImageResource(android.R.drawable.ic_menu_send);
                    break;
            }

            // 设置状态
            binding.tvStatus.setText("已发送");
        }
    }
}
