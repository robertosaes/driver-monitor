package com.bytesw.tyu.drivers.comcel.btf;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.bytesw.tyu.drivers.comcel.task.ScheduledTask;

import lombok.extern.slf4j.Slf4j;

/**
 * Run the spring boot application main thread.
 * 
 * @author csosa
 */
@Slf4j
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.bytesw")
public class BTFManager {
    public static void main(String[] args) {
        new SpringApplicationBuilder(BTFManager.class)
                .addCommandLineProperties(true).bannerMode(Mode.CONSOLE)
                .headless(true).registerShutdownHook(true).logStartupInfo(true)
                .run(args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent application)
            throws InterruptedException {
        log.info("BTF scheduled task is running.");
        application.getApplicationContext().getBean(ScheduledTask.class).run();
    }

    @EventListener(ApplicationFailedEvent.class)
    public void onApplicationFailed() {
        log.info("SpringBoot application is not running.");
    }
}
