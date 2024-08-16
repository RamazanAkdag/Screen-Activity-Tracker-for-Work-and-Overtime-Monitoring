package com.id3.screentracker.service;

import com.id3.screentracker.model.entity.WorkHour;
import com.id3.screentracker.repository.IWorkHourRecordRepository;
import com.id3.screentracker.repository.IWorkHourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class WorkHourManager implements IWorkHourService {

    private final IWorkHourRepository workHourRepository;

    @Override
    public WorkHour saveOrUpdateWorkHour(LocalTime startTime, LocalTime endTime) {
        WorkHour workHour = workHourRepository.findAll().stream().findFirst().orElse(null);

        if (workHour == null) {

            workHour = new WorkHour();
        }

        workHour.setStartTime(startTime);
        workHour.setEndTime(endTime);
        return workHourRepository.save(workHour);
    }

    public WorkHour getWorkHour() {
        WorkHour workHour = workHourRepository.findAll().stream().findFirst().orElse(null);


        if (workHour == null) {
            workHour = new WorkHour();
            workHour.setStartTime(LocalTime.of(9, 0));
            workHour.setEndTime(LocalTime.of(18, 0));
            workHourRepository.save(workHour);
        }

        return workHour;
    }
}
