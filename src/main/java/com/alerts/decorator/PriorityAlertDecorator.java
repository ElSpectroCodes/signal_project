package com.alerts.decorator;
import com.alerts.Alert;
import com.cardio_generator.outputs.OutputStrategy;

/**
 * Decorator that adds a HIGH PRIORITY tag to the alert message.
 */
public class PriorityAlertDecorator extends AlertDecorator {
    public PriorityAlertDecorator(Alert decoratedAlert) {
        super(decoratedAlert);
    }

    @Override
    public String getMessage() {
        return "[HIGH PRIORITY] " + decoratedAlert.getMessage();
    }

}
