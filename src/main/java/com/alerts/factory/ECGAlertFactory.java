package com.alerts.factory;

import com.alerts.Alert;

public class ECGAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(int patientId, String message, long timestamp) {
        return new Alert(patientId, timestamp, "[ECG] " + message);
    }
}

