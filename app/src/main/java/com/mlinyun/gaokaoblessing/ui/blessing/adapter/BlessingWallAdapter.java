package com.mlinyun.gaokaoblessing.ui.blessing.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mlinyun.gaokaoblessing.data.entity.BlessingEntity;
import com.mlinyun.gaokaoblessing.databinding.ItemBlessingWallBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 祈福墙适配器
 */
public class BlessingWallAdapter extends RecyclerView.Adapter<BlessingWallAdapter.BlessingViewHolder> {

    private List<BlessingEntity> blessings = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

    // 点击监听器
    private OnBlessingClickListener onBlessingClickListener;
    private OnLikeClickListener onLikeClickListener;

    public interface OnBlessingClickListener {
        void onBlessingClick(BlessingEntity blessing);
    }

    public interface OnLikeClickListener {
        void onLikeClick(BlessingEntity blessing);
    }

    public void setOnBlessingClickListener(OnBlessingClickListener listener) {
        this.onBlessingClickListener = listener;
    }

    public void setOnLikeClickListener(OnLikeClickListener listener) {
        this.onLikeClickListener = listener;
    }

    /**
     * 更新祈福列表
     */
    public void updateBlessings(List<BlessingEntity> blessings) {
        this.blessings = blessings != null ? blessings : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BlessingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBlessingWallBinding binding = ItemBlessingWallBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BlessingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BlessingViewHolder holder, int position) {
        BlessingEntity blessing = blessings.get(position);
        holder.bind(blessing);
    }

    @Override
    public int getItemCount() {
        return blessings.size();
    }

    class BlessingViewHolder extends RecyclerView.ViewHolder {
        private final ItemBlessingWallBinding binding;

        public BlessingViewHolder(ItemBlessingWallBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(BlessingEntity blessing) {
            // 设置祈福内容
            binding.tvBlessingContent.setText(blessing.getContent());

            // 设置学生信息
            binding.tvStudentName.setText(blessing.getStudentName());

            // 设置作者信息
            if (blessing.getAuthorName() != null && !blessing.getAuthorName().isEmpty()) {
                binding.tvAuthorName.setText("来自 " + blessing.getAuthorName());
                binding.tvAuthorName.setVisibility(android.view.View.VISIBLE);
            } else {
                binding.tvAuthorName.setVisibility(android.view.View.GONE);
            }

            // 设置时间
            if (blessing.getCreatedAt() != null) {
                binding.tvCreateTime.setText(dateFormat.format(blessing.getCreatedAt()));
            }

            // 设置分类标签
            if (blessing.getTemplateCategory() != null && !blessing.getTemplateCategory().isEmpty()) {
                binding.tvCategory.setText(blessing.getTemplateCategory());
                binding.tvCategory.setVisibility(android.view.View.VISIBLE);
            } else {
                binding.tvCategory.setVisibility(android.view.View.GONE);
            }

            // 设置点赞数
            binding.tvLikeCount.setText(String.valueOf(blessing.getLikeCount()));

            // 设置评论数
            binding.tvCommentCount.setText(String.valueOf(blessing.getCommentCount()));

            // 设置特效标识
            if (blessing.getEffectType() != null && !"默认".equals(blessing.getEffectType())) {
                binding.ivEffectIcon.setVisibility(android.view.View.VISIBLE);
                // 根据特效类型设置不同图标
                setEffectIcon(blessing.getEffectType());
            } else {
                binding.ivEffectIcon.setVisibility(android.view.View.GONE);
            }

            // 设置学生头像（使用姓名首字母）
            if (blessing.getStudentName() != null && !blessing.getStudentName().isEmpty()) {
                String avatarText = blessing.getStudentName().substring(0, 1);
                binding.tvStudentAvatar.setText(avatarText);
            }

            // 设置点击事件
            binding.getRoot().setOnClickListener(v -> {
                if (onBlessingClickListener != null) {
                    onBlessingClickListener.onBlessingClick(blessing);
                }
            });

            // 设置点赞按钮点击事件
            binding.btnLike.setOnClickListener(v -> {
                if (onLikeClickListener != null) {
                    onLikeClickListener.onLikeClick(blessing);

                    // 添加点赞动画
                    addLikeAnimation();
                }
            });

            // 设置卡片颜色（根据分类）
            setCategoryColor(blessing.getTemplateCategory());
        }

        private void setEffectIcon(String effectType) {
            int iconRes;
            switch (effectType) {
                case "金光闪闪":
                    iconRes = com.mlinyun.gaokaoblessing.R.drawable.ic_effect_golden;
                    break;
                case "彩虹特效":
                    iconRes = com.mlinyun.gaokaoblessing.R.drawable.ic_effect_rainbow;
                    break;
                case "星光点点":
                    iconRes = com.mlinyun.gaokaoblessing.R.drawable.ic_effect_star;
                    break;
                case "花瓣飞舞":
                    iconRes = com.mlinyun.gaokaoblessing.R.drawable.ic_effect_flower;
                    break;
                default:
                    iconRes = com.mlinyun.gaokaoblessing.R.drawable.ic_effect_default;
                    break;
            }
            binding.ivEffectIcon.setImageResource(iconRes);
        }

        private void setCategoryColor(String category) {
            int colorRes;
            switch (category) {
                case "学业":
                    colorRes = com.mlinyun.gaokaoblessing.R.color.category_study;
                    break;
                case "健康":
                    colorRes = com.mlinyun.gaokaoblessing.R.color.category_health;
                    break;
                case "平安":
                    colorRes = com.mlinyun.gaokaoblessing.R.color.category_safety;
                    break;
                case "成功":
                    colorRes = com.mlinyun.gaokaoblessing.R.color.category_success;
                    break;
                case "鼓励":
                    colorRes = com.mlinyun.gaokaoblessing.R.color.category_encourage;
                    break;
                default:
                    colorRes = com.mlinyun.gaokaoblessing.R.color.card_background;
                    break;
            }

            // 设置左边缘颜色条
            binding.vCategoryIndicator.setBackgroundColor(
                    binding.getRoot().getContext().getColor(colorRes)
            );
        }

        private void addLikeAnimation() {
            // 添加点赞动画效果
            binding.btnLike.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(150)
                    .withEndAction(() -> {
                        binding.btnLike.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(150)
                                .start();
                    })
                    .start();
        }
    }
}
