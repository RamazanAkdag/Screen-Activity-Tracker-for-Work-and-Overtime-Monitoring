package com.id3.screentracker.service;

import com.id3.screentracker.model.entity.WorkHourRecord;
import com.id3.screentracker.repository.IWorkHourRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkHourRecordService {

    private final IWorkHourRecordRepository workHourRecordRepository;

    public List<WorkHourRecord> getAllWorkHourRecords() {
        return workHourRecordRepository.findAll();
    }

    public WorkHourRecord getTodayWorkHourRecord() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        return workHourRecordRepository.findByDateBetween(startOfDay, endOfDay);
    }

    public WorkHourRecord getWorkHourRecordById(Long id) {
        return workHourRecordRepository.findById(id).orElse(null);
    }
}