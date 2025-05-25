package com.data_management;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alerts.AlertGenerator;
import com.cardio_generator.outputs.ConsoleOutputStrategy;
import com.cardio_generator.outputs.OutputStrategy;

/**
 * Manages storage and retrieval of patient data within a healthcare monitoring
 * system. Implemented as a singleton.
 */
public class DataStorage {
    private static DataStorage instance;
    private final Map<Integer, Patient> patientMap;

    // Private constructor prevents external instantiation
    protected DataStorage() {
    this.patientMap = new HashMap<>();
    }

    /** 
     * Returns the singleton instance, creating it if necessary.
     */
    public static synchronized DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    /**
     * Loads data from the given reader into the singleton instance.
     */
    public void load(DataReader reader) {
        try {
            reader.readData(this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read data", e);
        }
    }

      public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        Patient patient = patientMap.computeIfAbsent(patientId, Patient::new);
        patient.addRecord(measurementValue, recordType, timestamp);
    }

    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);
        return (patient != null)
            ? patient.getRecords(startTime, endTime)
            : new ArrayList<>();
    }

    public List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    public static void main(String[] args) {
        // Use singleton to load and evaluate
        DataStorage storage = DataStorage.getInstance();
        // e.g. storage.load(new FileDataReader("output"));

        OutputStrategy output = new ConsoleOutputStrategy();
        AlertGenerator alerts = new AlertGenerator(output);

        for (Patient p : storage.getAllPatients()) {
            alerts.evaluateData(p);
        }
    }
}

