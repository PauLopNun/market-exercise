package com.sts.market.repository;

import com.sts.shared.model.Product;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MarketStockRepository {
    private final String stockFilePath;

    public MarketStockRepository(String stockFilePath) {
        this.stockFilePath = stockFilePath;
    }

    public List<Product> findAll() throws IOException {
        List<Product> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(stockFilePath))) {
            reader.readLine(); // skip header
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                if (csvLine.isBlank()) continue;

                String[] columns = csvLine.split(",");
                products.add(new Product(
                        columns[0],
                        columns[1],
                        Double.parseDouble(columns[2]),
                        Integer.parseInt(columns[3]),
                        Integer.parseInt(columns[4])
                ));
            }
        }
        return products;
    }

    public void saveAll(List<Product> products) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stockFilePath))) {
            writer.write("productId,name,price,current_stock,max_capacity\n");
            for (Product product : products) {
                writer.write(String.join(",",
                        product.getProductId(),
                        product.getName(),
                        String.valueOf(product.getPrice()),
                        String.valueOf(product.getCurrentStock()),
                        String.valueOf(product.getMaxCapacity())
                ) + "\n");
            }
        }
    }
}
