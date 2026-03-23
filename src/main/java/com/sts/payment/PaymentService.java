package com.sts.payment;

import com.sts.audit.EventType;
import com.sts.market.service.MarketService;
import com.sts.shared.audit.AuditLogger;
import com.sts.shared.model.CartEntry;
import com.sts.shared.model.Product;
import com.sts.shared.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentService {

    private final String usersPath;
    private final String cartPath;
    private final String marketStockPath;
    private final MarketService marketService;
    private final AuditLogger auditLogger;

    public PaymentService(String usersPath, String cartPath, String marketStockPath,
                          MarketService marketService, AuditLogger auditLogger) {
        this.usersPath = usersPath;
        this.cartPath = cartPath;
        this.marketStockPath = marketStockPath;
        this.marketService = marketService;
        this.auditLogger = auditLogger;
    }

    public boolean checkout(String userId) throws IOException {
        List<CartEntry> cart = readCart(userId);
        List<Product> stock = readStock();
        double total = calculateTotal(cart, stock);

        User user = findUser(userId);
        if (user == null) {
            auditLogger.log("PAYMENT", EventType.ITEM_PURCHASED, "FAILED",
                    "User: " + userId + " | Error: User not found");
            return false;
        }

        // LIFO: remove items until total fits budget
        int removedCount = 0;
        while (total > user.getBudget() && !cart.isEmpty()) {
            CartEntry last = cart.remove(cart.size() - 1);
            removedCount++;
            marketService.restock(last.getProductId(), last.getQuantity());
            auditLogger.log("PAYMENT", EventType.ITEM_DROPPED, "FAILED",
                    "User: " + userId + " | ProductId: " + last.getProductId() + 
                    " | RemovedCount: " + removedCount + " | Reason: insufficient funds");
            total = calculateTotal(cart, stock);
        }


        user.setBudget(user.getBudget() - total);
        saveUser(user);
        clearCart(userId);

        auditLogger.log("PAYMENT", EventType.ITEM_PURCHASED, "SUCCESS",
                "User: " + userId + " | Total: " + total + "€ | Remaining: " + user.getBudget() + "€");
        return true;
    }

    public double calculateTotal(List<CartEntry> cart, List<Product> stock) {
        double total = 0;
        for (CartEntry entry : cart) {
            for (Product product : stock) {
                if (product.getProductId().equals(entry.getProductId())) {
                    total += product.getPrice() * entry.getQuantity();
                    break;
                }
            }
        }
        return total;
    }

    public List<CartEntry> readCart(String userId) throws IOException {
        List<CartEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(cartPath))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] csvFields = line.split(",");
                if (csvFields[0].equals(userId)) {
                    entries.add(new CartEntry(csvFields[0], csvFields[1], Integer.parseInt(csvFields[2])));
                }
            }
        }
        return entries;
    }

    public List<Product> readStock() throws IOException {
        List<Product> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(marketStockPath))) {
            reader.readLine();
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

    public User findUser(String userId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(usersPath))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] csvFields = line.split(",");
                if (csvFields[0].equals(userId) || csvFields[1].equals(userId)) {
                    return new User(csvFields[0], csvFields[1], Double.parseDouble(csvFields[2]));
                }
            }
        }
        return null;
    }

    public void saveUser(User user) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(usersPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersPath))) {
            writer.write(lines.get(0) + "\n");
            for (int lineIndex = 1; lineIndex < lines.size(); lineIndex++) {
                String[] csvFields = lines.get(lineIndex).split(",");
                if (csvFields[0].equals(user.getId())) {
                    writer.write(String.join(",", csvFields[0], csvFields[1], String.valueOf(user.getBudget())) + "\n");
                } else {
                    writer.write(lines.get(lineIndex) + "\n");
                }
            }
        }
    }

    public void clearCart(String userId) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(cartPath))) {
            String header = reader.readLine();
            lines.add(header);
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] csvFields = line.split(",");
                if (!csvFields[0].equals(userId)) lines.add(line);
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cartPath))) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        }
    }
}
