package com.alerts.rules;

import com.alerts.AlertRule;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class RapidDropRule implements AlertRule {
    @Override
    public List<Alert> evaluate(Patient patient, long startTime, long endTime) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        List<Double> values = new ArrayList<>();
        List<Long> timestamps = new ArrayList<>();

        for (PatientRecord record : records) {
            if (record.getRecordType().equalsIgnoreCase("OxygenSaturation")) {
                values.add(record.getMeasurementValue());
                timestamps.add(record.getTimestamp());
            }
        }

        for (int i = 0; i < values.size(); i++) {
            for (int j = i + 1; j < values.size(); j++) {
                if (timestamps.get(j) - timestamps.get(i) <= 10 * 60 * 1000) {
                    if (values.get(i) - values.get(j) >= 5.0) {
                        alerts.add(new Alert(patient.getPatientId(), timestamps.get(j),
                                "Rapid oxygen saturation drop of 5% or more"));
                        return alerts; // alert once per rapid drop
                    }
                }
            }
        }
        return alerts;
    }
}