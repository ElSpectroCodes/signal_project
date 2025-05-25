package com.alerts;

import com.data_management.Patient;
import java.util.List;

public interface AlertRule {
    List<Alert> evaluate(Patient patient, long startTime, long endTime);
}
