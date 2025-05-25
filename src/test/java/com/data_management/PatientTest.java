package com.data_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PatientTest {

    private Patient patient;
    private long now;

    @BeforeEach
    void setUp() {
        now = System.currentTimeMillis();
        patient = new Patient(101);
    }

    @Test
    void testAddAndRetrieveRecordInRange() {
        long timestamp1 = now - 5000;
        long timestamp2 = now;

        patient.addRecord(98.6, "Temperature", timestamp1);
        patient.addRecord(120, "SystolicBP", timestamp2);

        List<PatientRecord> records = patient.getRecords(now - 10000, now + 10000);

        assertEquals(2, records.size(), "Should return both records within the range");
    }

    @Test
    void testRetrieveRecordOutOfRange() {
        long timestamp = now - 20000;
        patient.addRecord(72, "HeartRate", timestamp);

        List<PatientRecord> records = patient.getRecords(now - 10000, now);

        assertTrue(records.isEmpty(), "Should return no records outside the range");
    }

    @Test
    void testGetPatientId() {
        assertEquals(101, patient.getPatientId(), "Patient ID should match the initialized value");
    }
}
