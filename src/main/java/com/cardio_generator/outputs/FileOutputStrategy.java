package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the OutputStrategy interface that stores patient data in files.
 *
 * <p>Each label (e.g., ECG, BloodPressure) corresponds to a separate file in the directory. 
 * Data ia appended to an existing file rather than loosing existing data. 
 *
 * <p> Uses a thread-safe ConcurrentHashMap to manage file paths based on the label provided. 
 *
 * <p>Example entry written to file:
 * <p>Patient ID: 6348120, Timestamp: 1718128230000, Label: BloodPressure, Data: 120/80
 *   
 * @author Nithessh Rajesh
 */

public class FileOutputStrategy implements OutputStrategy {

    //Changed variable name to Camel Case
    private String baseDirectory;

    
    
    /**
     * Constructs a FileOutputStrategy with the specified base directory.
     *
     * @param baseDirectory the directory where all output files will be written
     */
    //Changed variable name to Camel Case
    public FileOutputStrategy(String baseDirectory) {

        this.baseDirectory = baseDirectory;
    }

     //Changed variable name to Camel Case
     //Changed layout to match Constants → Fields → Constructors → Methods
     public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }

        // Set the FilePath variable
        // Renamed to Camel Case 
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

/**
     * Outputs the patient data to a file based on the label.
     *
     * <p>The file will be named (<label>.txt) and created inside the base directory.
     * It checks the existence of the file, if not creates or appends to the file accordingly. 
     * 
     * @param patientId the ID of the patient
     * @param timestamp The exact time of the data
     * @param label     the type of data being recorded (e.g., "ECG", "BloodPressure")
     * @param data      the actual value to write (e.g., "120/80")
     */

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}