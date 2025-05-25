package com.cardio_generator.outputs;

/**
 * Defines a strategy for outputting patient health data.
 *
 * <p>Implementing classes specify how and where the generated patient data
 * (such as ECG readings or blood pressure values) will be sent or displayed,
 * for example, to the console, a file, a WebSocket, or a TCP connection.</p>
 *
 * <p>Uses the Strategy design pattern</p>
 * 
 * @author Nithessh Rajesh
 */
public interface OutputStrategy {
    /**
     * Outputs patient data with metadata.
     *
     * @param patientId the identifier of the patient
     * @param timestamp the exact time when the data was generated 
     * @param label     a short label telling the data type 
     * @param data      the actual data value or values as a string
     */
    void output(int patientId, long timestamp, String label, String data);
}
