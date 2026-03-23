package com.sts.store;

public class WarehouseProduct {
    private int id;
    private String name;
    private int totalStock;

    public WarehouseProduct(String[] warehouseProductsParts) {
        this.id = Integer.parseInt(warehouseProductsParts[0]);
        this.name = warehouseProductsParts[1];
        this.totalStock = Integer.parseInt(warehouseProductsParts[2]);
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

    public int getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(int totalStock) {
        this.totalStock = totalStock;
    }

    @Override
    public String toString() {
        return String.format("%d;%s;%d", id, name, totalStock);
    }
}
