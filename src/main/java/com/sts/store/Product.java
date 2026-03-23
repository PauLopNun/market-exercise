package com.sts.store;

public class Product {
    private int id;
    private String name;
    private double price;
    private int currentStock;
    private int maxCapacity;

    public Product(String[] partsProduct) {
        this.id = Integer.parseInt(partsProduct[0]);
        this.name = partsProduct[1];
        this.price = Double.parseDouble(partsProduct[2]);
        this.currentStock = Integer.parseInt(partsProduct[3]);
        this.maxCapacity = Integer.parseInt(partsProduct[4]);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public String toString() {
        return String.format("%d;%s;%.2f;%d;%d", id, name, price, currentStock, maxCapacity);
    }
}
