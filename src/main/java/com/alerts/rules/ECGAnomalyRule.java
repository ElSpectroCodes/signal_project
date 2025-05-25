package com.alerts.rules;

//Old Week3 code
import com.alerts.Alert; 
import com.alerts.AlertRule;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class ECGAnomalyRule implements AlertRule {
    @Override
    public List<Alert> evaluate(Patient patient, long startTime, long endTime) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        List<Double> ecgValues = new ArrayList<>();
        for (PatientRecord record : records) {
            if (record.getRecordType().equalsIgnoreCase("ECG")) {
                ecgValues.add(record.getMeasurementValue());
            }
        }

        int window = 5;
        for (int i = window; i < ecgValues.size(); i++) {
            double sum = 0.0;
            for (int j = i - window; j < i; j++) {
                sum += ecgValues.get(j);
            }
            double avg = sum / window;
            if (ecgValues.get(i) > avg * 1.5) {
                alerts.add(new Alert(patient.getPatientId(), System.currentTimeMillis(),
                        "Abnormal ECG peak detected"));
                break;
            }
        }

        return alerts;
    }
}

