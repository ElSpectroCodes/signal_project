package com;

import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataStorage;

import java.io.IOException;

/**
 * Entry point class for selecting which main application to run.
 *
 * Usage:
 * java -cp target/your-jar-name.jar com.Main [DataStorage]
 * If no argument is provided, HealthDataSimulator will be run by default.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equalsIgnoreCase("DataStorage")) {
            DataStorage.main(new String[]{});
        } else {
            HealthDataSimulator.main(new String[]{});
        }
    }
}

