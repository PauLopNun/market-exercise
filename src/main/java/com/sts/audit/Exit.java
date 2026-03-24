package com.sts.audit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Exit {

    public static Map<String, Object> process(String filePath) {

        double totalRevenue = 0;
        Map<String, Integer> productCount = new HashMap<>();
        Set<String> failedUsers = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;
            boolean header = true;

            while ((line = br.readLine()) != null) {

                if (header) {
                    header = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String status = parts[3];
                String details = parts[parts.length - 1];

                EventType event;
                try {
                    event = EventType.valueOf(parts[2]);
                } catch (Exception e) {
                    continue;
                }

                Map<String, String> map = parseDetails(details);

                if (event == EventType.ITEM_PURCHASED) {
                    if (map.containsKey("PRICE")) {
                        totalRevenue += Double.parseDouble(map.get("PRICE"));
                    }
                    if (map.containsKey("NAME")) {
                        String product = map.get("NAME");
                        productCount.put(product, productCount.getOrDefault(product, 0) + 1);
                    }
                }

                if (event == EventType.PAYMENT && "FAILURE".equalsIgnoreCase(status)) {
                    if (map.containsKey("USER")) {
                        failedUsers.add(map.get("USER"));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("revenue", totalRevenue);
        result.put("products", productCount);
        result.put("failedUsers", failedUsers);

        return result;
    }

    private static Map<String, String> parseDetails(String details) {
        Map<String, String> map = new HashMap<>();

        String[] pairs = details.split(" ");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                map.put(kv[0], kv[1].replace("\"", ""));
            }
        }

        return map;
    }
}