package com.sts.shared.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class Product {
    private String productId;
    private String name;
    private double price;
    private int currentStock;
    private int maxCapacity;

}