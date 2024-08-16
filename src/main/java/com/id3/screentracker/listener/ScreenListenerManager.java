package com.id3.screentracker.listener;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.id3.screentracker.model.entity.OvertimeRecord;
import com.id3.screentracker.model.entity.WorkHour;
import com.id3.screentracker.model.entity.WorkHourRecord;
import com.id3.screentracker.repository.IOvertimeRecordRepository;
import com.id3.screentracker.repository.IWorkHourRecordRepository;
import com.id3.screentracker.service.IWorkHourService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
@Service
@Slf4j
@RequiredArgsConstructor
public class ScreenListenerManager implements IListenerService {

    private final IWorkHourRecordRepository workHourRecordRepository;
    private final IOvertimeRecordRepository overtimeRecordRepository;
    private final IWorkHourService workHourService;

    private ScheduledExecutorService scheduler;
    private LocalDateTime passiveStartTime;
    private WorkHourRecord currentWorkRecord;
    private OvertimeRecord currentOvertimeRecord;
    private boolean running = false;

    @Override
    public void startListening() {
        if (!running) {
            log.info("Listener started...");
            running = true;
            scheduler = Executors.newScheduledThreadPool(1); // Yeni scheduler oluştur
            WorkHour workHour = workHourService.getWorkHour();
            LocalDateTime now = LocalDateTime.now();

            if (isWithinWorkHours(now, workHour)) {
                currentWorkRecord = workHourRecordRepository.findByDate(now.toLocalDate().atStartOfDay()).orElse(new WorkHourRecord());
                currentWorkRecord.setDate(now.toLocalDate().atStartOfDay());

                if (currentWorkRecord.getStartTime() == null) {
                    currentWorkRecord.setStartTime(now);
                }

                workHourRecordRepository.save(currentWorkRecord);
            } else {
                currentOvertimeRecord = overtimeRecordRepository.findByDate(now.toLocalDate().atStartOfDay()).orElse(new OvertimeRecord());
                currentOvertimeRecord.setDate(now.toLocalDate().atStartOfDay());


                if (currentOvertimeRecord.getStartTime() == null) {
                    currentOvertimeRecord.setStartTime(now);
                }

                overtimeRecordRepository.save(currentOvertimeRecord);
            }

            startInputListenning();
            startPassiveModeCheck();
        }
    }

    public void stopListening() {
        if (running) {
            log.info("Listener stopped...");
            running = false;
            scheduler.shutdownNow();
            onShutdown();
        }
    }

    public boolean isRunning() {
        return running;
    }

    private void startInputListenning() {
        try {

            if (!GlobalScreen.isNativeHookRegistered()) {
                GlobalScreen.registerNativeHook();
            }
        } catch (NativeHookException ex) {
            log.error("There was a problem registering the native hook.", ex);
            System.exit(1);
        }

        GlobalMouseListener mouseListener = new GlobalMouseListener();
        GlobalScreen.addNativeMouseListener(mouseListener);
        GlobalScreen.addNativeMouseMotionListener(mouseListener);
        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
    }

    private void startPassiveModeCheck() {
        scheduler.scheduleAtFixedRate(() -> {
            LocalDateTime now = LocalDateTime.now();
            long idleTime = Duration.between(LastActivityTime.getLastActivityTime(), now).getSeconds();

            if (idleTime >= 300) {
                if (passiveStartTime == null) {
                    startPassiveMode();
                }
            } else if (passiveStartTime != null) {

                endPassiveMode();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void startPassiveMode() {
        passiveStartTime = LocalDateTime.now();
        log.info("Entered passive mode at {}", passiveStartTime);

    }

    private void endPassiveMode() {
        LocalDateTime now = LocalDateTime.now();
        long passiveDuration = Duration.between(passiveStartTime, now).getSeconds();
        log.info("Exited passive mode. Passive duration: {} seconds", passiveDuration);

        WorkHour workHour = workHourService.getWorkHour();

        if (workHour != null && isWithinWorkHours(now, workHour)) {

            currentWorkRecord = workHourRecordRepository.findById(currentWorkRecord.getId())
                    .orElse(currentWorkRecord);

            if (currentWorkRecord != null) {
                currentWorkRecord.setPassiveDuration(currentWorkRecord.getPassiveDuration() + passiveDuration);
                workHourRecordRepository.save(currentWorkRecord);
            }
        } else {

            currentOvertimeRecord = overtimeRecordRepository.findById(currentOvertimeRecord.getId())
                    .orElse(currentOvertimeRecord);

            if (currentOvertimeRecord != null) {
                currentOvertimeRecord.setPassiveDuration(currentOvertimeRecord.getPassiveDuration() + passiveDuration);
                overtimeRecordRepository.save(currentOvertimeRecord);
            }
        }

        passiveStartTime = null;
    }

    @PreDestroy
    public void onShutdown() {
        log.info("Application is shutting down...");

        LocalDateTime now = LocalDateTime.now();
        long passiveDuration = (passiveStartTime != null) ? Duration.between(passiveStartTime, now).getSeconds() : 0;

        if (currentWorkRecord != null) {

            currentWorkRecord = workHourRecordRepository.findById(currentWorkRecord.getId())
                    .orElse(currentWorkRecord);

            long activeDuration = Duration.between(currentWorkRecord.getStartTime(), now).getSeconds() - currentWorkRecord.getPassiveDuration();
            currentWorkRecord.setEndTime(now);
            currentWorkRecord.setActiveDuration(activeDuration);
            currentWorkRecord.setPassiveDuration(currentWorkRecord.getPassiveDuration() + passiveDuration);
            workHourRecordRepository.save(currentWorkRecord);

            log.info("Work day summary:");
            log.info("Date: {}", currentWorkRecord.getDate());
            log.info("Start Time: {}", currentWorkRecord.getStartTime());
            log.info("End Time: {}", currentWorkRecord.getEndTime());
            log.info("Active Duration: {} seconds", activeDuration);
            log.info("Passive Duration: {} seconds", currentWorkRecord.getPassiveDuration());
        } else if (currentOvertimeRecord != null) {

            currentOvertimeRecord = overtimeRecordRepository.findById(currentOvertimeRecord.getId())
                    .orElse(currentOvertimeRecord);

            long activeDuration = Duration.between(currentOvertimeRecord.getStartTime(), now).getSeconds() - currentOvertimeRecord.getPassiveDuration();
            currentOvertimeRecord.setEndTime(now);
            currentOvertimeRecord.setActiveDuration(activeDuration);
            currentOvertimeRecord.setPassiveDuration(currentOvertimeRecord.getPassiveDuration() + passiveDuration);
            overtimeRecordRepository.save(currentOvertimeRecord);

            log.info("Overtime summary:");
            log.info("Date: {}", currentOvertimeRecord.getDate());
            log.info("Start Time: {}", currentOvertimeRecord.getStartTime());
            log.info("End Time: {}", currentOvertimeRecord.getEndTime());
            log.info("Active Duration: {} seconds", activeDuration);
            log.info("Passive Duration: {} seconds", currentOvertimeRecord.getPassiveDuration());
        }
    }

    private boolean isWithinWorkHours(LocalDateTime now, WorkHour workHour) {

        LocalTime currentTime = now.toLocalTime();

        LocalTime workStartTime = workHour.getStartTime();
        LocalTime workEndTime = workHour.getEndTime();


        if (workEndTime.isBefore(workStartTime)) {
            return currentTime.isAfter(workStartTime) || currentTime.isBefore(workEndTime);
        } else {
            return !currentTime.isBefore(workStartTime) && !currentTime.isAfter(workEndTime);
        }
    }


    public String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}
