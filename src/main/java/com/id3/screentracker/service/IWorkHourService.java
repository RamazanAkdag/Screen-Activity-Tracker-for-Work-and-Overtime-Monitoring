package com.id3.screentracker.service;

import com.id3.screentracker.model.entity.WorkHour;

import java.time.LocalTime;

public interface IWorkHourService {
    public WorkHour saveOrUpdateWorkHour(LocalTime startTime, LocalTime endTime);
    public WorkHour getWorkHour();
}
