package com.mlinyun.gaokaoblessing.ui.blessing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mlinyun.gaokaoblessing.R;
import com.mlinyun.gaokaoblessing.data.entity.BlessingTemplateEntity;
import com.mlinyun.gaokaoblessing.databinding.ItemBlessingTemplateAdvancedBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * 高级祈福模板适配器 - 支持完整的模板实体显示
 */
public class AdvancedBlessingTemplateAdapter extends RecyclerView.Adapter<AdvancedBlessingTemplateAdapter.ViewHolder> {

    private List<BlessingTemplateEntity> templates = new ArrayList<>();
    private OnTemplateSelectedListener listener;
    private String selectedTemplate;

    /**
     * 模板选择接口
     */
    public interface OnTemplateSelectedListener {
        void onTemplateSelected(String template);
    }

    /**
     * 更新模板列表
     */
    public void updateTemplates(List<BlessingTemplateEntity> templates) {
        this.templates = templates != null ? templates : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * 设置模板选择监听器
     */
    public void setOnTemplateSelectedListener(OnTemplateSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBlessingTemplateAdvancedBinding binding = ItemBlessingTemplateAdvancedBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BlessingTemplateEntity template = templates.get(position);
        holder.bind(template);
    }

    @Override
    public int getItemCount() {
        return templates.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemBlessingTemplateAdvancedBinding binding;

        public ViewHolder(ItemBlessingTemplateAdvancedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(BlessingTemplateEntity template) {
            // 设置模板内容
            binding.tvTemplateContent.setText(template.getContent());

            // 设置分类标签
            binding.tvTemplateCategory.setText(template.getCategory());

            // 设置评分
            binding.rbTemplateRating.setRating(template.getRating());

            // 设置标签
            if (template.getTags() != null && !template.getTags().isEmpty()) {
                binding.tvTemplateTags.setText(template.getTags());
                binding.tvTemplateTags.setVisibility(View.VISIBLE);
            } else {
                binding.tvTemplateTags.setVisibility(View.GONE);
            }

            // 设置使用次数
            binding.tvUsageCount.setText(String.format("已使用 %d 次", template.getUsageCount()));

            // 设置点击事件
            binding.getRoot().setOnClickListener(v -> {
                selectedTemplate = template.getContent();
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onTemplateSelected(template.getContent());
                }
            });

            // 设置选中状态
            boolean isSelected = template.getContent().equals(selectedTemplate);
            binding.getRoot().setSelected(isSelected);

            // 设置选中指示器
            binding.ivSelectedIndicator.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            // 设置卡片背景色
            if (isSelected) {
                binding.cardTemplate.setCardBackgroundColor(
                        binding.getRoot().getContext().getColor(R.color.blessing_primary_light));
            } else {
                binding.cardTemplate.setCardBackgroundColor(
                        binding.getRoot().getContext().getColor(R.color.card_background));
            }
        }
    }
}
