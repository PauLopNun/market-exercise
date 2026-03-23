package com.sts.payment.integration;

public enum EventType {
    PAYMENT_SUCCESS,
    PAYMENT_DENIED,
    LIFO_ITEM_REMOVED,
    INVENTORY_UPDATED
}



/*
* Llamar al metodo de logs
* Logs.log("PAYMENT", "DELETE", "SUCCESS", "El pago se ha cancelado.")
*
*Logs.log(modulo, action, status, details(strings))
*
* */