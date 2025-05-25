package com.alerts.rules;

import com.alerts.AlertRule;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class BloodPressureTrendRule implements AlertRule {

    @Override
    public List<Alert> evaluate(Patient patient, long start, long end) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getRecords(start, end);

        List<Double> systolicList = new ArrayList<>();
        List<Double> diastolicList = new ArrayList<>();

        for (PatientRecord record : records) {
            if (record.getRecordType().equalsIgnoreCase("SystolicBP")) {
                systolicList.add(record.getMeasurementValue());
            } else if (record.getRecordType().equalsIgnoreCase("DiastolicBP")) {
                diastolicList.add(record.getMeasurementValue());
            }
        }

        long now = System.currentTimeMillis();
        if (checkTrend(systolicList)) {
            alerts.add(new Alert(patient.getPatientId(), now, "Systolic trend alert (3 increasing/decreasing readings)"));
        }
        if (checkTrend(diastolicList)) {
            alerts.add(new Alert(patient.getPatientId(), now, "Diastolic trend alert (3 increasing/decreasing readings)"));
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