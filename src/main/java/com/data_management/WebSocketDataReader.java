package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A real-time DataReader that connects to a WebSocket server to receive patient health data.
 * <p>
 * Uses logger to to show warnings
 * <p>
 * Messages are expected in the format: {@code patientId,recordType,value,timestamp}
 * <p>
 * For example: {@code 42,HeartRate,98.6,1690000000000}
 * <p>
 * Valid record types include: HeartRate, BloodPressure, ECG, OxygenSaturation, Alert.
 * <p>
 * This class handles reconnection attempts and malformed input gracefully.
 */

public class WebSocketDataReader implements DataReader {

    private static final Logger log = Logger.getLogger(WebSocketDataReader.class.getName());
    private final String serverUrl;
    private final Set<String> validRecordTypes = Set.of("HeartRate", "BloodPressure", "ECG", "OxygenSaturation", "Alert");

/**
 * 
 * Accepts the websockets serve's URL and stores it later
 *
 * @param serverUrl 
 */
    public WebSocketDataReader(String serverUrl) {
        this.serverUrl = serverUrl;
    }

 /**
 * Starts a WebSocket client to stream live data from the specified server URL.
 * If connection is lost, faulty or erroneous it will try to connect again. 
 *
 * @param storage The DataStorage system to which parsed records will be added.
 * @throws IOException if the WebSocket URI is invalid.
 */

    @Override
    public void streamData(DataStorage storage) throws IOException {
        try {
            URI uri = new URI(serverUrl);

            WebSocketClient client = new WebSocketClient(uri) {

                /**
                * Automatically called when connection is established
                */
                @Override
                public void onOpen(ServerHandshake handshake) {
                    log.info("WebSocket connected to " + serverUrl);
                }

                /**
                * Called when data is recived from websocket server
                *parses the string into a structured record 
                *taking care of unkown and bad names handling them.
                */
                @Override
                public void onMessage(String message) {
                    try {
                        PatientRecord record = parseMessage(message);
                        if (!validRecordTypes.contains(record.getRecordType())) {
                            log.warning("Skipped unknown record type: " + record.getRecordType());
                            return;
                        }

                        storage.addPatientData(
                            record.getPatientId(),
                            record.getMeasurementValue(),
                            record.getRecordType(),
                            record.getTimestamp()
                        );

                    } catch (IllegalArgumentException e) {
                        log.warning("Ignored malformed message: " + message);
                    } catch (Exception e) {
                        log.severe("Unexpected error during message parsing: " + e.getMessage());
                    }
                }

                /**
                * Called when the server closes. 
                */
                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info("WebSocket closed (code=" + code + ", reason=" + reason + ")");
                }

                /**
                * Catches socket failures or lower level socket errors 
                */
                @Override
                public void onError(Exception ex) {
                    log.severe("WebSocket error: " + ex.getMessage());
                }
            };

            client.connect();

            /**
            * This is a background thread that checks if the server is connected every 3 seconds 
            * and if not trys to reconnect 
            */            
            new Thread(() -> {
                int backoff = 2000;
                while (true) {
                    if (!client.isOpen()) {
                        try {
                            log.warning("Lost connection. Trying to reconnect...");
                            client.reconnectBlocking();
                            Thread.sleep(backoff);
                        } catch (InterruptedException e) {
                            log.info("Reconnect loop interrupted");
                            break;
                        } catch (Exception e) {
                            log.severe("Reconnect failed: " + e.getMessage());
                        }
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ignored) {}
                }
            }).start();

        /*
         * If the URL is invalid turns it into a readable IOexception
         */
        } catch (URISyntaxException e) {
            throw new IOException("Invalid WebSocket URL: " + serverUrl, e);
        }
    }

 /**
 * Parses a comma-separated string message into a {@link PatientRecord} object.
 * <p>
 * The expected format is:
 * <pre>{@code
 * patientId,recordType,measurementValue,timestamp
 * }</pre>
 * Example: {@code 42,HeartRate,98.6,1690000000000}
 * <p>
 * Each field is trimmed of whitespace. Numeric fields must be valid integers or doubles.
 *
 * @param msg the raw CSV message string received from the WebSocket server
 * @return a valid {@link PatientRecord} object with extracted fields
 * @throws IllegalArgumentException if the input does not contain exactly 4 parts
 *                                  or any numeric value fails to parse
 */
protected PatientRecord parseMessage(String msg) {

        String[] parts = msg.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Incorrect number of fields");
        }

        try {
            int id = Integer.parseInt(parts[0].trim());
            String type = parts[1].trim();
            double value = Double.parseDouble(parts[2].trim());
            long ts = Long.parseLong(parts[3].trim());

            return new PatientRecord(id, value, type, ts);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric field in message: " + msg);
        }
    }

    @Override
    public void readData(DataStorage storage) {
        throw new UnsupportedOperationException("This reader only supports streamData()");
    }
}


