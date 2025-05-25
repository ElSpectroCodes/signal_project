package com.data_management;

import java.io.*;

import com.alerts.AlertGenerator;
import com.cardio_generator.outputs.ConsoleOutputStrategy;
import com.cardio_generator.outputs.OutputStrategy;
/**
 * The {@code FileDataReader} class implements the {@link DataReader} interface
 * to provide functionality for reading data from files.
 * <p>
 * This class is responsible for handling file input operations and parsing
 * the data as required by the application.
 * </p>
 *
 * @author Nithessh Rajesh
 * @version 1.0
 */

 
public class FileDataReader implements DataReader {

    private final String directoryPath;

    
    /**
     * Constructs a new FileDataReader with the specified directory path.
     *
     * @param directoryPath the path to the directory containing the files to be read
     */
    public FileDataReader(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    
 @Override
    public void readData(DataStorage dataStorage) throws IOException {
        File directory = new File(directoryPath); 
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Invalid directory: " + directoryPath);
        }

        File[] files = directory.listFiles((d, name) -> name.endsWith(".txt") || name.endsWith(".csv") || name.endsWith(".log"));
            if (files == null || files.length == 0) {
                throw new IOException(" No files of requirement " + directoryPath);
            }

                for (File file : files) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;

                while ((line = reader.readLine()) != null) {
                    String[] segments = line.split(",");
                    if (segments.length != 4) {
                        System.err.println("Line is erroneous and skipped " + line);
                        continue;
                    }

                    try {
                        int patientId = Integer.parseInt(segments[0].trim());
                        long timestamp = Long.parseLong(segments[1].trim());
                        String recordType = segments[2].trim();
                        double measurementValue = Double.parseDouble(segments[3].trim());

                        dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);

                    } catch (NumberFormatException e) {
                        System.err.println(" Number is badly written" + line);
                    }
                }
            }
        }
    }

     public static void main(String[] args) {
        System.out.println("Loading patient data from output directory...");

        String path = "output";  // adjust if your files are elsewhere
        DataReader reader = new FileDataReader(path);
        DataStorage storage = new DataStorage(reader);

        OutputStrategy outputStrategy = new ConsoleOutputStrategy();
        AlertGenerator alertGenerator = new AlertGenerator(outputStrategy);

        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }

        System.out.println("Evaluation completed.");
    }
}






