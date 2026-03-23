package com.sts.market.service;

import com.sts.market.repository.CartRepository;
import com.sts.market.repository.MarketStockRepository;
import com.sts.shared.model.CartEntry;
import com.sts.shared.model.Product;

import java.io.IOException;
import java.util.List;

public class MarketService {
    private final MarketStockRepository stockRepo;
    private final CartRepository cartRepo;

    public MarketService(MarketStockRepository stockRepo, CartRepository cartRepo) {
        this.stockRepo = stockRepo;
        this.cartRepo = cartRepo;
    }

    public boolean buy(String userId, String productId, int qty) throws IOException {
        List<Product> products = stockRepo.findAll();
        Product product = findById(products, productId);

        if (product == null || product.getCurrentStock() < qty) {
            return false;
        }

        product.setCurrentStock(product.getCurrentStock() - qty);
        stockRepo.saveAll(products);
        cartRepo.addOrUpdate(userId, productId, qty);
        return true;
    }

    public void drop(String userId, String productId, int qty) throws IOException {
        List<CartEntry> entries = cartRepo.findAll();
        int actualQty = 0;

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
    }

    public void restock(String productId, int qty) throws IOException {
        List<Product> products = stockRepo.findAll();
        Product product = findById(products, productId);

        if (product != null) {
            product.setCurrentStock(product.getCurrentStock() + qty);
            stockRepo.saveAll(products);
        }
    }

    private Product findById(List<Product> products, String productId) {
        return products.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
    }
}
