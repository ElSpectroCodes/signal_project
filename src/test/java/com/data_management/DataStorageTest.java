package com.data_management;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;

class DataStorageTest {

    private DataStorage storage;

    @BeforeEach
    void setUp() {
        storage = DataStorage.getInstance();
        
    }

    @Test
    void testAddAndGetRecords() {
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records =
            storage.getRecords(1, 1714376789050L, 1714376789051L);

        assertEquals(2, records.size(), "Should retrieve both records");
        assertEquals(100.0, records.get(0).getMeasurementValue(),
                     "First record should match inserted value");
    }
}