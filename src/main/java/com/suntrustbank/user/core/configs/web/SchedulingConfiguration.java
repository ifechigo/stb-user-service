package com.suntrustbank.user.core.configs.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class SchedulingConfiguration {

    @Bean
    ApplicationEventMulticaster applicationEventMulticaster() {
        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
        multicaster.setTaskExecutor(new VirtualThreadTaskExecutor());
        return multicaster;
    }

}
