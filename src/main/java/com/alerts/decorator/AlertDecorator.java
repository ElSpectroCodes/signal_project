package com.alerts.decorator;

import com.alerts.Alert;
import com.cardio_generator.outputs.OutputStrategy;

/**
 * Base decorator for Alert, allowing additional behavior to be added dynamically.
 */
public abstract class AlertDecorator extends Alert {
    protected final Alert decoratedAlert;

    public AlertDecorator(Alert decoratedAlert) {
        super(decoratedAlert.getPatientId(), decoratedAlert.getTimestamp(), decoratedAlert.getMessage());
        this.decoratedAlert = decoratedAlert;
    }

    @Override
    public int getPatientId() {
        return decoratedAlert.getPatientId();
    }

    @Override
    public long getTimestamp() {
        return decoratedAlert.getTimestamp();
    }

    @Override
    public String getMessage() {
        return decoratedAlert.getMessage();
    }

    @Override
    public String toString() {
        return decoratedAlert.toString();
    }

    @Override
public void emit(OutputStrategy outputStrategy) {
    outputStrategy.output(
        getPatientId(),
        getTimestamp(),
        "Priority Alert",
        getMessage()  // now uses decorated version
    );
}

}