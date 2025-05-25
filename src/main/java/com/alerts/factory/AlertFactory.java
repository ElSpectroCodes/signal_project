package com.alerts.factory;

import com.alerts.Alert;

public abstract class AlertFactory {
    public abstract Alert createAlert(int patientId, String message, long timestamp);
}