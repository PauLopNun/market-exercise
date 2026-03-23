package com.sts.payment.data_access;

import com.sts.payment.payment_management.PaymentDataGateway;
import com.sts.shared.model.Product;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public void updateBalance(String userId, double newBudget) {
        Path path = Path.of(this.usersFile);
        int attempts = 0;
        boolean success = false;

        while (attempts < 3 && !success) {
            try {
                List<String> lines = Files.readAllLines(path);
                for (int i = 0; i < lines.size(); i++) {
                    String[] parts = lines.get(i).split(",");
                    if (parts[0].equals(userId)) {
                        lines.set(i, parts[0] + "," + parts[1] + "," + newBudget);
                        break;
                    }
                }
                Files.write(path, lines);
                success = true;
            } catch (IOException e) {
                attempts++;
                if (attempts >= 3) {
                    System.err.println("Error crítico tras 3 intentos: " + e.getMessage());
                } else {
                    try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                }
            }
        }
    }

    @Override
    public void clearCart(String userId) {

        Path path = Path.of(this.cartFile);

        try {
            List<String> lines = Files.readAllLines(path);

            lines.removeIf(line -> line.startsWith(userId + ","));
            Files.write(path, lines);

        } catch (IOException e) {
            System.err.println("Error vaciando el carrito: " + e.getMessage());
        }
    }

    @Override
    public void removeLastItemFromCart(String userId) {

        Path path = Path.of(this.cartFile);

        try {

            List<String> lines = Files.readAllLines(path);

            for (int i = lines.size() - 1; i >= 0; i--) {

                if (lines.get(i).startsWith(userId + ",")) {
                    lines.remove(i);
                    break;
                }

            }
            Files.write(path, lines);

        } catch (IOException e) {
            System.err.println("Error borrando último item: " + e.getMessage());
        }
    }

    @Override
    public void incrementProductStock(String productId, int quantity) {

        Path path = Path.of(this.marketFile);

        try {

            List<String> lines = Files.readAllLines(path);

            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");

                if (parts[0].equals(productId)) {

                    int currentStock = Integer.parseInt(parts[3]);
                    int newStock = currentStock + quantity;
                    lines.set(i, parts[0] + "," + parts[1] + "," + parts[2] + "," + newStock + "," + parts[4]);
                    break;

                }
            }

            Files.write(path, lines);
        } catch (IOException e) {
            System.err.println("Error incrementando stock: " + e.getMessage());
        }
    }
}
