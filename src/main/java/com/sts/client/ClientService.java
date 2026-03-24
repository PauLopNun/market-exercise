package com.sts.client;

import com.sts.audit.AuditService;
import com.sts.market.service.MarketService;
import com.sts.payment.PaymentService;
import com.sts.shared.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class ClientService {

    private final UserRepository userRepository;
    private final MarketService marketService;
    private final PaymentService paymentService;
    private final AuditService auditService;
    private final PrintStream out;

    private User currentUser;

    public ClientService(UserRepository userRepository, MarketService marketService,
                         PaymentService paymentService, AuditService auditService,
                         PrintStream out) {
        this.userRepository = userRepository;
        this.marketService = marketService;
        this.paymentService = paymentService;
        this.auditService = auditService;
        this.out = out;
    }

    public boolean login(String input) throws IOException {
        User user = userRepository.findByIdOrName(input);
        if (user == null) {
            out.println("User not found: " + input);
            return false;
        }
        currentUser = user;
        out.println("Welcome, " + currentUser.getName() + "! Budget: " + currentUser.getBudget() + "€");
        return true;
    }

    public boolean buy(String productId, int qty) throws IOException {
        requireLogin();
        boolean result = marketService.buy(currentUser.getId(), productId, qty);
        if (result) {
            out.println("Added to cart: " + productId + " x" + qty);
        } else {
            out.println("Cannot buy: insufficient stock for " + productId);
        }
        return result;
    }

    public void drop(String productId, int qty) throws IOException {
        requireLogin();
        marketService.drop(currentUser.getId(), productId, qty);
        out.println("Dropped: " + productId + " x" + qty);
    }

    public boolean checkout() throws IOException {
        requireLogin();
        boolean result = paymentService.checkout(currentUser.getId());
        if (result) {
            out.println("Checkout successful!");
        } else {
            out.println("Checkout failed: insufficient funds.");
        }
        return result;
    }

    public void logs() throws IOException {
        requireLogin();
        List<String[]> entries = auditService.readAll();
        if (entries.isEmpty()) {
            out.println("No logs found.");
        } else {
            entries.forEach(logEntry -> out.println(String.join(" | ", logEntry)));
        }
    }

    public void exit() throws IOException {
        out.println("--- Daily Report ---");
        out.println("Revenue: " + auditService.getTotalRevenue() + "€");
        out.println("Top products: " + auditService.getTop3Products());
        out.println("Users over budget: " + auditService.getUsersWhoExceededBudget());
        out.println("Goodbye!");
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private void requireLogin() {
        if (currentUser == null) throw new IllegalStateException("Not logged in");
    }
}
