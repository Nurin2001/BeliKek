package com.example.belikek;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CakeOptionAdapter extends RecyclerView.Adapter<CakeOptionAdapter.ViewHolder> {
    private List<CakeOption> cakeOptions;
    private OnOptionSelectedListener listener;

    public interface OnOptionSelectedListener {
        void onOptionSelected(int categoryPosition, int optionPosition, CakeOptionDetail option);
    }

    public CakeOptionAdapter(List<CakeOption> cakeOptions, OnOptionSelectedListener listener) {
        this.cakeOptions = cakeOptions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cake_option_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CakeOption cakeOption = cakeOptions.get(position);

        holder.categoryTitle.setText(cakeOption.getCategoryName());
        holder.pickText.setText(cakeOption.getPickText());

        // Setup inner RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        holder.innerRecyclerView.setLayoutManager(layoutManager);
        holder.innerRecyclerView.setHasFixedSize(true);
        holder.innerRecyclerView.setNestedScrollingEnabled(false);

        CakeOptionDetailAdapter innerAdapter = new CakeOptionDetailAdapter(
                cakeOption.getOptions(),
                (optionPosition, option) -> {
                    if (listener != null) {
                        listener.onOptionSelected(position, optionPosition, option);
                    }
                }
        );
        holder.innerRecyclerView.setAdapter(innerAdapter);
    }

    @Override
    public int getItemCount() {
        return cakeOptions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle, pickText;
        RecyclerView innerRecyclerView;

        ViewHolder(View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.cake_option_tv);
            pickText = itemView.findViewById(R.id.no_of_picks_tv);
            innerRecyclerView = itemView.findViewById(R.id.cake_option_detail_recyclerview);
        }
    }
}