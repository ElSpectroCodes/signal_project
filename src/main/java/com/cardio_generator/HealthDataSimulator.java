package com.cardio_generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cardio_generator.generators.AlertGenerator;
import com.cardio_generator.generators.BloodLevelsDataGenerator;
import com.cardio_generator.generators.BloodPressureDataGenerator;
import com.cardio_generator.generators.BloodSaturationDataGenerator;
import com.cardio_generator.generators.ECGDataGenerator;
import com.cardio_generator.outputs.ConsoleOutputStrategy;
import com.cardio_generator.outputs.FileOutputStrategy;
import com.cardio_generator.outputs.OutputStrategy;
import com.cardio_generator.outputs.TcpOutputStrategy;
import com.cardio_generator.outputs.WebSocketOutputStrategy;

/**
 * Generates real-time health data for patients and outputs it via
 * console, file, TCP, or WebSocket. Implemented as a singleton
 * to ensure one global simulation instance.
 *
 * Usage:
 * java HealthDataSimulator --patient-count 100 --output websocket:8080
 *
 * @author Nithessh Rajesh
 */
public class HealthDataSimulator {
    private static HealthDataSimulator instance;

    private int patientCount = 50;
    private ScheduledExecutorService scheduler;
    private OutputStrategy outputStrategy = new ConsoleOutputStrategy();
    private final Random random = new Random();

    /**
     * Private constructor to prevent direct instantiation.
     */
    private HealthDataSimulator() {}

    /**
     * Returns the singleton instance, creating it if necessary.
     */
    public static synchronized HealthDataSimulator getInstance() {
        if (instance == null) {
            instance = new HealthDataSimulator();
        }
        return instance;
    }

    /**
     * Starts the simulation with the given command-line arguments.
     *
     * @param args Command-line arguments
     * @throws IOException if output directory creation fails
     */
    public void start(String[] args) throws IOException {
        parseArguments(args);
        scheduler = Executors.newScheduledThreadPool(patientCount * 4);

        List<Integer> patientIds = initializePatientIds(patientCount);
        Collections.shuffle(patientIds);
        scheduleTasksForPatients(patientIds);
    }

    /**
     * Main entry point; delegates to singleton.
     */
    public static void main(String[] args) throws IOException {
        getInstance().start(args);
    }

    /**
     * Parses command-line arguments to configure patientCount and outputStrategy.
     */
    private void parseArguments(String[] args) throws IOException {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                    printHelp();
                    System.exit(0);
                    break;
                case "--patient-count":
                    if (i + 1 < args.length) {
                        try {
                            patientCount = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid patient count, using default: " + patientCount);
                        }
                    }
                    break;
                case "--output":
                    if (i + 1 < args.length) {
                        String outputArg = args[++i];
                        configureOutput(outputArg);
                    }
                    break;
                default:
                    System.err.println("Unknown option '" + args[i] + "'");
                    printHelp();
                    System.exit(1);
            }
        }
    }

    /**
     * Configures the output strategy based on the argument.
     */
    private void configureOutput(String outputArg) throws IOException {
        if ("console".equals(outputArg)) {
            outputStrategy = new ConsoleOutputStrategy();
        } else if (outputArg.startsWith("file:")) {
            String dir = outputArg.substring(5);
            Path path = Paths.get(dir);
            if (!Files.exists(path)) Files.createDirectories(path);
            outputStrategy = new FileOutputStrategy(dir);
        } else if (outputArg.startsWith("websocket:")) {
            int port = Integer.parseInt(outputArg.substring(10));
            outputStrategy = new WebSocketOutputStrategy(port);
        } else if (outputArg.startsWith("tcp:")) {
            int port = Integer.parseInt(outputArg.substring(4));
            outputStrategy = new TcpOutputStrategy(port);
        } else {
            System.err.println("Unknown output type, using console.");
        }
    }

    /**
     * Prints usage help information.
     */
    private void printHelp() {
        System.out.println("Usage: java HealthDataSimulator [options]");
        System.out.println("  -h                       Show help");
        System.out.println("  --patient-count <count>  Number of patients (default: 50)");
        System.out.println("  --output <type>          console | file:<dir> | websocket:<port> | tcp:<port>");
    }

    /**
     * Initializes patient IDs from 1 to count.
     */
    private List<Integer> initializePatientIds(int count) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 1; i <= count; i++) ids.add(i);
        return ids;
    }

    /**
     * Schedules data generation tasks for each patient.
     */
    private void scheduleTasksForPatients(List<Integer> patientIds) {
        ECGDataGenerator ecgGen = new ECGDataGenerator(patientCount);
        BloodSaturationDataGenerator satGen = new BloodSaturationDataGenerator(patientCount);
        BloodPressureDataGenerator bpGen = new BloodPressureDataGenerator(patientCount);
        BloodLevelsDataGenerator lvlGen = new BloodLevelsDataGenerator(patientCount);
        AlertGenerator alertGen = new AlertGenerator(patientCount);

        for (int id : patientIds) {
            scheduleTask(() -> ecgGen.generate(id, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> satGen.generate(id, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> bpGen.generate(id, outputStrategy), 1, TimeUnit.MINUTES);
            scheduleTask(() -> lvlGen.generate(id, outputStrategy), 2, TimeUnit.MINUTES);
            scheduleTask(() -> alertGen.generate(id, outputStrategy), 20, TimeUnit.SECONDS);
        }
    }

    /**
     * Schedules a repeating task with randomized initial delay.
     */
    private void scheduleTask(Runnable task, long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(task, random.nextInt(5), period, unit);
    }
}
