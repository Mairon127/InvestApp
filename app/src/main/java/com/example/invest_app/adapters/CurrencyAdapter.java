package com.example.invest_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.invest_app.R;
import com.example.invest_app.model.CurrencyModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyViewholder> {
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    private ArrayList<CurrencyModel> currencyModels;
    private Context context;

    public CurrencyAdapter(ArrayList<CurrencyModel> currencyModals, Context context) {
        this.currencyModels = currencyModals;
        this.context = context;
    }
    public void filterList(ArrayList<CurrencyModel> filterlist) {
        currencyModels = filterlist;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CurrencyAdapter.CurrencyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.currency_item, parent, false);
        return new CurrencyAdapter.CurrencyViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyAdapter.CurrencyViewholder holder, int position) {
        CurrencyModel modal = currencyModels.get(position);
        holder.name.setText(modal.getName());
        holder.rate.setText("$ " + df2.format(modal.getPrice()));
        holder.symbol.setText(modal.getSymbol());
    }

    @Override
    public int getItemCount() {
        return currencyModels.size();
    }
    public class CurrencyViewholder extends RecyclerView.ViewHolder {
        private TextView symbol, rate, name;

        public CurrencyViewholder(@NonNull View itemView) {
            super(itemView);
            symbol = itemView.findViewById(R.id.Symbol);
            rate = itemView.findViewById(R.id.Rate);
            name = itemView.findViewById(R.id.Name);
        }
    }
}