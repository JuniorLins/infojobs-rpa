package com.rpa.infojobs.robot.listener;

import com.rpa.infojobs.robot.RobotEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RobotEventListener {

    @EventListener
    public void handleRobotEvent(RobotEvent event) {
        log.info("EVENTO [{}]: {}", event.getAction(), event.getDetails());
        // Aqui você pode salvar em uma tabela de logs no banco futuramente
    }
}
