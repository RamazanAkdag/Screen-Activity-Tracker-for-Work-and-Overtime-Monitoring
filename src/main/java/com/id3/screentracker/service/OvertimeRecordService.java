package com.id3.screentracker.service;

import com.id3.screentracker.model.entity.OvertimeRecord;
import com.id3.screentracker.repository.IOvertimeRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OvertimeRecordService {

    private final IOvertimeRecordRepository overtimeRecordRepository;

    // Tüm OvertimeRecord kayıtlarını getirir
    public List<OvertimeRecord> getAllOvertimeRecords() {
        return overtimeRecordRepository.findAll();
    }

    // Bugüne ait OvertimeRecord kaydını getirir
    public OvertimeRecord getTodayOvertimeRecord() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        return overtimeRecordRepository.findByDateBetween(startOfDay, endOfDay);
    }

    // ID'ye göre tek bir OvertimeRecord kaydını getirir
    public OvertimeRecord getOvertimeRecordById(Long id) {
        return overtimeRecordRepository.findById(id).orElse(null);
    }
}