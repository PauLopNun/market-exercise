package com.sts.market.repository;

import com.sts.shared.model.CartEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CartRepository {
    private final String filePath;

    public CartRepository(String filePath) {
        this.filePath = filePath;
    }

    public List<CartEntry> findAll() throws IOException {
        List<CartEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                entries.add(new CartEntry(p[0], p[1], Integer.parseInt(p[2])));
            }
        }
        return entries;
    }

    public void saveAll(List<CartEntry> entries) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("userId,productId,quantity\n");
            for (CartEntry e : entries) {
                writer.write(String.join(",",
                        e.getUserId(),
                        e.getProductId(),
                        String.valueOf(e.getQuantity())
                ) + "\n");
            }
        }
    }

    public void addOrUpdate(String userId, String productId, int qty) throws IOException {
        List<CartEntry> entries = findAll();
        boolean found = false;
        for (CartEntry e : entries) {
            if (e.getUserId().equals(userId) && e.getProductId().equals(productId)) {
                e.setQuantity(e.getQuantity() + qty);
                found = true;
                break;
            }
        }
        if (!found) {
            entries.add(new CartEntry(userId, productId, qty));
        }
        saveAll(entries);
    }

    public void remove(String userId, String productId, int qty) throws IOException {
        List<CartEntry> entries = findAll();
        CartEntry toRemove = null;

        for (CartEntry e : entries) {
            if (e.getUserId().equals(userId) && e.getProductId().equals(productId)) {
                int remaining = e.getQuantity() - qty;
                if (remaining <= 0) {
                    toRemove = e;
                } else {
                    e.setQuantity(remaining);
                }
                break;
            }
        }

        if (toRemove != null) {
            entries.remove(toRemove);
        }
        saveAll(entries);
    }
}
