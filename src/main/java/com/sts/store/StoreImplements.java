package com.sts.store;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StoreImplements implements Store {

    @Override
    public void refillProducts() {
        final List<Product> marketProducts = getProductsFromCSV();
        final List<WarehouseProduct> warehouseProducts = getWarehouseProductsFromCSV();

        final boolean modified = replenishLowStockProducts(marketProducts, warehouseProducts);

        if (modified) {
            writeMarketCSV(marketProducts);
            writeWarehouseCSV(warehouseProducts);
        }
    }

    public boolean replenishLowStockProducts(List<Product> marketProducts, List<WarehouseProduct> warehouseProducts) {
        boolean modified = false;
        for (final Product marketItem : marketProducts) {
            final double productPercentageToRefill = (double) marketItem.getCurrentStock() / marketItem.getMaxCapacity();
            double PERCENTAGE_REFILL = 0.2;
            if (productPercentageToRefill < PERCENTAGE_REFILL) {
                for (final WarehouseProduct warehouseItem : warehouseProducts) {
                    if (marketItem.getId() == warehouseItem.getId()) {
                        final int needed = marketItem.getMaxCapacity() - marketItem.getCurrentStock();
                        final int toTransfer = Math.min(needed, warehouseItem.getTotalStock());

                        if (toTransfer > 0) {
                            marketItem.setCurrentStock(marketItem.getCurrentStock() + toTransfer);
                            warehouseItem.setTotalStock(warehouseItem.getTotalStock() - toTransfer);
                            modified = true;
                        }
                    }
                }
            }
        }

        return modified;
    }

    public List<Product> getProductsFromCSV() {
        final List<Product> productList = new ArrayList<>();
        try (final BufferedReader reader = new BufferedReader(new FileReader("data/market_stock.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String[] parts = line.split(",");
                // Price format is like "1.500000" (dot as thousands separator, no decimals)
                // or "1.000000" — remove dots used as thousands separators and parse as integer/double
                String priceString = parts[2].replace(".", "");
                double price = Double.parseDouble(priceString);
                productList.add(Product.builder()
                        .id(Integer.parseInt(parts[0]))
                        .name(parts[1])
                        .price(price)
                        .currentStock(Integer.parseInt(parts[3]))
                        .maxCapacity(Integer.parseInt(parts[4]))
                        .build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return productList;
    }

    public List<WarehouseProduct> getWarehouseProductsFromCSV() {
        final List<WarehouseProduct> productList = new ArrayList<>();
        try (final BufferedReader reader = new BufferedReader(new FileReader("data/warehouse.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String[] parts = line.split(",");
                productList.add(WarehouseProduct.builder()
                        .id(Integer.parseInt(parts[0]))
                        .name(parts[1])
                        .totalStock(Integer.parseInt(parts[2]))
                        .build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return productList;
    }

    public void writeWarehouseCSV(List<WarehouseProduct> warehouseProductList) {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter("data/warehouse.csv"))) {
            for (final WarehouseProduct warehouseProduct : warehouseProductList) {
                writer.write(warehouseProduct.toString());
                writer.newLine();
                writer.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void writeMarketCSV(List<Product> productList) {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter("data/market_stock.csv"))) {
            for (final Product product : productList) {
                writer.write(product.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}