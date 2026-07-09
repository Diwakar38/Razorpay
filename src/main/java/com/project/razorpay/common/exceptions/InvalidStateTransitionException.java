package com.project.razorpay.common.exceptions;

import lombok.Getter;

@Getter
public class InvalidStateTransitionException extends RuntimeException {

    private final String fromState;
    private final String toEvent;

    public InvalidStateTransitionException(String fromState, String toEvent) {
        super("Invalid transition from " + fromState + " with event " + toEvent);
        this.fromState = fromState;
        this.toEvent = toEvent;
    }
}
