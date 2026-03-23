package com.sts.audit;

public class ManageLogWriter {

    private final AuditorWriterCsv auditor;

    public ManageLogWriter(AuditorWriterCsv auditor) {
        this.auditor = auditor;
    }

    public void processSuccess(String userId, double amount) {
        String details = "userId:" + userId + ";amount:" + amount;

        auditor.log("PAYMENT", EventType.PAYMENT_ACCEPTED, "SUCCESS", details);

        System.out.println("El proceso ha sido exitoso.");
    }

    public void processFailure(String userId, String reason) {
        String details = "userId:" + userId + ";reason:" + reason;

        auditor.log("PAYMENT", EventType.PAYMENT_DENIED, "FAILURE", details);

        System.out.println("El proceso ha fallado.");
    }
}