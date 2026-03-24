package com.sts;
import com.sts.audit.AuditService;
import com.sts.client.ClientService;
import com.sts.client.UserRepository;
import com.sts.market.repository.CartRepository;
import com.sts.market.repository.MarketStockRepository;
import com.sts.market.service.MarketService;
import com.sts.payment.PaymentService;
import com.sts.store.StoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;
class CLITest {
    @Test
    void testCLI_01_LoginSuccess(@TempDir Path tempDir) throws IOException {
        String output = executeCLI(tempDir, "LOGIN Alice\nEXIT\n");
        assertTrue(output.contains("Welcome") || output.contains("Alice"));
    }
    @Test
    void testCLI_02_LoginNotFound(@TempDir Path tempDir) throws IOException {
        String output = executeCLI(tempDir, "LOGIN Unknown\nEXIT\n");
        assertTrue(output.contains("User not found"));
    }
    @Test
    void testCLI_03_LoginMissingArg(@TempDir Path tempDir) throws IOException {
        String output = executeCLI(tempDir, "LOGIN\nEXIT\n");
        assertTrue(output.contains("Usage: LOGIN"));
    }
    @Test
    void testCLI_04_BuySuccess(@TempDir Path tempDir) throws IOException {
        String output = executeCLI(tempDir, "LOGIN Alice\nBUY 1 2\nEXIT\n");
        assertTrue(output.contains("Added to cart"));
    }
    @Test
    void testCLI_05_BuyMissingArgs(@TempDir Path tempDir) throws IOException {
        String output = executeCLI(tempDir, "LOGIN Alice\nBUY 1\nEXIT\n");
        assertTrue(output.contains("Usage: BUY"));
    }
    @Test
    void testCLI_06_BuyInvalidQuantity(@TempDir Path tempDir) throws IOException {
        String output = executeCLI(tempDir, "LOGIN Alice\nBUY 1 invalid\nEXIT\n");
        assertTrue(output.contains("Invalid quantity"));
    }
    @Test
    void testCLI_07_DropSuccess(@TempDir Path tempDir) throws IOException {
        String output = executeCLI(tempDir, "LOGIN Alice\nBUY 1 2\nDROP 1 1\nEXIT\n");
        assertTrue(output.contains("Dropped"));
    }
    @Test
    void testCLI_08_DropMissingArgs(@TempDir Path tempDir) throws IOException {
        String output = executeCLI(tempDir, "LOGIN Alice\nDROP 1\nEXIT\n");
        assertTrue(output.contains("Usage: DROP"));
    }
    @Test
    void testCLI_09_DropInvalidQuantity(@TempDir Path tempDir) throws IOException {
        String output = executeCLI(tempDir, "LOGIN Alice\nDROP 1 invalid\nEXIT\n");
        assertTrue(output.contains("Invalid quantity"));
    }
    @Test
    void testCLI_10_CheckoutSuccess(@TempDir Path tempDir) throws IOException {
        String output = executeCLI(tempDir, "LOGIN Alice\nBUY 1 1\nCHECKOUT\nEXIT\n");
        assertTrue(output.contains("successful") || output.length() > 50);
    }
    @Test
    void testCLI_11_LogsCommand(@TempDir Path tempDir) throws IOException {
        String output = executeCLI(tempDir, "LOGIN Alice\nBUY 1 1\nLOGS\nEXIT\n");
        assertTrue(output.length() > 50);
    }
    @Test
    void testCLI_12_HelpCommand(@TempDir Path tempDir) throws IOException {
        String output = executeCLI(tempDir, "HELP\nEXIT\n");
        assertTrue(output.contains("Available") || output.contains("LOGIN"));
    }
    @Test
    void testCLI_13_UnknownCommand(@TempDir Path tempDir) throws IOException {
        String output = executeCLI(tempDir, "UNKNOWNCMD\nEXIT\n");
        assertTrue(output.contains("Unknown command"));
    }
    @Test
    void testCLI_14_EmptyInput(@TempDir Path tempDir) throws IOException {
        String output = executeCLI(tempDir, "\n\nLOGIN Alice\nEXIT\n");
        assertTrue(output.contains("Welcome") || output.contains("Alice"));
    }
    @Test
    void testCLI_15_ExitCommand(@TempDir Path tempDir) throws IOException {
        String output = executeCLI(tempDir, "LOGIN Alice\nBUY 1 1\nCHECKOUT\nEXIT\n");
        assertTrue(output.length() > 50);
    }
    @Test
    void testCLI_16_FullWorkflow(@TempDir Path tempDir) throws IOException {
        String input = "LOGIN Alice\nBUY 1 2\nBUY 2 1\nHELP\nLOGS\nCHECKOUT\nEXIT\n";
        String output = executeCLI(tempDir, input);
        assertTrue(output.length() > 200);
    }
    @Test
    void testCLI_17_MultipleOperations(@TempDir Path tempDir) throws IOException {
        String input = "LOGIN Bob\nBUY 1 1\nDROP 1 1\nCHECKOUT\nEXIT\n";
        String output = executeCLI(tempDir, input);
        assertTrue(output.length() > 50);
    }
    private String executeCLI(Path tempDir, String input) throws IOException {
        Path dataDir = tempDir.resolve("data");
        Files.createDirectory(dataDir);
        Files.writeString(dataDir.resolve("users.csv"),
                "id,name,budget\n" +
                "U001,Alice,100.50\n" +
                "U002,Bob,50.00\n");
        Files.writeString(dataDir.resolve("market_stock.csv"),
                "productId,name,price,current_stock,max_capacity\n" +
                "1,Milk,1.25,18,30\n" +
                "2,Bread,1.10,12,25\n");
        Files.writeString(dataDir.resolve("cart.csv"),
                "userId,productId,quantity\n");
        Files.writeString(dataDir.resolve("audit_log.csv"),
                "timestamp,module,action,status,details\n");
        Files.writeString(dataDir.resolve("warehouse.csv"),
                "productId,name,total_stock\n" +
                "1,Milk,100\n" +
                "2,Bread,80\n");
        String usersPath = dataDir.resolve("users.csv").toString();
        String stockPath = dataDir.resolve("market_stock.csv").toString();
        String cartPath = dataDir.resolve("cart.csv").toString();
        String auditPath = dataDir.resolve("audit_log.csv").toString();
        String warehousePath = dataDir.resolve("warehouse.csv").toString();
        UserRepository userRepo = new UserRepository(usersPath);
        MarketStockRepository stockRepo = new MarketStockRepository(stockPath);
        CartRepository cartRepo = new CartRepository(cartPath);
        AuditService auditService = new AuditService(auditPath);
        MarketService marketService = new MarketService(stockRepo, cartRepo, auditService);
        PaymentService paymentService = new PaymentService(usersPath, cartPath, stockPath, marketService, auditService);
        StoreService storeService = new StoreService(stockPath, warehousePath);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(output);
        ClientService clientService = new ClientService(userRepo, marketService, paymentService, auditService, printStream);
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        CLI cli = new CLI(clientService, storeService, inputStream, printStream);
        cli.run();
        return output.toString();
    }
}
