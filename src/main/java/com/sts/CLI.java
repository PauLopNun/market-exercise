package com.sts;

import com.sts.audit.AuditService;
import com.sts.client.ClientService;
import com.sts.client.UserRepository;
import com.sts.market.repository.CartRepository;
import com.sts.market.repository.MarketStockRepository;
import com.sts.market.service.MarketService;
import com.sts.payment.PaymentService;
import com.sts.store.StoreService;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;


public class CLI {

    private final ClientService clientService;
    private final StoreService storeService;
    private final InputStream input;
    private final PrintStream output;

    public CLI(ClientService clientService, StoreService storeService, InputStream input, PrintStream output) {
        this.clientService = clientService;
        this.storeService = storeService;
        this.input = input;
        this.output = output;
    }

    public void run() throws IOException {
        output.println("StS - Simple Terminal Supermarket");
        output.println("Type HELP for available commands\n");

        try (Scanner scanner = new Scanner(input)) {
            boolean running = true;

            while (running && scanner.hasNextLine()) {
                output.print("> ");
                String line = scanner.nextLine().trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] tokens = line.split("\\s+");
                String command = tokens[0].toUpperCase();

                try {
                    running = processCommand(command, tokens);
                } catch (IOException e) {
                    output.println("Error: " + e.getMessage());
                } catch (IllegalStateException e) {
                    output.println(e.getMessage());
                }
            }
        }
    }

    private boolean processCommand(String command, String[] tokens) throws IOException {
        switch (command) {
            case "LOGIN":
                return handleLogin(tokens);

            case "BUY":
                return handleBuy(tokens);

            case "DROP":
                return handleDrop(tokens);

            case "CHECKOUT":
                return handleCheckout();

            case "LOGS":
                return handleLogs();

            case "HELP":
                return handleHelp();

            case "EXIT":
                return handleExit();

            default:
                output.println("Unknown command: " + command + ". Type HELP for available commands.");
                return true;
        }
    }

    private boolean handleLogin(String[] tokens) throws IOException {
        if (tokens.length < 2) {
            output.println("Usage: LOGIN <user_id_or_name>");
            return true;
        }
        clientService.login(tokens[1]);
        return true;
    }

    private boolean handleBuy(String[] tokens) throws IOException {
        if (tokens.length < 3) {
            output.println("Usage: BUY <product_id> <quantity>");
            return true;
        }
        try {
            int qty = Integer.parseInt(tokens[2]);
            clientService.buy(tokens[1], qty);
        } catch (NumberFormatException e) {
            output.println("Invalid quantity");
        }
        return true;
    }

    private boolean handleDrop(String[] tokens) throws IOException {
        if (tokens.length < 3) {
            output.println("Usage: DROP <product_id> <quantity>");
            return true;
        }
        try {
            int qty = Integer.parseInt(tokens[2]);
            clientService.drop(tokens[1], qty);
        } catch (NumberFormatException e) {
            output.println("Invalid quantity");
        }
        return true;
    }

    private boolean handleCheckout() throws IOException {
        clientService.checkout();
        storeService.refillProducts(); // Auto-replenish from warehouse
        return true;
    }

    private boolean handleLogs() throws IOException {
        clientService.logs();
        return true;
    }

    private boolean handleHelp() {
        output.println("\n--- Available Commands ---");
        output.println("LOGIN <user_id_or_name>  - Log in with user ID or name");
        output.println("BUY <product_id> <qty>   - Add item to cart");
        output.println("DROP <product_id> <qty>  - Remove item from cart");
        output.println("CHECKOUT                 - Pay for cart and trigger replenishment");
        output.println("LOGS                     - Show audit log");
        output.println("HELP                     - Show this message");
        output.println("EXIT                     - Close session and show daily report\n");
        return true;
    }

    private boolean handleExit() throws IOException {
        clientService.exit();
        return false;
    }
}
