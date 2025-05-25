package com.alerts.rules;

import com.alerts.AlertRule;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class ManualAlertRule implements AlertRule {
    @Override
    public List<Alert> evaluate(Patient patient, long startTime, long endTime) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        for (PatientRecord record : records) {
            if (record.getRecordType().equalsIgnoreCase("Alert") &&
                String.valueOf(record.getMeasurementValue()).equalsIgnoreCase("triggered")) {
                alerts.add(new Alert(patient.getPatientId(), record.getTimestamp(),
                        "Manual alert triggered by patient or nurse"));
            }
        }

        return alerts;
    }
}
