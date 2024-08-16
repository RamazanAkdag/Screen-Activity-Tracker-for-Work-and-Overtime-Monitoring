package com.id3.screentracker.repository;

import com.id3.screentracker.model.entity.OvertimeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface IOvertimeRecordRepository extends JpaRepository<OvertimeRecord, Long> {
    OvertimeRecord findByDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    Optional<OvertimeRecord> findByDate(LocalDateTime localDateTime);
}