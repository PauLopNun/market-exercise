package com.sts.payment.integration;

public class PaymentCoordinator {

    public void processSuccess(String userId, double amount) {
        PaymentAuditor.log(EventType.PAYMENT_SUCCESS, "Usuario: " + userId + " pagó " + amount);
        System.out.println("[INFO] Notificando éxito al resto de módulos...");
        //Logs.log("PAYMENT", "REMOVELASTITEMFROMCART", "SUCCESS", "El pago se ha hecho.");
    }

    public void processFailure(String userId, String reason) {
        PaymentAuditor.log(EventType.PAYMENT_DENIED, "Usuario: " + userId + " Falló por: " + reason);
        System.out.println("Notificíon de fallo. Realizando limpieza de datos.");
        //Logs.log("PAYMENT", "REMOVELASTITEMFROMCART", "FAIL", "El pago se ha cancelado.");
    }

    public void checkout(String userId) {
        try {
            //Calcular total
            processSuccess(userId, 100.0);
        } catch (Exception e) {
            processFailure(userId, e.getMessage());
        }
    }

    public void handlePayment(PaymentResult result){
        if (result.isSuccess){
            this.processSuccess(result.userId, result.totalAmount);
        }else{
            this.processFailure(result.userId, result.message);
        }
    }

    public void processPayment(String userId, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo: " + amount);
        }
    }
}
