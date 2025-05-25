package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;


/**
 * Interface for generating patient data(high level module).
 * <p>
 * Implementing classes are responsible for producing approriate health related
 * data, such as ECG, blood pressure, etc... and make sure they use a suitable output 
 * for displaying and recording the data. 
 * </p>
 * 
 * @author Nithessh Rajesh
 */

public interface PatientDataGenerator {

     /**
     * Generates health data for a specific patient and outputs it using
     * the provided output strategy.
     *
     * @param patientId       The identifier of the patient.
     * @param outputStrategy  The solution used to output the generated data
     *                        
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
