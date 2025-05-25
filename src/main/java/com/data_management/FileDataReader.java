package com.data_management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.alerts.AlertGenerator;
import com.cardio_generator.outputs.ConsoleOutputStrategy;
import com.cardio_generator.outputs.OutputStrategy;

/**
 * The {@code FileDataReader} class implements the {@link DataReader} interface
 * to provide functionality for reading patient data from files.
 * <p>
 * It reads all supported files (.txt, .csv, .log) in a specified directory,
 * parses each line into patientId, timestamp, recordType, and measurementValue,
 * and adds them to the provided {@link DataStorage} singleton.
 * </p>
 *
 * @author Nithessh
 * @version 1.0
 */
public class FileDataReader implements DataReader {

    /**
     * Path to the directory containing data files.
     */
    private final String directoryPath;

    /**
     * Constructs a new {@code FileDataReader} for the given directory.
     *
     * @param directoryPath the path to the directory containing patient data files
     */
    public FileDataReader(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    /**
     * Reads all files in the configured directory and loads data into storage.
     * 
     * @param dataStorage the {@link DataStorage} singleton to populate with records
     * @throws IOException if the directory is invalid or files cannot be read
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Invalid directory: " + directoryPath);
        }

        File[] files = directory.listFiles((d, name) ->
            name.endsWith(".txt") || name.endsWith(".csv") || name.endsWith(".log")
        );
        if (files == null || files.length == 0) {
            throw new IOException("No data files found in directory: " + directoryPath);
        }

        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] segments = line.split(",");
                    if (segments.length != 4) {
                        System.err.println("Skipping malformed line: " + line);
                        continue;
                    }

                    try {
                        int patientId = Integer.parseInt(segments[0].trim());
                        long timestamp = Long.parseLong(segments[1].trim());
                        String recordType = segments[2].trim();
                        double measurementValue = Double.parseDouble(segments[3].trim());

                        dataStorage.addPatientData(
                            patientId, measurementValue, recordType, timestamp
                        );
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping line with invalid number: " + line);
                    }
                }
            }
        }
    }

    /**
     * Standalone main method for loading and evaluating patient data.
     * 
     * @param args command-line arguments (ignored)
     */
    public static void main(String[] args) {
        System.out.println("Loading patient data from output directory...");

        String path = "output";  // adjust if your files are elsewhere
        DataReader reader = new FileDataReader(path);

        // Obtain singleton instance and load data
        DataStorage storage = DataStorage.getInstance();
        storage.load(reader);

        // Setup alert evaluation
        OutputStrategy outputStrategy = new ConsoleOutputStrategy();
        AlertGenerator alertGenerator = new AlertGenerator(outputStrategy);

        // Evaluate all patients' data for alerts
        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }

        System.out.println("Evaluation completed.");
    }
}





