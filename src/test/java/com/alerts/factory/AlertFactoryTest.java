package com.alerts.factory;

import com.alerts.Alert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertFactoryTest {

    @Test
    void testBloodPressureAlertFactory() {
        AlertFactory factory = new BloodPressureAlertFactory();
        Alert alert = factory.createAlert(1, "High systolic value", 123456789L);

        assertEquals(1, alert.getPatientId());
        assertEquals(123456789L, alert.getTimestamp());
        assertTrue(alert.getMessage().startsWith("[Blood Pressure]"));
        assertTrue(alert.getMessage().contains("High systolic value"));
    }

    @Test
    void testBloodOxygenAlertFactory() {
        AlertFactory factory = new BloodOxygenAlertFactory();
        Alert alert = factory.createAlert(2, "Low saturation detected", 987654321L);

        assertEquals(2, alert.getPatientId());
        assertEquals(987654321L, alert.getTimestamp());
        assertTrue(alert.getMessage().startsWith("[Oxygen]"));
        assertTrue(alert.getMessage().contains("Low saturation detected"));
    }

    @Test
    void testECGAlertFactory() {
        AlertFactory factory = new ECGAlertFactory();
        Alert alert = factory.createAlert(3, "Irregular ECG detected", 111222333L);

        assertEquals(3, alert.getPatientId());
        assertEquals(111222333L, alert.getTimestamp());
        assertTrue(alert.getMessage().startsWith("[ECG]"));
        assertTrue(alert.getMessage().contains("Irregular ECG detected"));
    }
} 
