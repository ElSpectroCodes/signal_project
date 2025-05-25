package com.alerts;

import com.alerts.rules.*;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlertRulesTest {

    private Patient patient;
    private long now;

    @BeforeEach
    void setup() {
        now = System.currentTimeMillis();
        patient = new Patient(1);
    }

    @Test
    void testBloodPressureTrendAlert() {
        patient.addRecord(110, "SystolicBP", now - 900000);
        patient.addRecord(123, "SystolicBP", now - 600000);
        patient.addRecord(137, "SystolicBP", now - 300000);

        AlertRule rule = new BloodPressureTrendRule();
        List<Alert> alerts = rule.evaluate(patient, now - 1000000, now);

        assertFalse(alerts.isEmpty());
        assertTrue(alerts.get(0).getMessage().contains("Systolic"));
    }

    @Test
    void testBloodPressureThresholdAlert() {
        patient.addRecord(185, "SystolicBP", now);

        AlertRule rule = new BloodPressureThresholdRule();
        List<Alert> alerts = rule.evaluate(patient, now - 5000, now + 5000);

        assertEquals(1, alerts.size());
        assertTrue(alerts.get(0).getMessage().contains("Critical systolic"));
    }

    @Test
    void testSaturationAlert() {
        patient.addRecord(89, "OxygenSaturation", now);

        AlertRule rule = new SaturationAlertRule();
        List<Alert> alerts = rule.evaluate(patient, now - 10000, now);

        assertEquals(1, alerts.size());
        assertTrue(alerts.get(0).getMessage().contains("Low"));
    }

    @Test
    void testRapidDropAlert() {
        patient.addRecord(97, "OxygenSaturation", now - 500000);
        patient.addRecord(91.5, "OxygenSaturation", now - 200000);

        AlertRule rule = new RapidDropRule();
        List<Alert> alerts = rule.evaluate(patient, now - 600000, now);

        assertEquals(1, alerts.size());
        assertTrue(alerts.get(0).getMessage().contains("drop"));
    }

    @Test
    void testHypotensiveHypoxemiaAlert() {
        patient.addRecord(85, "SystolicBP", now);
        patient.addRecord(90, "OxygenSaturation", now);

        AlertRule rule = new HypotensiveHypoxemiaRule();
        List<Alert> alerts = rule.evaluate(patient, now - 5000, now);

        assertEquals(1, alerts.size());
        assertTrue(alerts.get(0).getMessage().contains("Hypotensive"));
    }

    @Test
    void testECGAnomalyAlert() {
        patient.addRecord(1.0, "ECG", now - 60000);
        patient.addRecord(1.1, "ECG", now - 50000);
        patient.addRecord(1.2, "ECG", now - 40000);
        patient.addRecord(1.1, "ECG", now - 30000);
        patient.addRecord(1.0, "ECG", now - 20000);
        patient.addRecord(2.0, "ECG", now - 10000);

        AlertRule rule = new ECGAnomalyRule();
        List<Alert> alerts = rule.evaluate(patient, now - 70000, now);

        assertEquals(1, alerts.size());
        assertTrue(alerts.get(0).getMessage().contains("ECG"));
    }

    @Test
    void testManualAlert() {
        patient.addRecord(0, "Alert", now); 

        AlertRule rule = new ManualAlertRule();
        List<Alert> alerts = rule.evaluate(patient, now - 10000, now);
        assertTrue(alerts.isEmpty()); 
}

}
