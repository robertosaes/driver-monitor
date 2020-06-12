package com.bytesw.tyu.drivers.comcel.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.validation.Valid;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bytesw.tyu.drivers.comcel.config.Properties.As400System;
import com.bytesw.tyu.drivers.comcel.obj.Monitor;
import com.bytesw.tyu.drivers.connection.ConnectionManager;
import com.bytesw.tyu.drivers.connection.impl.DefaultConnectionManager;
import com.bytesw.tyu.drivers.queue.DefaultQueueManager;
import com.bytesw.tyu.drivers.queue.QueueManager;
import com.bytesw.tyu.drivers.queue.model.Queue;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {
    @Bean
    public ConnectionManager connectionManager(@Valid Properties properties) {
        As400System ref = properties.getAs400System();
        return new DefaultConnectionManager(ref.getSystemName(),
                ref.getUsername(), ref.getPassword());
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newWorkStealingPool();
    }

    @Bean
    public ExecutorService executorTasks() {
        return Executors.newWorkStealingPool();
    }

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(4);
    }

    @Bean
    public QueueManager<Queue> queueManager(
            ConnectionManager connectionManager, ExecutorService executorService) {
        return new DefaultQueueManager(connectionManager, executorService);
    }

    @Bean
    public Monitor v1ppctrlMonitor(JdbcTemplate jdbcTemplate, Properties properties) {
        return new Monitor(jdbcTemplate, properties);
    }
}
