package com.sts.payment.payment_management;

import com.sts.payment.User;
import com.sts.payment.CartEntry;
import com.sts.payment.Product;
import java.util.List;

public interface PaymentDataGateway {

    User getUserById(String id);
    List<CartEntry> getCartByUserId(String userId);
    Product getProductById(String productId);
    void removeLastItemFromCart(String userId);
    void incrementProductStock(String productId, int quantity);
    void updateBalance(String userId, double newBudget);
    void clearCart(String userId);

}
