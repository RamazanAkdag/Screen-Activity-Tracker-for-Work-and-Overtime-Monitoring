package com.id3.screentracker.repository;

import com.id3.screentracker.model.entity.WorkHour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWorkHourRepository extends JpaRepository<WorkHour,Long> {
}
