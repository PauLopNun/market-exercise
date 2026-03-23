package com.sts.client.service;

import com.sts.market.repository.CartRepository;
import com.sts.market.repository.MarketStockRepository;
import com.sts.market.service.MarketService;
import com.sts.shared.audit.AuditLogger;
import com.sts.shared.model.Product;
import com.sts.shared.model.User;

import javax.sound.sampled.AudioFileFormat;
import java.util.Scanner;

public class UserInterface {

    static MarketService marketService = new MarketService(new MarketStockRepository("data/market_stock.csv"),new CartRepository("data/cart.csv"));

    static final String SEPARATOR = "----------------------------------------------------------";

    public static void startInterface(){
        try(Scanner sc = new Scanner(System.in)){
            String identifier = getIdentifierByConsole(sc);
            User activeUser = UserLogin.login(identifier);
            boolean end = false;
            while(!end){
                printMenu(activeUser);
                String option = sc.nextLine().toLowerCase();
                try{
                    switch (option){
                        case "1","login":
                            activeUser = UserLogin.login(getIdentifierByConsole(sc));
                            //Logs.log("CLIENTS","LOGIN","SUCCESS",activeUser.getName() + " LOGGED IN");
                            break;
                        case "2","buy":
                            marketService.buy(activeUser.getId(), getIdentifierByConsole(sc), getQuantityByConsole(sc));
                            //buy
                            break;
                        case "3","drop":
                            marketService.drop(activeUser.getId(), getIdentifierByConsole(sc), getQuantityByConsole(sc));
                            //drop
                            break;
                        case "4","checkout":
                            //checkout
                            break;
                        case "5","logs":
                            //logs
                            break;
                        case "6","exit":
                            System.out.println("SEE YOU LATER ;)");
                            System.out.println(SEPARATOR);
                            end = true;
                            break;
                        default:
                            System.out.println("INCORRECT OPTION :(");
                    }
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }

            }
        }

    }

    private static void printMenu(User activeUser) {
        System.out.println("WELCOME TO MERCADONA, "+ activeUser.getName().toUpperCase() +", enter the number of the function you want to execute");

        printUserData(activeUser);

        System.out.println(SEPARATOR);
        System.out.println("1. LOGIN");
        System.out.println("2. BUY");
        System.out.println("3. DROP");
        System.out.println("4. CHECKOUT");
        System.out.println("5. LOGS");
        System.out.println("6. EXIT");
        System.out.println(SEPARATOR);
        System.out.print(">");
    }

    private static void printUserData(User activeUser){
        System.out.println("Current cart:");

    }

    public static String getIdentifierByConsole(Scanner sc) {
        System.out.println(SEPARATOR);
        System.out.println("- Enter identifier");
        return sc.next();
    }

    public static int getQuantityByConsole(Scanner sc){
        System.out.println(SEPARATOR);
        System.out.println("- Enter quantity");
        return sc.nextInt();
    }

}
