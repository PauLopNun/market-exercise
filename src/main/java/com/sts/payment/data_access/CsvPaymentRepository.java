package com.sts.payment.data_access;

import com.sts.payment.payment_management.PaymentDataGateway;
import com.sts.shared.model.Product;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvPaymentRepository implements PaymentDataGateway {

    private String usersFile;
    private String marketFile;
    private String cartFile;

    public CsvPaymentRepository(String usersFile, String marketFile, String cartFile) {
        this.usersFile = usersFile;
        this.marketFile = marketFile;
        this.cartFile = cartFile;
    }

    @Override
    public User getUserById(String id) {
        User userFound = null;

        try (BufferedReader br = new BufferedReader(new FileReader(this.usersFile))) {
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

        List<CartItem> cartItemsList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(this.cartFile))) {
            String line;

            while ((line = br.readLine()) != null) {

                String[] cartParts = line.split(",");

                if (cartParts[0].equals(userId)) {
                    cartItemsList.add(new CartItem(cartParts[0], cartParts[1], Integer.parseInt(cartParts[2])));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al acceder al archivo: " + e.getMessage());
        }

        return cartItemsList;
    }

    @Override
    public Product getProductById(String productId) {
        Product productFound = null;

        try (BufferedReader br = new BufferedReader(new FileReader(this.marketFile))) {
            String line;

            while ((line = br.readLine()) != null) {

                String[] productParts = line.split(",");

                if (productParts[0].equals(productId)) {
                    productFound = new Product(productParts[0], productParts[1], Double.parseDouble(productParts[2]), Integer.parseInt(productParts[3]), Integer.parseInt(productParts[4]));
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al acceder al archivo: " + e.getMessage());
        }

        return productFound;
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
