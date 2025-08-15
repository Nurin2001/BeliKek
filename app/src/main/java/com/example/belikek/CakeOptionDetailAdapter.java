package com.example.belikek;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CakeOptionDetailAdapter extends RecyclerView.Adapter<CakeOptionDetailAdapter.ViewHolder> {
    private List<CakeOptionDetail> options;
    private OnOptionSelectedListener listener;

    public interface OnOptionSelectedListener {
        void onOptionSelected(int position, CakeOptionDetail option);
    }

    public CakeOptionDetailAdapter(List<CakeOptionDetail> options, OnOptionSelectedListener listener) {
        this.options = options;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_cake_option_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CakeOptionDetail option = options.get(position);
        holder.optionButton.setText(option.getOptionName());

        // Set button state based on selection
        if (option.isSelected()) {
            holder.optionButton.setBackground(ContextCompat.getDrawable(
                    holder.itemView.getContext(), R.drawable.rounded_cake_options_selected_btn));
            holder.optionButton.setTextColor(R.color.primaryPink);
        } else {
            holder.optionButton.setBackground(ContextCompat.getDrawable(
                    holder.itemView.getContext(), R.drawable.rounded_cake_options_btn));
            holder.optionButton.setTextColor(R.color.buyNowBtnText);
        }

        holder.optionButton.setOnClickListener(v -> {
            // Handle single selection (unselect others)
            for (CakeOptionDetail opt : options) {
                opt.setSelected(false);
            }
            option.setSelected(true);
            notifyDataSetChanged();

            if (listener != null) {
                listener.onOptionSelected(position, option);
            }
        });
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Button optionButton;

        ViewHolder(View itemView) {
            super(itemView);
            optionButton = itemView.findViewById(R.id.detail_option_btn);
        }
    }
}
