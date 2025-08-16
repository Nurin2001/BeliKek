package com.example.belikek;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {

    public interface OnClick { void onItem(ProductUI item); }

    private final List<ProductUI> items = new ArrayList<>();
    private final OnClick onClick;

    public ProductAdapter(OnClick onClick) { this.onClick = onClick; }

    public void submit(List<ProductUI> list) {
        items.clear(); if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        View view = LayoutInflater.from(p.getContext())
                .inflate(R.layout.menu_item, p, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ProductUI it = items.get(position);
        h.name.setText(it.name);
        h.price.setText(String.format("RM %.2f", it.price));

        // Jika imagePathOrUrl bermula http, load terus; jika tidak, anggap Storage path
        String src = it.imagePathOrUrl;
        if (src != null && (src.startsWith("http://") || src.startsWith("https://"))) {
            Glide.with(h.thumb.getContext())
                    .load(src)
                    .centerCrop()
                    .placeholder(R.drawable.ic_cookies)
                    .error(R.drawable.ic_default_background)
                    .into(h.thumb);
        }

        h.itemView.setOnClickListener(v -> { if (onClick != null) onClick.onItem(it); });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView thumb; TextView name, price;
        VH(@NonNull View v) {
            super(v);
            thumb = v.findViewById(R.id.menu_img);
            name  = v.findViewById(R.id.menu_name_tv);
            price = v.findViewById(R.id.menu_price_tv);
        }
    }
}
