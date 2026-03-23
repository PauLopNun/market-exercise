package com.sts.payment.integration;

public class PaymentCoordinator {
    private PaymentAuditor auditor = new PaymentAuditor();

    public void processSuccess(String userId, double amount) {
        auditor.log(EventType.PAYMENT_SUCCESS, "Usuario: " + userId + " pagó " + amount);
        System.out.println("[INFO] Notificando éxito al resto de módulos...");
    }

    public void processFailure(String userId, String reason) {
        auditor.log(EventType.PAYMENT_DENIED, "Usuario: " + userId + " Falló por: " + reason);
        System.out.println("[ALERTA] Notificando fallo. Realizando limpieza de datos.");
    }
}
