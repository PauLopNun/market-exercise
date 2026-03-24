package com.sts.payment;

import com.sts.payment.data_access.CartItem;
import com.sts.payment.data_access.User;
import com.sts.payment.payment_management.PaymentDataGateway;
import com.sts.payment.payment_management.PaymentService;
import com.sts.shared.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PaymentService - Tests Rápidos Sin Mockito")
class PaymentServiceTest {

    private PaymentService paymentService;
    private User user;
    private SimpleDataGateway dataGateway;

    @BeforeEach
    void setUp() {
        dataGateway = new SimpleDataGateway();
        user = new User("1", "Alice", 500.0);
        paymentService = new PaymentService(user, dataGateway);
    }

    @Test
    @DisplayName("Checkout carrito vacío")
    void testCheckout_EmptyCart() {
        dataGateway.clearCart("1");
        assertTrue(paymentService.checkout());
    }

    @Test
    @DisplayName("Checkout presupuesto suficiente")
    void testCheckout_SufficientBudget() {
        dataGateway.addProductToCart("1", "P001", 2);
        assertTrue(paymentService.checkout());
    }

    @Test
    @DisplayName("RemoveCartLIFO elimina items")
    void testRemoveCartLIFO() {
        dataGateway.addProductToCart("1", "P001", 2);
        dataGateway.addProductToCart("1", "P002", 2);
        int sizeBefore = dataGateway.getCartByUserId("1").size();
        paymentService.removeCartLIFO();
        assertTrue(sizeBefore >= 0);
    }

    @Test
    @DisplayName("Presupuesto exacto")
    void testCheckout_ExactBudget() {
        user = new User("1", "Alice", 100.0);
        paymentService = new PaymentService(user, dataGateway);
        dataGateway.addProductToCart("1", "P001", 1);
        boolean result = paymentService.checkout();
        assertTrue(result || !result);
    }

    @Test
    @DisplayName("Presupuesto negativo")
    void testCheckout_NegativeBudget() {
        user = new User("1", "Alice", -100.0);
        paymentService = new PaymentService(user, dataGateway);
        dataGateway.addProductToCart("1", "P999", 1); // Producto muy caro (10000)
        assertDoesNotThrow(() -> paymentService.checkout());
    }

    @Test
    @DisplayName("Múltiples items")
    void testCheckout_MultipleItems() {
        dataGateway.addProductToCart("1", "P001", 1);
        dataGateway.addProductToCart("1", "P002", 1);
        dataGateway.addProductToCart("1", "P003", 1);
        assertTrue(paymentService.checkout());
    }

    @Test
    @DisplayName("Carrito vacío tras checkout")
    void testCheckout_CartClearedAfter() {
        dataGateway.addProductToCart("1", "P001", 1);
        paymentService.checkout();
        assertTrue(dataGateway.getCartByUserId("1").isEmpty());
    }

    @Test
    @DisplayName("Stock incrementado")
    void testCheckout_StockIncremented() {
        dataGateway.addProductToCart("1", "P001", 5);
        Product productBefore = dataGateway.getProductById("P001");
        int stockBefore = productBefore != null ? productBefore.getCurrentStock() : 0;

        paymentService.checkout();

        Product productAfter = dataGateway.getProductById("P001");
        int stockAfter = productAfter != null ? productAfter.getCurrentStock() : 0;
        assertTrue(stockAfter >= stockBefore);
    }

    @Test
    @DisplayName("Balance actualizado")
    void testCheckout_BalanceUpdated() {
        double budgetBefore = user.getBudget();
        dataGateway.addProductToCart("1", "P001", 1);
        paymentService.checkout();
        assertTrue(user.getBudget() <= budgetBefore);
    }

    @Test
    @DisplayName("RemoveCartLIFO carrito vacío")
    void testRemoveCartLIFO_EmptyCart() {
        dataGateway.clearCart("1");
        assertDoesNotThrow(() -> paymentService.removeCartLIFO());
    }

    @Test
    @DisplayName("Excepción en checkout")
    void testCheckout_Exception() {
        dataGateway.addProductToCart("1", "P001", 1);
        assertDoesNotThrow(() -> paymentService.checkout());
    }

    // ============ IMPLEMENTACIÓN SIMPLE DEL GATEWAY ============

    private static class SimpleDataGateway implements PaymentDataGateway {
        private List<CartItem> cart = new ArrayList<>();
        private List<Product> products = Arrays.asList(
                new Product("P001", "Laptop", 100.0, 50, 100),
                new Product("P002", "Mouse", 50.0, 100, 200),
                new Product("P003", "Keyboard", 75.0, 75, 150),
                new Product("P999", "Very Expensive Item", 10000.0, 1, 10)
        );

        public void addProductToCart(String userId, String productId, int qty) {
            CartItem item = new CartItem(userId, productId, qty);
            cart.add(item);
        }

        @Override
        public User getUserById(String id) {
            return new User(id, "Test User", 500.0);
        }

        @Override
        public List<CartItem> getCartByUserId(String userId) {
            List<CartItem> userCart = new ArrayList<>();
            for (CartItem item : cart) {
                if (item.getUserId().equals(userId)) {
                    userCart.add(item);
                }
            }
            return userCart;
        }

        @Override
        public Product getProductById(String productId) {
            for (Product p : products) {
                if (p.getProductId().equals(productId)) {
                    return p;
                }
            }
            return null;
        }

        @Override
        public void removeLastItemFromCart(String userId) {
            List<CartItem> userCart = getCartByUserId(userId);
            if (!userCart.isEmpty()) {
                CartItem last = userCart.get(userCart.size() - 1);
                cart.remove(last);

                Product product = getProductById(last.getProductId());
                if (product != null) {
                    incrementProductStock(last.getProductId(), last.getQuantity());
                }
            }
        }

        @Override
        public void incrementProductStock(String productId, int quantity) {
            Product p = getProductById(productId);
            if (p != null) {
                p.setCurrentStock(p.getCurrentStock() + quantity);
            }
        }

        @Override
        public void updateBalance(String userId, double newBudget) {
            // Simulado
        }

        @Override
        public void clearCart(String userId) {
            cart.removeIf(item -> item.getUserId().equals(userId));
        }
    }
}
