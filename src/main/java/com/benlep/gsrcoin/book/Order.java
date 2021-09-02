package com.benlep.gsrcoin.book;

import java.util.Comparator;

public class Order {
    private final boolean buy;
    private final double price;
    private double quantity;

    public Order(boolean buy, double price, double quantity) {
        this.buy = buy;
        this.price = price;
        this.quantity = quantity;
    }

    public boolean isBuy() {
        return buy;
    }

    public double getPrice() {
        return price;
    }

    public double getQuantity() {
        return quantity;
    }

    public double addQuantity(double add) {
        quantity += add;
        return quantity;
    }

}
