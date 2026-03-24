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
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {

        String dataPath = "data";
        String usersPath = Paths.get(dataPath, "users.csv").toString();
        String marketStockPath = Paths.get(dataPath, "market_stock.csv").toString();
        String cartPath = Paths.get(dataPath, "cart.csv").toString();
        String auditPath = Paths.get(dataPath, "audit_log.csv").toString();
        String warehousePath = Paths.get(dataPath, "warehouse.csv").toString();

        UserRepository userRepository = new UserRepository(usersPath);
        MarketStockRepository marketStockRepository = new MarketStockRepository(marketStockPath);
        CartRepository cartRepository = new CartRepository(cartPath);
        AuditService auditService = new AuditService(auditPath);

        MarketService marketService = new MarketService(marketStockRepository, cartRepository, auditService);
        PaymentService paymentService = new PaymentService(usersPath, cartPath, marketStockPath, marketService, auditService);
        StoreService storeService = new StoreService(marketStockPath, warehousePath);

        ClientService clientService = new ClientService(userRepository, marketService, paymentService, auditService, System.out);

        CLI cli = new CLI(clientService, storeService, System.in, System.out);
        cli.run();
    }
}
