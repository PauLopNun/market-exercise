package com.sts.payment.data_access;

import com.sts.payment.payment_management.PaymentDataGateway;
import com.sts.shared.model.Product;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CsvPaymentRepository implements PaymentDataGateway {

    private String fileName;
    public CsvPaymentRepository  (String fileName){
        this.fileName = fileName;
    }

    @Override
    public User getUserById(String id) {
        User userFound = null;

        try (BufferedReader br = new BufferedReader(new FileReader(this.fileName))) {
            String line;

            while ((line = br.readLine()) != null) {

                String[] userParts = line.split(",");

                if (userParts[0].equals(id)) {
                    userFound = new User(userParts[0], userParts[1], Double.parseDouble(userParts[2]));
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al acceder al archivo: " + e.getMessage());
        }

        return userFound;
    }

    @Override
    public List<CartItem> getCartByUserId(String userId) {
        return null;
    }

    @Override
    public Product getProductById(String productId) {
        return null;
    }

    @Override
    public void removeLastItemFromCart(String userId) {

    }

    @Override
    public void incrementProductStock(String productId, int quantity) {

    }

    @Override
    public void updateBalance(String userId, double newBudget) {

    }

    @Override
    public void clearCart(String userId) {

    }
}
