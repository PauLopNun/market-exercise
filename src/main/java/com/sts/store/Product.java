package com.sts.store;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Product {
    private int id;
    private String name;
    private double price;
    private int currentStock;
    private int maxCapacity;

    @Override
    public String toString() {
        return String.format("%d,%s,%.2f,%d,%d", id, name, price, currentStock, maxCapacity);
    }
}
