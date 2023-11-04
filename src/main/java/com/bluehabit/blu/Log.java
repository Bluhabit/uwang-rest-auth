/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.blu;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class Log {

    @EventListener
    public void onStartup(ApplicationReadyEvent event) {
        final String url = event.getApplicationContext().getEnvironment().getProperty("spring.datasource.url");
        final String uname = event.getApplicationContext().getEnvironment().getProperty("spring.datasource.username");
        final String pwd = event.getApplicationContext().getEnvironment().getProperty("spring.datasource.password");

        log.error("URL => " + url);
        log.error("UNAME => " + uname);
        log.error("PWD => " + pwd);
    }

    @EventListener
    public void onShutdown(ContextStoppedEvent event) {
        final String url = event.getApplicationContext().getEnvironment().getProperty("spring.datasource.url");
        final String uname = event.getApplicationContext().getEnvironment().getProperty("spring.datasource.username");
        final String pwd = event.getApplicationContext().getEnvironment().getProperty("spring.datasource.password");

        log.error("URL => " + url);
        log.error("UNAME => " + uname);
        log.error("PWD => " + pwd);
    }

}
