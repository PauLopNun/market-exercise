package com.sts.store;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoExtension.*;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class StoreImplementsTest {
    private final static StoreImplements storeImplements = new StoreImplements();

    @Test
    void refillProducts() {
        storeImplements.refillProducts();
    }

    @Test
    void writeMarketCSV() {
        List<Product>productList=new ArrayList<>();
        productList.add(new Product(1,"Leche",1.50,10,100));
        productList.add(new Product(2,"Pan",1.00,10,100));

        storeImplements.writeMarketCSV(productList);
        assertTrue(Files.exists(Path.of("data/market_stock.csv")));
    }

    @Test
    void writeWarehouseCSV(){
        List<WarehouseProduct> warehouseProductList =new ArrayList<>();
        warehouseProductList.add(new WarehouseProduct(1,"Leche",100));
        warehouseProductList.add(new WarehouseProduct(2,"Pan",100));

        storeImplements.writeWarehouseCSV(warehouseProductList);
        assertTrue(Files.exists(Path.of("data/warehouse.csv")));
    }

    @Test
    void getWarehouseProductsFromCSV(){
        List<WarehouseProduct>warehouseProductList = storeImplements.getWarehouseProductsFromCSV();
        assertFalse(warehouseProductList.isEmpty());
    }

    @Test
    void getProductsFromCSV(){
        List<Product>ProductList = storeImplements.getProductsFromCSV();
        assertFalse(ProductList.isEmpty());
    }

    @Test
    void replenishLowStockProducts() {
        List<WarehouseProduct> warehouseProductList =new ArrayList<>();
        warehouseProductList.add(new WarehouseProduct(1,"Leche",100));
        warehouseProductList.add(new WarehouseProduct(2,"Pan",100));

        storeImplements.writeWarehouseCSV(warehouseProductList);

        List<Product> marketProducts;
        marketProducts =  storeImplements.getProductsFromCSV();

        boolean condition = storeImplements.replenishLowStockProducts(marketProducts, warehouseProductList);

        assertTrue(condition);
    }


}