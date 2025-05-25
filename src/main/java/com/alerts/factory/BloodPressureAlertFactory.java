package com.alerts.factory;

import com.alerts.Alert;

public class BloodPressureAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(int patientId, String message, long timestamp) {
        return new Alert(patientId, timestamp, "[Blood Pressure] " + message);
    }

}