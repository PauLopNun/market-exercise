package com.sts.market.service;

import com.sts.audit.EventType;
import com.sts.market.repository.CartRepository;
import com.sts.market.repository.MarketStockRepository;
import com.sts.shared.audit.AuditLogger;
import com.sts.shared.model.CartEntry;
import com.sts.shared.model.Product;

import java.io.IOException;
import java.util.List;

public class MarketService {
    private final MarketStockRepository stockRepo;
    private final CartRepository cartRepo;
    private final AuditLogger auditLogger;

    public MarketService(MarketStockRepository stockRepo, CartRepository cartRepo, AuditLogger auditLogger) {
        this.stockRepo = stockRepo;
        this.cartRepo = cartRepo;
        this.auditLogger = auditLogger;
    }

    public boolean buy(String userId, String productId, int qty) throws IOException {
        List<Product> products = stockRepo.findAll();
        Product product = findById(products, productId);

        // Caso: El producto no existe o no hay stock suficiente
        if (product == null || product.getCurrentStock() < qty) {
            String stockInfo = (product == null) ? "Product not found" : "Stock: " + product.getCurrentStock();
            auditLogger.log("MARKET", EventType.ITEM_PURCHASED, "FAILED",
                    "User: " + userId + " | ProductId: " + productId + " | Requested: " + qty + " | Error: " + stockInfo);
            return false;
        }

        // Caso: Éxito
        product.setCurrentStock(product.getCurrentStock() - qty);
        stockRepo.saveAll(products);
        cartRepo.addOrUpdate(userId, productId, qty);

        double totalAmount = product.getPrice() * qty;
        auditLogger.log("MARKET", EventType.ITEM_PURCHASED, "SUCCESS",
                "User: " + userId + " | Product: " + product.getName() + " | Qty: " + qty + " | Total: " + totalAmount + "€");

        return true;
    }

    public void drop(String userId, String productId, int qty) throws IOException {
        List<CartEntry> entries = cartRepo.findAll();
        int actualQty = 0;

        // Buscamos cuánto tiene realmente en el carrito para no devolver de más al stock
        for (CartEntry e : entries) {
            if (e.getUserId().equals(userId) && e.getProductId().equals(productId)) {
                actualQty = Math.min(e.getQuantity(), qty);
                break;
            }
        }

        if (actualQty == 0) return;

        List<Product> products = stockRepo.findAll();
        Product product = findById(products, productId);
        if (product != null) {
            product.setCurrentStock(product.getCurrentStock() + actualQty);
            stockRepo.saveAll(products);
        }
        cartRepo.remove(userId, productId, actualQty);

        auditLogger.log("MARKET", EventType.ITEM_DROPPED, "SUCCESS",
                "User: " + userId + " | ProductId: " + productId + " | QtyReturned: " + actualQty);
    }

    public void restock(String productId, int qty) throws IOException {
        List<Product> products = stockRepo.findAll();
        Product product = findById(products, productId);

        if (product != null) {
            product.setCurrentStock(product.getCurrentStock() + qty);
            stockRepo.saveAll(products);
        }

        auditLogger.log("MARKET", EventType.MARKET_REFILLED, "SUCCESS",
                "Restock Event | ProductId: " + productId + " | Qty: " + qty);
    }

    private Product findById(List<Product> products, String productId) {
        return products.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
    }
}