package com.sts.shared.audit;

import com.sts.audit.EventType;

public interface AuditLogger {

    void log(String module, EventType action, String status, String details);

}
