package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates and alrert stauts for each invidual to simulate unusual health conditions.
 *
 * <p>Alerts simulate health-related issues and can be "triggered" or "resolved".
 *
 * <p>Each patient has an alert state which can be triggered:
 * - If there is an alert currently enabled there is 90 percent chance it will be resolved.
 * - If no alert is triggered there is a small chance it can be triggered using a Poision
 * distribution with a configurable Lambda value. 
 *
 * @author Nithessh Rajesh
 */

public class AlertGenerator implements PatientDataGenerator {

    // Renamed to ALL_CAPS_WITH_UNDERSCORES for constants
    public static final Random RANDOM_GENERATOR = new Random();

    //Renamed to Camel CAse
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Constructs an AlertGenerator for the specified number of patients.
     *
     * @param patientCount the number of patients to track alerts for
     */ 

    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

     /**
     * Generates alert data which can either be triggered or resolved. 
     *
     * @param patientId      the ID of the patient
     * @param outputStrategy the strategy used to output the alert data
     */

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {

                //Changed to Camel Case
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
