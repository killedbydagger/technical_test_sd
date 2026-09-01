package com.temp.demo.bean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
public class CustomScheduler {
    private final Logger logger = LogManager.getLogger(this);

    @Scheduled(fixedDelay = 3, timeUnit = TimeUnit.SECONDS)
    public void scheduleFixedDelay() {
        // do specified task here
    }

    @Scheduled(fixedRate = 3, timeUnit = TimeUnit.SECONDS)
    public void scheduleFixedRate() {
        // do specified task here
    }

    @Scheduled(cron = "00 00 01 ? * MON")
    public void scheduleCron() {
        // do specified task here
    }
}
