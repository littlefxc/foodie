package com.example.disruptor;

import com.lmax.disruptor.EventHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiEventConsumer implements EventConsumer {

    private int expectedValue = -1;
    private int otherExpectedValue = -1;

    @Override
    @SuppressWarnings("unchecked")
    public EventHandler<ValueEvent>[] getEventHandler() {
        final EventHandler<ValueEvent> eventHandler = (event, sequence, endOfBatch) -> assertExpectedValue(event.getValue());
        final EventHandler<ValueEvent> otherEventHandler = (event, sequence, endOfBatch) -> assertOtherExpectedValue(event.getValue());
        return new EventHandler[] { eventHandler, otherEventHandler };
    }

    private void assertExpectedValue(final int id) {
        assertEquals(++expectedValue, id);
    }

    private void assertOtherExpectedValue(final int id) {
        assertEquals(++otherExpectedValue, id);
    }
}