package com.assignment3;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Stock implements Serializable,Comparable<Stock>{


    private String Stock_Symbol;
    private String Company_name;
    private double Price;
    private double Price_Change;
    private double Change_Percentage;


    public Stock(String a, String b, double p, double pc, double cp) {
        this.Stock_Symbol = a;
        this.Company_name = b;
        this.Price = p;
        this.Price_Change = pc;
        this.Change_Percentage = cp;


    }

    public String getStock_Symbol() {
        return Stock_Symbol;
    }

    public String getCompany_name() {
        return Company_name;
    }

    public double getPrice() {
        return Price;
    }

    public double getPrice_Change() {
        return Price_Change;
    }

    public double getChange_Percentage() {
        return Change_Percentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return Company_name.equals(stock.Company_name) &&
                Stock_Symbol.equals(stock.Stock_Symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Company_name, Stock_Symbol);
    }

    @Override
    public int compareTo(Stock stock) {
        return Stock_Symbol.compareTo(stock.getStock_Symbol());
    }
}
