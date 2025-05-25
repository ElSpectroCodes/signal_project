package com.alerts.decorator;

import com.alerts.Alert;
import com.cardio_generator.outputs.OutputStrategy;

/**
 * Decorator that repeats alert emission a specified number of times.
 */
public class RepeatedAlertDecorator extends AlertDecorator {
    private final int repeatCount;

    public RepeatedAlertDecorator(Alert decoratedAlert, int repeatCount) {
        super(decoratedAlert);
        this.repeatCount = repeatCount;
    }

    @Override
    public void emit(OutputStrategy outputStrategy) {
        for (int i = 0; i < repeatCount; i++) {
            decoratedAlert.emit(outputStrategy);
        }
    }

}