package com.alerts.decorator;

import com.alerts.Alert;
import com.cardio_generator.outputs.OutputStrategy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlertDecoratorTest {

    static class MockOutputStrategy implements OutputStrategy {
        List<String> logs = new ArrayList<>();
        @Override
        public void output(int patientId, long timestamp, String label, String data) {
            logs.add(label + ": " + data);
        }
    }

    @Test
    void testPriorityAlertDecoratorAddsPrefix() {
        Alert base = new Alert(1, 1000L, "Critical ECG spike");
        Alert decorated = new PriorityAlertDecorator(base);

        assertTrue(decorated.getMessage().startsWith("[HIGH PRIORITY]"));
        assertTrue(decorated.getMessage().contains("ECG spike"));
    }

    @Test
    void testRepeatedAlertDecoratorEmitsMultipleTimes() {
        Alert base = new Alert(2, 2000L, "BP above threshold");
        MockOutputStrategy output = new MockOutputStrategy();

        Alert repeated = new RepeatedAlertDecorator(base, 3);
        repeated.emit(output);

        assertEquals(3, output.logs.size());
        for (String entry : output.logs) {
            assertTrue(entry.contains("BP above threshold"));
        }
    }

    @Test
    void testRepeatedAlertDecoratorZeroRepeat() {
        Alert base = new Alert(3, 3000L, "Zero test");
        MockOutputStrategy output = new MockOutputStrategy();

        Alert repeated = new RepeatedAlertDecorator(base, 0);
        repeated.emit(output);

        assertEquals(0, output.logs.size());
    }

    @Test
    void testNestedDecorators() {
        Alert base = new Alert(4, 4000L, "Oxygen level drop");
        Alert decorated = new RepeatedAlertDecorator(new PriorityAlertDecorator(base), 2);
        MockOutputStrategy output = new MockOutputStrategy();

        decorated.emit(output);

        assertEquals(2, output.logs.size());
        for (String msg : output.logs) {
            assertTrue(msg.contains("[HIGH PRIORITY]"));
            assertTrue(msg.contains("Oxygen level drop"));
        }
    }
}

