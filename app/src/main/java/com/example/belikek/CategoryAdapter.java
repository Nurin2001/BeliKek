package com.example.belikek;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {

    private OnCategoryClickListener listener;
    private final List<CategoryUI> items = new ArrayList<>();
    private int selected = RecyclerView.NO_POSITION;
    private Context context;

    public interface OnCategoryClickListener {
        void onItemClick(CategoryUI item, int position);
    }

    public CategoryAdapter(Context context, OnCategoryClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void submit(List<CategoryUI> list) {
        items.clear(); if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    public void setSelected(int position) {
        int old = selected; selected = position;
        if (old != RecyclerView.NO_POSITION) notifyItemChanged(old);
        if (selected != RecyclerView.NO_POSITION) notifyItemChanged(selected);
    }

    public int getSelected() {
        return selected;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int vType) {
        View v = LayoutInflater.from(p.getContext())
                .inflate(R.layout.category_item, p, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        CategoryUI it = items.get(pos);
        setImageByName(h.icon, "ic_" + it.getId());
        h.label.setText(it.getLabel());
        //mierza tukar
        h.layout.setSelected(pos == selected);
        h.layout.setOnClickListener(v -> {
            setSelected(pos);
            if (listener != null) {
                listener.onItemClick(it, pos);
            }
        });
    }

    private void setImageByName(ImageView imageView, String imageName) {
        try {
            Log.d("image category", imageName);
            int resourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
            Log.d("resource id", String.valueOf(resourceId));
            if (resourceId != 0) {
                imageView.setImageResource(resourceId);
            } else {
                // Image not found, use default
                imageView.setImageResource(R.drawable.ic_default_background);
            }
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.ic_default_background);
        }
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView icon; TextView label;
        ConstraintLayout layout;
        VH(@NonNull View v) {
            super(v);
            icon  = v.findViewById(R.id.category_icon);
            label = v.findViewById(R.id.category_tv);
            layout = v.findViewById(R.id.sidebar_category_layout);
        }
    }
}
