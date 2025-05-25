package com.alerts.strategy;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class OxygenSaturationStrategy implements AlertStrategy {
    @Override
    public List<Alert> checkAlert(Patient patient, long startTime, long endTime) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        List<Double> saturationValues = new ArrayList<>();
        List<Long> timestamps = new ArrayList<>();

        for (PatientRecord record : records) {
            if (record.getRecordType().equalsIgnoreCase("OxygenSaturation")) {
                double value = record.getMeasurementValue();
                saturationValues.add(value);
                timestamps.add(record.getTimestamp());

                if (value < 92.0) {
                    alerts.add(new Alert(patient.getPatientId(), record.getTimestamp(),
                            "Low blood oxygen saturation: " + value));
                }
            }
        }

        if (checkRapidDrop(saturationValues, timestamps)) {
            alerts.add(new Alert(patient.getPatientId(), endTime,
                    "Rapid drop in oxygen saturation within 10 minutes"));
        }

        return alerts;
    }

    private boolean checkRapidDrop(List<Double> values, List<Long> timestamps) {
        for (int i = 0; i < values.size(); i++) {
            for (int j = i + 1; j < values.size(); j++) {
                if (timestamps.get(j) - timestamps.get(i) <= 10 * 60 * 1000) {
                    if (values.get(i) - values.get(j) >= 5.0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
