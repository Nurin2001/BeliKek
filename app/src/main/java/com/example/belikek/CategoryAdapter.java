package com.example.belikek;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.belikek.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {

    public interface OnClick { void onItem(CategoryUi item, int position); }

    private final List<CategoryUi> items = new ArrayList<>();
    private int selected = RecyclerView.NO_POSITION;
    private final OnClick onClick;

    public CategoryAdapter(OnClick onClick) { this.onClick = onClick; }

    public void submit(List<CategoryUi> list) {
        items.clear(); if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    public void setSelected(int position) {
        int old = selected; selected = position;
        if (old != RecyclerView.NO_POSITION) notifyItemChanged(old);
        if (selected != RecyclerView.NO_POSITION) notifyItemChanged(selected);
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int vType) {
        View v = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_category, p, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        CategoryUi it = items.get(pos);
        h.icon.setImageResource(it.iconRes);
        h.label.setText(it.label);
        h.itemView.setSelected(pos == selected);
        h.itemView.setOnClickListener(v -> {
            if (onClick != null) onClick.onItem(it, pos);
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView icon; TextView label;
        VH(@NonNull View v) {
            super(v);
            icon  = v.findViewById(R.id.imgIcon);
            label = v.findViewById(R.id.tvLabel);
        }
    }
}
