package com.sts.store;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class WarehouseProduct {
    private int id;
    private String name;
    private int totalStock;

    @Override
    public String toString() {
        return String.format("%d,%s,%d", id, name, totalStock);
    }
}
