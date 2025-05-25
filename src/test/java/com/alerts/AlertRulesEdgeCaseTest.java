package com.alerts;

import com.alerts.rules.*;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlertRulesEdgeCaseTest {

    private Patient patient;
    private long now;

    @BeforeEach
    void setup() {
        now = System.currentTimeMillis();
        patient = new Patient(99);
    }

    @Test
    void testHealthyPatientNoAlerts() {
        patient.addRecord(120, "SystolicBP", now);
        patient.addRecord(80, "DiastolicBP", now);
        patient.addRecord(98, "OxygenSaturation", now);
        patient.addRecord(1.0, "ECG", now);

        List<AlertRule> rules = List.of(
                new BloodPressureTrendRule(),
                new BloodPressureThresholdRule(),
                new SaturationAlertRule(),
                new RapidDropRule(),
                new HypotensiveHypoxemiaRule(),
                new ECGAnomalyRule(),
                new ManualAlertRule()
        );

        for (AlertRule rule : rules) {
            assertTrue(rule.evaluate(patient, now - 10000, now).isEmpty(),
                    "Expected no alerts for healthy patient from rule: " + rule.getClass().getSimpleName());
        }
    }

    @Test
    void testDiastolicThresholdTrigger() {
        patient.addRecord(130, "DiastolicBP", now);

        AlertRule rule = new BloodPressureThresholdRule();
        List<Alert> alerts = rule.evaluate(patient, now - 5000, now);

        assertEquals(1, alerts.size());
        assertTrue(alerts.get(0).getMessage().contains("Critical diastolic"));
    }

    @Test
    void testDecreasingTrendTriggersAlert() {
        patient.addRecord(150, "SystolicBP", now - 900000);
        patient.addRecord(135, "SystolicBP", now - 600000);
        patient.addRecord(120, "SystolicBP", now - 300000);

        AlertRule rule = new BloodPressureTrendRule();
        List<Alert> alerts = rule.evaluate(patient, now - 1000000, now);

        assertEquals(1, alerts.size());
        assertTrue(alerts.get(0).getMessage().contains("Systolic"));
    }

    @Test
    void testManualAlertWithNumericTrigger() {
        patient.addRecord(1.0, "Alert", now);

        AlertRule rule = new ManualAlertRule();
        List<Alert> alerts = rule.evaluate(patient, now - 10000, now);

        assertEquals(1, alerts.size());
        assertTrue(alerts.get(0).getMessage().contains("Manual alert triggered"));
    }
}