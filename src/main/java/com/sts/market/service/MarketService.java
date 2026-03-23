package com.sts.market.service;

import com.sts.market.repository.CartRepository;
import com.sts.market.repository.MarketStockRepository;
import com.sts.shared.model.CartEntry;
import com.sts.shared.model.Product;

import java.io.IOException;
import java.util.List;

public class MarketService {
    private final MarketStockRepository stockRepository;
    private final CartRepository cartRepository;

    public MarketService(MarketStockRepository stockRepository, CartRepository cartRepository) {
        this.stockRepository = stockRepository;
        this.cartRepository = cartRepository;
    }

    public boolean buy(String userId, String productId, int quantityToBuy) throws IOException {
        List<Product> products = stockRepository.findAll();
        Product product = findProductById(products, productId);

        if (product == null || product.getCurrentStock() < quantityToBuy) {
            return false;
        }

        product.setCurrentStock(product.getCurrentStock() - quantityToBuy);
        stockRepository.saveAll(products);
        cartRepository.addOrUpdate(userId, productId, quantityToBuy);
        return true;
    }

    public void drop(String userId, String productId, int quantityToDrop) throws IOException {
        List<CartEntry> cartEntries = cartRepository.findAll();
        int quantityToRestore = 0;

        for (CartEntry cartEntry : cartEntries) {
            if (cartEntry.getUserId().equals(userId) && cartEntry.getProductId().equals(productId)) {
                quantityToRestore = Math.min(cartEntry.getQuantity(), quantityToDrop);
                break;
            }
        }

        if (quantityToRestore == 0) return;

        List<Product> products = stockRepository.findAll();
        Product product = findProductById(products, productId);

        if (product != null) {
            product.setCurrentStock(product.getCurrentStock() + quantityToRestore);
            stockRepository.saveAll(products);
        }

        cartRepository.remove(userId, productId, quantityToRestore);
    }

    public void restock(String productId, int quantityToAdd) throws IOException {
        List<Product> products = stockRepository.findAll();
        Product product = findProductById(products, productId);

        if (product != null) {
            product.setCurrentStock(product.getCurrentStock() + quantityToAdd);
            stockRepository.saveAll(products);
        }
    }

    private Product findProductById(List<Product> products, String productId) {
        return products.stream()
                .filter(product -> product.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
    }
}
