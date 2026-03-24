package com.sts.store;

import com.sts.shared.model.Product;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StoreService {

    private final String marketStockPath;
    private final String warehousePath;

    public StoreService(String marketStockPath, String warehousePath) {
        this.marketStockPath = marketStockPath;
        this.warehousePath = warehousePath;
    }

    public void refillProducts() throws IOException {
        List<Product> marketProducts = readMarketStock();
        List<WarehouseProduct> warehouseProducts = readWarehouse();

        boolean modified = replenishLowStockProducts(marketProducts, warehouseProducts);

        if (modified) {
            writeMarketStock(marketProducts);
            writeWarehouse(warehouseProducts);
        }
    }

    public boolean replenishLowStockProducts(List<Product> marketProducts, List<WarehouseProduct> warehouseProducts) {
        boolean modified = false;
        for (Product market : marketProducts) {
            double ratio = (double) market.getCurrentStock() / market.getMaxCapacity();
            if (ratio < 0.2) {
                for (WarehouseProduct warehouse : warehouseProducts) {
                    if (market.getProductId().equals(warehouse.getProductId())) {
                        int needed = market.getMaxCapacity() - market.getCurrentStock();
                        int toTransfer = Math.min(needed, warehouse.getTotalStock());
                        if (toTransfer > 0) {
                            market.setCurrentStock(market.getCurrentStock() + toTransfer);
                            warehouse.setTotalStock(warehouse.getTotalStock() - toTransfer);
                            modified = true;
                        }
                        break;
                    }
                }
            }
        }
        return modified;
    }

    public List<Product> readMarketStock() throws IOException {
        List<Product> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(marketStockPath))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] csvFields = line.split(",");
                products.add(new Product(csvFields[0], csvFields[1],
                        Double.parseDouble(csvFields[2]),
                        Integer.parseInt(csvFields[3]),
                        Integer.parseInt(csvFields[4])));
            }
        }
        return products;
    }

    public List<WarehouseProduct> readWarehouse() throws IOException {
        List<WarehouseProduct> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(warehousePath))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] csvFields = line.split(",");
                products.add(new WarehouseProduct(csvFields[0], csvFields[1], Integer.parseInt(csvFields[2])));
            }
        }
        return products;
    }

    public void writeMarketStock(List<Product> products) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(marketStockPath))) {
            writer.write("productId,name,price,current_stock,max_capacity\n");
            for (Product p : products) {
                writer.write(String.join(",",
                        p.getProductId(), p.getName(),
                        String.valueOf(p.getPrice()),
                        String.valueOf(p.getCurrentStock()),
                        String.valueOf(p.getMaxCapacity())) + "\n");
            }
        }
    }

    public void writeWarehouse(List<WarehouseProduct> products) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(warehousePath))) {
            writer.write("productId,name,total_stock\n");
            for (WarehouseProduct p : products) {
                writer.write(String.join(",",
                        p.getProductId(), p.getName(),
                        String.valueOf(p.getTotalStock())) + "\n");
            }
        }
    }
}
