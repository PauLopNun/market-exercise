package com.sts.audit;

public class ManageLogWriter {

    public void processSuccess(String userId, double amount) {
        AuditorWriterCsv.log(EventType.PAYMENT_ACCEPTED, "Usuario: " + userId + " pagó " + amount);
        System.out.println("[INFO] Notificando éxito al resto de módulos...");
    }

    public void processFailure(String userId, String reason) {
        AuditorWriterCsv.log(EventType.PAYMENT_DENIED, "Usuario: " + userId + " Falló por: " + reason);
        System.out.println("[ALERTA] Notificando fallo. Realizando limpieza de datos.");
    }
}
