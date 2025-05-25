package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates blood saturation levels for each patients.
 *
 * <p>This class produces blood oxygen saturation levels (SpO2) for each patient,
 * Saturation values are restricted between 90% and 100% to make sure realistic values are generated.
 *
 * <p>Each patient is initialized with a baseline saturation between 95% and 100%.
 * The generator then causes mild fluxtuations to simulate natural bilogical behavior. 
 *
 * 
 * <p>Example output: {@code Patient ID: 6348120, Timestamp: 1716572300000, Label: Saturation, Data: 97%}
 * 
 * @author Nithessh Rajesh
 */

public class BloodSaturationDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private int[] lastSaturationValues;

    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }

      /**
     * Generates and outputs a new saturation value for the specified patient.
     *
     * @param patientId      the ID of the patient
     * @param outputStrategy the strategy used to output the data
     */

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
