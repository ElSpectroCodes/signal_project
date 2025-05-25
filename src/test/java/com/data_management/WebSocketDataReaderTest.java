package com.data_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;
import com.data_management.WebSocketDataReader;
import com.data_management.PatientRecord;

/**
 * Unit tests for WebSocketDataReader message parsing.
 */
public class WebSocketDataReaderTest {

    private WebSocketDataReader reader;

    @BeforeEach
    public void setup() {
        reader = new WebSocketDataReader("ws://localhost:8080");
    }

    @Test
    public void testValidMessageParsesCorrectly() {
        PatientRecord record = readerTestHelper("42,HeartRate,98.6,1690000000000");
        assertEquals(42, record.getPatientId());
        assertEquals("HeartRate", record.getRecordType());
        assertEquals(98.6, record.getMeasurementValue(), 0.001);
        assertEquals(1690000000000L, record.getTimestamp());
    }

    @Test
    public void testMessageWithExtraSpaces() {
        PatientRecord record = readerTestHelper("  7 , ECG , 0.76 , 1689999999999 ");
        assertEquals(7, record.getPatientId());
        assertEquals("ECG", record.getRecordType());
        assertEquals(0.76, record.getMeasurementValue(), 0.001);
        assertEquals(1689999999999L, record.getTimestamp());
    }

    @Test
    public void testMessageTooFewFields() {
        assertThrows(IllegalArgumentException.class, () ->
                readerTestHelper("42,HeartRate,98.6"));
    }

    @Test
    public void testMessageTooManyFields() {
        assertThrows(IllegalArgumentException.class, () ->
                readerTestHelper("42,HeartRate,98.6,1690000000000,extra"));
    }

    @Test
    public void testNonNumericPatientId() {
        assertThrows(IllegalArgumentException.class, () ->
                readerTestHelper("abc,HeartRate,98.6,1690000000000"));
    }

    @Test
    public void testNonNumericValue() {
        assertThrows(IllegalArgumentException.class, () ->
                readerTestHelper("42,HeartRate,nan,1690000000000"));
    }

    @Test
    public void testNonNumericTimestamp() {
        assertThrows(IllegalArgumentException.class, () ->
                readerTestHelper("42,HeartRate,98.6,notatime"));
    }

    // Helper method to call the private parseMessage() using a subclass
    private PatientRecord readerTestHelper(String msg) {
        return new WebSocketDataReader("ws://test") {
            public PatientRecord testParse(String input) {
                return super.parseMessage(input);
            }
        }.testParse(msg);
    }
}
