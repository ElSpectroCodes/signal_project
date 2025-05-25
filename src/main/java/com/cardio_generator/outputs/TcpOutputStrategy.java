package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Implementation of the OutputStrategy interface that sends data through a TCP socket.
 *
 * <p> It starts a TCP socket on the given port and listens for a single client connection. 
 * Once connected it sends streams to the client using a CSV format. 
 *
 * <p>Only one client connection is supported at a time in this implementation.
 *
 * <p>Example message format:
 * <pre>
 *     1,1716564300000,ECG,0.92
 * </pre>
 *   
 * @author Nithessh Rajesh
 */


public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;

      /**
     * Constructs the TCP output strategy and starts the server.
     *
     * @param port the TCP port to listen to incoming client 
     */

    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     /**
     *  patient data is sent to the client using a CSV format.
     *
     * @param patientId the ID of the patient
     * @param timestamp the time the data was generated 
     * @param label     the label describing the type of data (e.g., ECG, BP)
     * @param data      the actual patient data
     */

    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
