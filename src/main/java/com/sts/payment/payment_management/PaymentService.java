package com.sts.payment.payment_management;

import com.sts.payment.User;
import com.sts.payment.CartEntry;
import com.sts.payment.Product;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentService implements PaymentDataGateway {

    private static final String USERS_FILE = "data/users.csv";
    private static final String CART_FILE = "data/cart.csv";
    private static final String MARKET_FILE = "data/market_stock.csv";

    @Override
    public User getUserById(String id) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            reader.readLine(); // skip header
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                if (parts[0].equals(id)) {
                    return new User(parts[0], parts[1], Double.parseDouble(parts[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CartEntry> getCartByUserId(String userId) {
        List<CartEntry> cart = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CART_FILE))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                if (parts[0].equals(userId)) {
                    cart.add(new CartEntry(parts[0], parts[1], Integer.parseInt(parts[2])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cart;
    }

    @Override
    public Product getProductById(String productId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(MARKET_FILE))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                if (parts[0].equals(productId)) {
                    return new Product(parts[0], parts[1], Double.parseDouble(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void removeLastItemFromCart(String userId) {
        List<CartEntry> cart = getCartByUserId(userId);
        if (!cart.isEmpty()) {
            CartEntry last = cart.get(cart.size() - 1);
            // To remove the last item, but since LIFO, remove the last added, but cart is list, assuming order is addition order.
            // But to implement LIFO, we need to remove the last one in the list.
            cart.remove(cart.size() - 1);
            saveCart(cart);
            // Also, increment stock
            Product p = getProductById(last.getProductId());
            if (p != null) {
                incrementProductStock(last.getProductId(), last.getQuantity());
            }
        }
    }

    @Override
    public void incrementProductStock(String productId, int quantity) {
        List<Product> products = getAllProducts();
        for (Product p : products) {
            if (p.getProductId().equals(productId)) {
                p.setCurrentStock(p.getCurrentStock() + quantity);
                break;
            }
        }
        saveProducts(products);
    }

    @Override
    public void updateBalance(String userId, double newBudget) {
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.getId().equals(userId)) {
                u.setBudget(newBudget);
                break;
            }
        }
        saveUsers(users);
    }

    @Override
    public void clearCart(String userId) {
        List<CartEntry> cart = getAllCartEntries();
        cart.removeIf(e -> e.getUserId().equals(userId));
        saveCart(cart);
    }

    private List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(MARKET_FILE))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                products.add(new Product(parts[0], parts[1], Double.parseDouble(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }

    private void saveProducts(List<Product> products) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MARKET_FILE))) {
            writer.write("productId,name,price,current_stock,max_capacity\n");
            for (Product p : products) {
                writer.write(String.join(",", p.getProductId(), p.getName(), String.valueOf(p.getPrice()), String.valueOf(p.getCurrentStock()), String.valueOf(p.getMaxCapacity())) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                users.add(new User(parts[0], parts[1], Double.parseDouble(parts[2])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    private void saveUsers(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            writer.write("id,name,budget\n");
            for (User u : users) {
                writer.write(String.join(",", u.getId(), u.getName(), String.valueOf(u.getBudget())) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<CartEntry> getAllCartEntries() {
        List<CartEntry> cart = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CART_FILE))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                cart.add(new CartEntry(parts[0], parts[1], Integer.parseInt(parts[2])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cart;
    }

    private void saveCart(List<CartEntry> cart) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CART_FILE))) {
            writer.write("userId,productId,quantity\n");
            for (CartEntry e : cart) {
                writer.write(String.join(",", e.getUserId(), e.getProductId(), String.valueOf(e.getQuantity())) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkout(String u1) {
        // TODO
        return true;
    }
}
