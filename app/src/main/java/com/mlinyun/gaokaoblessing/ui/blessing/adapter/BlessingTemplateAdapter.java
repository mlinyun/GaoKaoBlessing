package com.mlinyun.gaokaoblessing.ui.blessing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mlinyun.gaokaoblessing.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 祈福模板适配器
 */
public class BlessingTemplateAdapter extends RecyclerView.Adapter<BlessingTemplateAdapter.ViewHolder> {

    private List<String> templates = new ArrayList<>();
    private OnTemplateSelectedListener listener;

    /**
     * 模板选择接口
     */
    public interface OnTemplateSelectedListener {
        void onTemplateSelected(String template);
    }

    /**
     * 模板点击接口 (保持向后兼容)
     */
    public interface OnTemplateClickListener {
        void onTemplateClick(String template);
    }

    // 默认构造函数
    public BlessingTemplateAdapter() {
    }

    // 兼容旧版本的构造函数
    public BlessingTemplateAdapter(String[] templates, OnTemplateClickListener listener) {
        this.templates = new ArrayList<>();
        for (String template : templates) {
            this.templates.add(template);
        }
        this.listener = template -> listener.onTemplateClick(template);
    }

    /**
     * 设置模板列表
     */
    public void setTemplates(List<String> templates) {
        this.templates = templates;
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blessing_template, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String template = templates.get(position);
        holder.tvTemplateText.setText(template);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTemplateSelected(template);
            }
        });
    }

    @Override
    public int getItemCount() {
        return templates.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTemplateText;

        ViewHolder(View itemView) {
            super(itemView);
            tvTemplateText = itemView.findViewById(R.id.tvTemplateText);
        }
    }
}
