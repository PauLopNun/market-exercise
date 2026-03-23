package com.sts.market.repository;

import com.sts.shared.model.Product;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MarketStockRepository {
    private final String filePath;

    public MarketStockRepository(String filePath) {
        this.filePath = filePath;
    }

    public List<Product> findAll() throws IOException {
        List<Product> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                products.add(new Product(
                        p[0], p[1],
                        Double.parseDouble(p[2]),
                        Integer.parseInt(p[3]),
                        Integer.parseInt(p[4])
                ));
            }
        }
        return products;
    }

    public void saveAll(List<Product> products) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("productId,name,price,current_stock,max_capacity\n");
            for (Product p : products) {
                writer.write(String.join(",",
                        p.getProductId(),
                        p.getName(),
                        String.valueOf(p.getPrice()),
                        String.valueOf(p.getCurrentStock()),
                        String.valueOf(p.getMaxCapacity())
                ) + "\n");
            }
        }
    }
}
