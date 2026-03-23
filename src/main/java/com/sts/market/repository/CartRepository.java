package com.sts.market.repository;

import com.sts.shared.model.CartEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CartRepository {
    private final String cartFilePath;

    public CartRepository(String cartFilePath) {
        this.cartFilePath = cartFilePath;
    }

    public List<CartEntry> findAll() throws IOException {
        List<CartEntry> cartEntries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(cartFilePath))) {
            reader.readLine();
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                if (csvLine.isBlank()) continue;

                String[] columns = csvLine.split(",");
                cartEntries.add(new CartEntry(
                        columns[0],
                        columns[1],
                        Integer.parseInt(columns[2])
                ));
            }
        }
        return cartEntries;
    }

    public void saveAll(List<CartEntry> cartEntries) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cartFilePath))) {
            writer.write("userId,productId,quantity\n");
            for (CartEntry cartEntry : cartEntries) {
                writer.write(String.join(",",
                        cartEntry.getUserId(),
                        cartEntry.getProductId(),
                        String.valueOf(cartEntry.getQuantity())
                ) + "\n");
            }
        }
    }

    public void addOrUpdate(String userId, String productId, int quantityToAdd) throws IOException {
        List<CartEntry> cartEntries = findAll();
        boolean existingEntryFound = false;

        for (CartEntry cartEntry : cartEntries) {
            if (cartEntry.getUserId().equals(userId) && cartEntry.getProductId().equals(productId)) {
                cartEntry.setQuantity(cartEntry.getQuantity() + quantityToAdd);
                existingEntryFound = true;
                break;
            }
        }

        if (!existingEntryFound) {
            cartEntries.add(new CartEntry(userId, productId, quantityToAdd));
        }

        saveAll(cartEntries);
    }

    public void remove(String userId, String productId, int quantityToRemove) throws IOException {
        List<CartEntry> cartEntries = findAll();
        CartEntry entryToDelete = null;

        for (CartEntry cartEntry : cartEntries) {
            if (cartEntry.getUserId().equals(userId) && cartEntry.getProductId().equals(productId)) {
                int remainingQuantity = cartEntry.getQuantity() - quantityToRemove;
                if (remainingQuantity <= 0) {
                    entryToDelete = cartEntry;
                } else {
                    cartEntry.setQuantity(remainingQuantity);
                }
                break;
            }
        }

        if (entryToDelete != null) {
            cartEntries.remove(entryToDelete);
        }

        saveAll(cartEntries);
    }
}
