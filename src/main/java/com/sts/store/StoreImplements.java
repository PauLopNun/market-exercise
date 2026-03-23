package com.sts.store;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StoreImplements implements Store {

    @Override
    public void refillProducts() {
        List<Product> marketProducts = getProductsFromCSV();
        List<WarehouseProduct> warehouseProducts = getWarehouseProductsFromCSV();
        boolean modified = replenishLowStockProducts(marketProducts, warehouseProducts);
        if (modified) {
            writeMarketCSV(marketProducts);
            writeWarehouseCSV(warehouseProducts);
        }
    }

    private boolean replenishLowStockProducts(List<Product> marketProducts, List<WarehouseProduct> warehouseProducts) {
        for (Product marketItem : marketProducts) {
            if ((double) marketItem.getCurrentStock() / marketItem.getMaxCapacity() < 0.2) {
                for (WarehouseProduct whItem : warehouseProducts) {
                    if (marketItem.getId() == whItem.getId()) {
                        int needed = marketItem.getMaxCapacity() - marketItem.getCurrentStock();
                        int toTransfer = Math.min(needed, whItem.getTotalStock());

                        if (toTransfer > 0) {
                            marketItem.setCurrentStock(marketItem.getCurrentStock() + toTransfer);
                            whItem.setTotalStock(whItem.getTotalStock() - toTransfer);
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private List<Product> getProductsFromCSV() {
        List<Product> productList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("data/market_stock.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                productList.add(new Product(parts));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return productList;
    }

    private List<WarehouseProduct> getWarehouseProductsFromCSV() {
        List<WarehouseProduct> productList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("data/warehouse.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                productList.add(new WarehouseProduct(parts));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return productList;
    }

    private void writeWarehouseCSV(List<WarehouseProduct> warehouseProductList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/warehouse.csv"))) {
            for (WarehouseProduct warehouseProduct : warehouseProductList) {
                writer.write(warehouseProduct.toString());
                writer.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeMarketCSV(List<Product> productList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/market_stock.csv"))) {
            for (Product product : productList) {
                writer.write(product.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
