package com.example.belikek;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/** Horizontal banner adapter for rvPromotion */
public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.VH> {

    public interface OnPromoClick { void onClick(PromotionItem item, int position); }

    private final List<PromotionItem> items = new ArrayList<>();
    private final OnPromoClick listener;

    public PromotionAdapter(OnPromoClick listener) {
        this.listener = listener;
    }

    /** Replace data */
    public void submit(List<PromotionItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_promotion_banner, parent, false); // your banner item layout
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        PromotionItem it = items.get(position);

        ImageLoader.imageLoader(h.image.getContext(), h.image, it.imageUrl);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(it, position);
        });

        // Optional: nicer outer margins for first/last items
        int start = position == 0 ? dp(16, h) : dp(8, h);
        int end   = position == getItemCount() - 1 ? dp(16, h) : dp(8, h);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) h.itemView.getLayoutParams();
        lp.setMarginStart(start);
        lp.setMarginEnd(end);
        h.itemView.setLayoutParams(lp);
    }

    private int dp(int v, RecyclerView.ViewHolder h) {
        float d = h.itemView.getResources().getDisplayMetrics().density;
        return Math.round(v * d);
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        VH(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.promotion_img); // id inside item_promotion_banner.xml
        }
    }

    /** Simple model for promotions */
    public static class PromotionItem {
        public String id;
        public String imageUrl;   // required
        public String deepLink;   // optional (tap action)

        public PromotionItem() {}
        public PromotionItem(String id, String imageUrl, String deepLink) {
            this.id = id; this.imageUrl = imageUrl; this.deepLink = deepLink;
        }
    }
}
