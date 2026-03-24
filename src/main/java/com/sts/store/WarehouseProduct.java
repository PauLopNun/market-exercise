package com.sts.store;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseProduct {
    private String productId;
    private String name;
    private int totalStock;

    public WarehouseProduct(String productId, String name, int totalStock) {
        this.productId = productId;
        this.name = name;
        this.totalStock = totalStock;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%d", productId, name, totalStock);
    }
}
