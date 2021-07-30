package com.assignment3;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class viewHolder extends RecyclerView.ViewHolder {


    TextView Stock_Symbol, Company_name,Price,Price_Change,Change_Percentage;

    public viewHolder(@NonNull View itemView) {
        super(itemView);

        Stock_Symbol=itemView.findViewById(R.id.stksymbol);
        Company_name=itemView.findViewById(R.id.companynm);
        Price = itemView.findViewById(R.id.pric);
        Price_Change = itemView.findViewById(R.id.priccng);
        Change_Percentage = itemView.findViewById(R.id.cngpercentage);

    }
}
