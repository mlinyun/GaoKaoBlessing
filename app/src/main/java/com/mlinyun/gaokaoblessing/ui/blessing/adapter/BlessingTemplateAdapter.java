package com.mlinyun.gaokaoblessing.ui.blessing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mlinyun.gaokaoblessing.R;

/**
 * 祈福模板适配器
 */
public class BlessingTemplateAdapter extends RecyclerView.Adapter<BlessingTemplateAdapter.ViewHolder> {

    private String[] templates;
    private OnTemplateClickListener listener;

    /**
     * 模板点击接口
     */
    public interface OnTemplateClickListener {
        void onTemplateClick(String template);
    }

    public BlessingTemplateAdapter(String[] templates, OnTemplateClickListener listener) {
        this.templates = templates;
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
        String template = templates[position];
        holder.tvTemplateText.setText(template);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTemplateClick(template);
            }
        });
    }

    @Override
    public int getItemCount() {
        return templates.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTemplateText;

        ViewHolder(View itemView) {
            super(itemView);
            tvTemplateText = itemView.findViewById(R.id.tvTemplateText);
        }
    }
}
