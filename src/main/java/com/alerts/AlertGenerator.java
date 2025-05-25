package com.alerts;

import com.cardio_generator.outputs.OutputStrategy;
import com.data_management.Patient;
import com.alerts.strategy.*;

import java.util.Arrays;
import java.util.List;


/**
 * The {@code AlertGenerator} class is responsible for evaluating patient data
 * and generating alerts based on predefined health rules. It uses a set of
 * {@link AlertStrategy} implementations to detect various conditions (e.g., critical blood pressure,
 * oxygen saturation drops, ECG anomalies).
 *
 * <p>It does not manage or access raw data directly; instead, it operates on individual
 * {@link com.data_management.Patient} objects and uses their recent records for analysis.
 *
 * <p>This design supports the Open/Closed Principle by allowing new alert strategies to be added
 * without modifying the AlertGenerator logic.
 */
public class AlertGenerator {
    private final List<AlertStrategy> strategies;
    private final OutputStrategy outputStrategy;

    /**
     * Constructs an {@code AlertGenerator} with the specified output strategy.
     * Initializes all alert strategies.
     *
     * @param outputStrategy how the alerts should be emitted
     */
    public AlertGenerator(OutputStrategy outputStrategy) {
        this.outputStrategy = outputStrategy;
        this.strategies = Arrays.asList(
                new BloodPressureStrategy(),
                new OxygenSaturationStrategy(),
                new HeartRateStrategy(),
                new HypotensiveHypoxemiaStrategy(),
                new ManualAlertStrategy()
        );
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert} method.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        long now = System.currentTimeMillis();
        long start = now - 10 * 60 * 1000; // last 10 minutes

        for (AlertStrategy strategy : strategies) {
            List<Alert> alerts = strategy.checkAlert(patient, start, now);
            for (Alert alert : alerts) {
                triggerAlert(alert);
            }
        }
    }

    /**
     * Triggers an alert for the monitoring system.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        alert.emit(outputStrategy);
    }
}
