package com.alerts.rules;

import com.alerts.AlertRule;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class BloodPressureThresholdRule implements AlertRule {
    @Override
    public List<Alert> evaluate(Patient patient, long startTime, long endTime) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        for (PatientRecord record : records) {
            String type = record.getRecordType();
            double value = record.getMeasurementValue();

            if (type.equalsIgnoreCase("SystolicBP") && (value > 180 || value < 90)) {
                alerts.add(new Alert(patient.getPatientId(), record.getTimestamp(),
                        "Critical systolic blood pressure: " + value));
            } else if (type.equalsIgnoreCase("DiastolicBP") && (value > 120 || value < 60)) {
                alerts.add(new Alert(patient.getPatientId(), record.getTimestamp(),
                        "Critical diastolic blood pressure: " + value));
            }
        }
        return alerts;
    }
}