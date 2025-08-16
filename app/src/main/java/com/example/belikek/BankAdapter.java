package com.example.belikek;// BankAdapter.java (same as previous response)
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.BankViewHolder> {

    private List<Bank> bankList;
    private OnBankSelectedListener listener;

    public interface OnBankSelectedListener {
        void onBankSelected(Bank bank);
    }

    public BankAdapter(List<Bank> bankList, OnBankSelectedListener listener) {
        this.bankList = bankList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bank, parent, false);
        return new BankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BankViewHolder holder, int position) {
        Bank bank = bankList.get(position);
        holder.bind(bank);
    }

    @Override
    public int getItemCount() {
        return bankList.size();
    }


    class BankViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgBankLogo;
        private TextView txtBankName;

        public BankViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBankLogo = itemView.findViewById(R.id.img_bank_logo);
            txtBankName = itemView.findViewById(R.id.txt_bank_name);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onBankSelected(bankList.get(position));
                }
            });
        }

        public void bind(Bank bank) {
            txtBankName.setText(bank.getBankName() + " " + bank.getBankCode());
            imgBankLogo.setImageResource(R.drawable.ic_bank_default);
            setBankLogo(bank.getBankCode());
        }

        private void setBankLogo(String bankCode) {
            // Set bank logos based on actual ToyyibPay bank codes
            switch (bankCode) {
                case "AGRO01": // Muamalat
                     imgBankLogo.setImageResource(R.drawable.agrobank);
                     break;
                case "BMMB0341": // Muamalat
                     imgBankLogo.setImageResource(R.drawable.muamalat);
                     break;
                case "BOCM01": //Bank of china
                     imgBankLogo.setImageResource(R.drawable.bankofchina);
                     break;
                case "MB2U0227": // Maybank2U
                case "MBB0228":  // Maybank2E
                     imgBankLogo.setImageResource(R.drawable.maybank);
                    break;
                case "BKRM0602": // Bank Rakyat
                     imgBankLogo.setImageResource(R.drawable.bankrakyat);
                    break;
                case "BCBB0235": // CIMB Clicks
                     imgBankLogo.setImageResource(R.drawable.cimb);
                    break;
                case "PBB0233": // Public Bank
                     imgBankLogo.setImageResource(R.drawable.publicbank);
                    break;
                case "RHB0218": // RHB Bank
                     imgBankLogo.setImageResource(R.drawable.rhb);
                    break;
                case "HLB0224": // Hong Leong Bank
                     imgBankLogo.setImageResource(R.drawable.hongleong);
                    break;
                case "AMBB0209": // AmBank
                     imgBankLogo.setImageResource(R.drawable.ambank);
                    break;
                case "ABB0233": // Affin Bank
                     imgBankLogo.setImageResource(R.drawable.affin);
                    break;
                case "ABMB0212": // Alliance Bank
                     imgBankLogo.setImageResource(R.drawable.alliance);
                    break;
                case "BSN0601": // BSN
                     imgBankLogo.setImageResource(R.drawable.bsn);
                    break;
                case "BIMB0340": // Bank Islam
                     imgBankLogo.setImageResource(R.drawable.bimb);
                    break;
                case "HSBC0223": // HSBC Bank
                     imgBankLogo.setImageResource(R.drawable.hsbc);
                    break;
                case "SCB0216": // Standard Chartered
                     imgBankLogo.setImageResource(R.drawable.stdchartered);
                    break;
                case "UOB0226": // UOB Bank
                     imgBankLogo.setImageResource(R.drawable.uob);
                    break;
                case "OCBC0229": // OCBC Bank
                     imgBankLogo.setImageResource(R.drawable.ocbc);
                    break;
                case "KFH0346": // OCBC Bank
                     imgBankLogo.setImageResource(R.drawable.kuwait);
                    break;
                default:
                    imgBankLogo.setImageResource(R.drawable.ic_bank_default);
                    break;
            }
        }
    }
}