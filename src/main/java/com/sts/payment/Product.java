package com.sts.payment;

public class Product {
    private String productId;
    private String name;
    private double price;
    private int currentStock;
    private int maxCapacity;

    public Product(String productId, String name, double price, int currentStock, int maxCapacity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.currentStock = currentStock;
        this.maxCapacity = maxCapacity;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getCurrentStock() { return currentStock; }
    public int getMaxCapacity() { return maxCapacity; }

    public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }
}
