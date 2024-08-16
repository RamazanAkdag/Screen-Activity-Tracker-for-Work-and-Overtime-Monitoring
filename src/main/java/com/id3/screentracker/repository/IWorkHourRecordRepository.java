package com.id3.screentracker.repository;

import com.id3.screentracker.model.entity.WorkHourRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface IWorkHourRecordRepository extends JpaRepository<WorkHourRecord, Long> {
    WorkHourRecord findByDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    Optional<WorkHourRecord> findByDate(LocalDateTime localDateTime);
}