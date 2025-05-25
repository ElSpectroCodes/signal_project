package com.alerts.stratergy.strategy;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class BloodPressureStrategy implements AlertStrategy {
    @Override
    public List<Alert> checkAlert(Patient patient, long startTime, long endTime) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        List<Double> systolicList = new ArrayList<>();
        List<Double> diastolicList = new ArrayList<>();

        for (PatientRecord record : records) {
            String type = record.getRecordType();
            double value = record.getMeasurementValue();
            long timestamp = record.getTimestamp();

            if (type.equalsIgnoreCase("SystolicBP")) {
                systolicList.add(value);
                if (value > 180 || value < 90) {
                    alerts.add(new Alert(patient.getPatientId(), timestamp, "Critical systolic BP: " + value));
                }
            } else if (type.equalsIgnoreCase("DiastolicBP")) {
                diastolicList.add(value);
                if (value > 120 || value < 60) {
                    alerts.add(new Alert(patient.getPatientId(), timestamp, "Critical diastolic BP: " + value));
                }
            }
        }

        if (checkTrend(systolicList)) {
            alerts.add(new Alert(patient.getPatientId(), endTime, "Systolic BP trend alert (>10 mmHg change)"));
        }
        if (checkTrend(diastolicList)) {
            alerts.add(new Alert(patient.getPatientId(), endTime, "Diastolic BP trend alert (>10 mmHg change)"));
        }

        return alerts;
    }

    private boolean checkTrend(List<Double> values) {
        if (values.size() < 3) return false;
        for (int i = 0; i < values.size() - 2; i++) {
            double v1 = values.get(i);
            double v2 = values.get(i + 1);
            double v3 = values.get(i + 2);

            boolean increasing = (v2 - v1 > 10) && (v3 - v2 > 10);
            boolean decreasing = (v1 - v2 > 10) && (v2 - v3 > 10);

            if (increasing || decreasing) return true;
        }
        return false;
    }
}

