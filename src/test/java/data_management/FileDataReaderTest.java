package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;

import java.util.List;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.junit.jupiter.api.*;

class FileDataReaderTest {

    private Path tempDir;

    @BeforeEach
    void setup() throws IOException {
        tempDir = Files.createTempDirectory("datareader_test");
    }

    @AfterEach
    void cleanup() throws IOException {
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }

    @Test
    void testReadData_validFile() throws IOException {
        // Create a sample data file with correct format
        Path file = tempDir.resolve("sample.csv");
        String content = 
            "123, 1625097600000, HeartRate, 72.5\n" +
            "123, 1625097660000, BloodPressure, 120.0\n";
        Files.writeString(file, content);

        // Create a simple DataStorage mock to capture data added
        class DataStorageMock extends DataStorage {
            List<String> addedData = new ArrayList<>();

            @Override
            public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
                addedData.add(patientId + "," + timestamp + "," + recordType + "," + measurementValue);
            }
        }

        DataStorageMock mockStorage = new DataStorageMock();

        FileDataReader reader = new FileDataReader(tempDir.toString());
        reader.readData(mockStorage);

        // We expect 2 records added
        assertEquals(2, mockStorage.addedData.size());
        assertTrue(mockStorage.addedData.contains("123,1625097600000,HeartRate,72.5"));
        assertTrue(mockStorage.addedData.contains("123,1625097660000,BloodPressure,120.0"));
    }

    @Test
    void testReadData_invalidLinesAreSkipped() throws IOException {
        Path file = tempDir.resolve("bad_data.csv");
        String content =
            "123, 1625097600000, HeartRate, 72.5\n" +
            "bad,line,without,proper,fields\n" + // 5 fields, invalid
            "124, notANumber, BloodPressure, 120.0\n"; // timestamp invalid
        Files.writeString(file, content);

        class DataStorageMock extends DataStorage {
            List<String> addedData = new ArrayList<>();

            @Override
            public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
                addedData.add(patientId + "," + timestamp + "," + recordType + "," + measurementValue);
            }
        }

        DataStorageMock mockStorage = new DataStorageMock();
        FileDataReader reader = new FileDataReader(tempDir.toString());

        reader.readData(mockStorage);

        // Only the first valid line should be added
        assertEquals(1, mockStorage.addedData.size());
        assertTrue(mockStorage.addedData.contains("123,1625097600000,HeartRate,72.5"));
    }

    @Test
    void testReadData_invalidDirectory() {
        FileDataReader reader = new FileDataReader("non_existing_dir_12345");

        DataStorage dummyStorage = new DataStorage() {
            @Override
            public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
                // no-op
            }
        };

        IOException thrown = assertThrows(IOException.class, () -> {
            reader.readData(dummyStorage);
        });

        assertTrue(thrown.getMessage().contains("Invalid directory"));
    }

    @Test
    void testReadData_noFilesInDirectory() throws IOException {
        // Create empty temp directory with no files
        FileDataReader reader = new FileDataReader(tempDir.toString());

        DataStorage dummyStorage = new DataStorage() {
            @Override
            public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
                // no-op
            }
        };

        IOException thrown = assertThrows(IOException.class, () -> {
            reader.readData(dummyStorage);
        });

        assertTrue(thrown.getMessage().contains("No files"));
    }
}

