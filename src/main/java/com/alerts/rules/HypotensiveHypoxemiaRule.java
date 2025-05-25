package com.alerts.rules;

import com.alerts.AlertRule;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class HypotensiveHypoxemiaRule implements AlertRule {
    @Override
    public List<Alert> evaluate(Patient patient, long startTime, long endTime) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        boolean hypotension = false;
        boolean hypoxia = false;

        for (PatientRecord record : records) {
            if (record.getRecordType().equalsIgnoreCase("SystolicBP") && record.getMeasurementValue() < 90) {
                hypotension = true;
            } else if (record.getRecordType().equalsIgnoreCase("OxygenSaturation") && record.getMeasurementValue() < 92) {
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

