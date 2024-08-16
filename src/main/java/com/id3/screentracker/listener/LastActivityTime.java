package com.id3.screentracker.listener;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;


@Slf4j
public class LastActivityTime {
    @Getter
    private static LocalDateTime lastActivityTime = LocalDateTime.now();

    public static void resetLastActivityTime(){
        lastActivityTime = LocalDateTime.now();
        log.info("Activity detected, resetting idle timer.");
    }

}
