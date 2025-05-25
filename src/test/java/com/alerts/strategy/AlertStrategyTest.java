package com.alerts.strategy;

import com.alerts.Alert;
import com.data_management.Patient;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlertStrategyTest {

    private Patient buildPatientWithRecords(String type, double... values) {
        Patient patient = new Patient(1);
        long baseTime = System.currentTimeMillis();
        for (int i = 0; i < values.length; i++) {
            patient.addRecord(values[i], type, baseTime + i * 1000);
        }
        return patient;
    }

    @Test
    void testBloodPressureStrategy_TriggersOnThreshold() {
        Patient patient = buildPatientWithRecords("SystolicBP", 190.0);
        AlertStrategy strategy = new BloodPressureStrategy();
        List<Alert> alerts = strategy.checkAlert(patient, 0, System.currentTimeMillis());
        assertFalse(alerts.isEmpty());
        assertTrue(alerts.get(0).getMessage().contains("systolic"));
    }

    @Test
    void testBloodPressureStrategy_NoTriggerBelowThreshold() {
        Patient patient = buildPatientWithRecords("SystolicBP", 120.0, 115.0);
        AlertStrategy strategy = new BloodPressureStrategy();
        List<Alert> alerts = strategy.checkAlert(patient, 0, System.currentTimeMillis());
        assertTrue(alerts.isEmpty());
    }

    @Test
    void testOxygenSaturationStrategy_LowAndRapidDrop() {
        Patient patient = new Patient(2);
        long base = System.currentTimeMillis();
        patient.addRecord(96.0, "OxygenSaturation", base);
        patient.addRecord(90.0, "OxygenSaturation", base + 5 * 60 * 1000); // 5 min later

        AlertStrategy strategy = new OxygenSaturationStrategy();
        List<Alert> alerts = strategy.checkAlert(patient, base, base + 10 * 60 * 1000);
        assertEquals(2, alerts.size());
        assertTrue(alerts.get(0).getMessage().contains("Low"));
        assertTrue(alerts.get(1).getMessage().contains("drop"));
    }

    @Test
    void testOxygenSaturationStrategy_NoTriggerIfStable() {
        Patient patient = buildPatientWithRecords("OxygenSaturation", 95.0, 95.1);
        AlertStrategy strategy = new OxygenSaturationStrategy();
        List<Alert> alerts = strategy.checkAlert(patient, 0, System.currentTimeMillis());
        assertTrue(alerts.isEmpty());
    }

    @Test
void testHeartRateStrategy_TriggersOnECGSpike() {
    Patient patient = new Patient(1);
    long now = System.currentTimeMillis();
    patient.addRecord(0.9, "ECG", now);
    patient.addRecord(0.9, "ECG", now + 1000);
    patient.addRecord(0.9, "ECG", now + 2000);
    patient.addRecord(0.9, "ECG", now + 3000);
    patient.addRecord(0.9, "ECG", now + 4000);
    patient.addRecord(2.0, "ECG", now + 5000); // spike

    AlertStrategy strategy = new HeartRateStrategy();
    List<Alert> alerts = strategy.checkAlert(patient, now, now + 6000);

    assertFalse(alerts.isEmpty(), "Expected ECG spike to trigger alert");
    assertTrue(alerts.get(0).getMessage().contains("ECG"));
}


    @Test
    void testHeartRateStrategy_NoSpikeNoAlert() {
        Patient patient = buildPatientWithRecords("ECG", 0.8, 0.85, 0.9, 0.95, 1.0);
        AlertStrategy strategy = new HeartRateStrategy();
        List<Alert> alerts = strategy.checkAlert(patient, 0, System.currentTimeMillis());
        assertTrue(alerts.isEmpty());
    }

    @Test
    void testManualAlertStrategy() {
        Patient patient = new Patient(3);
        patient.addRecord(1.0, "Alert", System.currentTimeMillis());

        AlertStrategy strategy = new ManualAlertStrategy();
        List<Alert> alerts = strategy.checkAlert(patient, 0, System.currentTimeMillis());
        assertEquals(1, alerts.size());
        assertTrue(alerts.get(0).getMessage().contains("Manual"));
    }

    @Test
    void testManualAlertStrategy_NoTrigger() {
        Patient patient = new Patient(3);
        patient.addRecord(0.0, "Alert", System.currentTimeMillis());

        AlertStrategy strategy = new ManualAlertStrategy();
        List<Alert> alerts = strategy.checkAlert(patient, 0, System.currentTimeMillis());
        assertTrue(alerts.isEmpty());
    }

    @Test
    void testHypotensiveHypoxemiaStrategy() {
        Patient patient = new Patient(4);
        patient.addRecord(85.0, "SystolicBP", System.currentTimeMillis());
        patient.addRecord(90.0, "OxygenSaturation", System.currentTimeMillis());

        AlertStrategy strategy = new HypotensiveHypoxemiaStrategy();
        List<Alert> alerts = strategy.checkAlert(patient, 0, System.currentTimeMillis());
        assertEquals(1, alerts.size());
        assertTrue(alerts.get(0).getMessage().contains("Hypotensive"));
    }

    @Test
    void testHypotensiveHypoxemiaStrategy_OnlyOneLow_NoAlert() {
        Patient patient = new Patient(4);
        patient.addRecord(130.0, "SystolicBP", System.currentTimeMillis());
        patient.addRecord(89.0, "OxygenSaturation", System.currentTimeMillis());

        AlertStrategy strategy = new HypotensiveHypoxemiaStrategy();
        List<Alert> alerts = strategy.checkAlert(patient, 0, System.currentTimeMillis());
        assertTrue(alerts.isEmpty());
    }
}

