package com.rpa.infojobs.robot;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RobotEvent extends ApplicationEvent {

    private final String action;
    private final String details;

    public RobotEvent(Object source, String action, String details) {
        super(source);
        this.action = action;
        this.details = details;
    }
}
