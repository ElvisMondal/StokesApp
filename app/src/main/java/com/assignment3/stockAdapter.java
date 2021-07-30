package com.assignment3;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class stockAdapter extends RecyclerView.Adapter<viewHolder> {

    private List<Stock> stockList;
    private MainActivity mainActiv;

    stockAdapter(List<Stock> stList, MainActivity mn) {
        this.stockList = stList;
        this.mainActiv = mn;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_view, parent, false);

        itemView.setOnClickListener(mainActiv);
        itemView.setOnLongClickListener(mainActiv);

        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        final Stock edit = stockList.get(position);
        if (edit.getPrice_Change() >0.0) {

            holder.Stock_Symbol.setTextColor(Color.GREEN);
            holder.Company_name.setTextColor(Color.GREEN);
            holder.Price.setTextColor(Color.GREEN);
            holder.Price_Change.setTextColor(Color.GREEN);
            holder.Change_Percentage.setTextColor(Color.GREEN);

            holder.Stock_Symbol.setText(edit.getStock_Symbol());
            holder.Company_name.setText(edit.getCompany_name());
            holder.Price.setText(String.format(Locale.getDefault(), "%.2f", edit.getPrice()));
            holder.Price_Change.setText(mainActiv.getString(R.string.arrow, "▲", String.format(Locale.getDefault(), "%.2f", edit.getPrice_Change())));
            holder.Change_Percentage.setText(mainActiv.getString(R.string.change_percentage, "(", String.format(Locale.getDefault(), "%.2f", edit.getChange_Percentage()), "%", ")"));


        }

        else if(edit.getPrice_Change() == 0.0){

            holder.Stock_Symbol.setTextColor(Color.WHITE);
            holder.Company_name.setTextColor(Color.WHITE);
            holder.Price.setTextColor(Color.WHITE);
            holder.Price_Change.setTextColor(Color.WHITE);
            holder.Change_Percentage.setTextColor(Color.WHITE);

            holder.Stock_Symbol.setText(edit.getStock_Symbol());
            holder.Company_name.setText(edit.getCompany_name());
            holder.Price.setText(String.format(Locale.getDefault(), "%.2f", edit.getPrice()));
            holder.Price_Change.setText(String.format(Locale.getDefault(), "%.2f", edit.getPrice_Change()));
            holder.Change_Percentage.setText(mainActiv.getString(R.string.change_percentage, "(", String.format(Locale.getDefault(), "%.2f", edit.getChange_Percentage()), "%", ")"));

        }

        else {
            holder.Stock_Symbol.setTextColor(Color.RED);
            holder.Company_name.setTextColor(Color.RED);
            holder.Price.setTextColor(Color.RED);
            holder.Price_Change.setTextColor(Color.RED);
            holder.Change_Percentage.setTextColor(Color.RED);

            holder.Stock_Symbol.setText(edit.getStock_Symbol());
            holder.Company_name.setText(edit.getCompany_name());
            holder.Price.setText(String.format(Locale.getDefault(), "%.2f", edit.getPrice()));
            holder.Price_Change.setText(mainActiv.getString(R.string.arrow, "▼", String.format(Locale.getDefault(), "%.2f", edit.getPrice_Change())));
            holder.Change_Percentage.setText(mainActiv.getString(R.string.change_percentage, "(", String.format(Locale.getDefault(), "%.2f", edit.getChange_Percentage()), "%", ")"));

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://www.marketwatch.com/investing/stock/" + edit.getStock_Symbol()));
                mainActiv.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
