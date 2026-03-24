package com.sts.payment.data_access;

public class CartItem {

    private String userId, productId;
    private int quantity;

    public CartItem(String userId, String productId, int quantity){
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }


    public String getProductId() {
        return productId;
    }

    public String getUserId() {
        return userId;
    }

    public int getQuantity() {
        return quantity;
    }
}
