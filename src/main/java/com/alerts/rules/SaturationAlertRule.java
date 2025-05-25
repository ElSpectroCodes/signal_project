package com.alerts.rules;

import com.alerts.AlertRule;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class SaturationAlertRule implements AlertRule {
    @Override
    public List<Alert> evaluate(Patient patient, long startTime, long endTime) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        for (PatientRecord record : records) {
            if (record.getRecordType().equalsIgnoreCase("OxygenSaturation")) {
                double value = record.getMeasurementValue();
                if (value < 92.0) {
                    alerts.add(new Alert(patient.getPatientId(), record.getTimestamp(),
                            "Low blood oxygen saturation: " + value));
                }
            }
        }
        return alerts;
    }
}