package com.sts.payment.payment_management;

import com.sts.payment.data_access.CartItem;
import com.sts.payment.data_access.User;
import com.sts.shared.model.Product;

import java.util.List;

public interface PaymentDataGateway {

    User getUserById(String id);
    List<CartItem> getCartByUserId(String userId);
    Product getProductById(String productId);
    void removeLastItemFromCart(String userId);
    void incrementProductStock(String productId, int quantity);
    void updateBalance(String userId, double newBudget);
    void clearCart(String userId);

}
