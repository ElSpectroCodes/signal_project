package com.alerts.strategy;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class HypotensiveHypoxemiaStrategy implements AlertStrategy {
    @Override
    public List<Alert> checkAlert(Patient patient, long startTime, long endTime) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        boolean hypotension = false;
        boolean hypoxia = false;

        for (PatientRecord record : records) {
            String type = record.getRecordType();
            double value = record.getMeasurementValue();

            if (type.equalsIgnoreCase("SystolicBP") && value < 90) {
                hypotension = true;
            }
            if (type.equalsIgnoreCase("OxygenSaturation") && value < 92) {
                hypoxia = true;
            }
        }

        if (hypotension && hypoxia) {
            alerts.add(new Alert(patient.getPatientId(), System.currentTimeMillis(),
                    "Hypotensive Hypoxemia Alert: Low BP and low oxygen saturation"));
        }

        return alerts;
    }
}
