package com.sts.payment.integration;

public class PaymentResult {
    public String userId;
    public double totalAmount;
    public boolean isSuccess;
    public String message;

    public PaymentResult(String userId, double totalAmount, boolean isSuccess, String message) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
