package com.alerts.Alerts

import com.cardio_generator.outputs.OutputStrategy;
import com.data_management.Patient;


/**
 * Represents an alert triggered due to abnormal or critical patient data.
 * Each alert includes the patient ID, time of the alert, and a descriptive message.
 */
public class Alert {
    private final int patientId;
    private final long timestamp;
    private final String message;

    /**
     * Constructs a new Alert instance.
     *
     * @param patientId the ID of the patient the alert is related to
     * @param timestamp the time the alert was triggered
     * @param message   the description or reason for the alert
     */
    public Alert(int patientId, long timestamp, String message) {
        this.patientId = patientId;
        this.timestamp = timestamp;
        this.message = message;
    }

    public int getPatientId() {
        return patientId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("ALERT -> Patient ID: %d | Time: %d | Message: %s",
                patientId, timestamp, message);
    }

    /**
     * Outputs this alert using the provided OutputStrategy.
     *
     * @param outputStrategy the strategy used to output alert information
     */
    public void emit(OutputStrategy outputStrategy) {
        outputStrategy.output(patientId, timestamp, "Alert", message);
    }
}
