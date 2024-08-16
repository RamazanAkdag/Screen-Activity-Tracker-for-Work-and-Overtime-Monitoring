package com.id3.screentracker.controller;

import com.id3.screentracker.listener.IListenerService;
import com.id3.screentracker.listener.ScreenListenerManager;
import com.id3.screentracker.model.entity.OvertimeRecord;
import com.id3.screentracker.model.entity.WorkHour;
import com.id3.screentracker.model.entity.WorkHourRecord;
import com.id3.screentracker.service.IWorkHourService;
import com.id3.screentracker.service.OvertimeRecordService;
import com.id3.screentracker.service.WorkHourRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalTime;
import java.util.List;
@Controller
@RequiredArgsConstructor
public class ScreenTrackerController {

    private final WorkHourRecordService workHourRecordService;
    private final OvertimeRecordService overtimeRecordService;
    private final IWorkHourService workHourService;
    private final ScreenListenerManager listenerService;

    @GetMapping("/screen-tracker")
    public String getScreenTracker(Model model) {

        WorkHourRecord todayWorkHourRecord = workHourRecordService.getTodayWorkHourRecord();
        List<WorkHourRecord> allWorkHourRecords = workHourRecordService.getAllWorkHourRecords();


        OvertimeRecord todayOvertimeRecord = overtimeRecordService.getTodayOvertimeRecord();
        List<OvertimeRecord> allOvertimeRecords = overtimeRecordService.getAllOvertimeRecords();


        WorkHour workHour = workHourService.getWorkHour();
        if (workHour == null) {
            workHour = new WorkHour();
        }
        String listenerStatus = listenerService.isRunning() ? "Running" : "Stopped";

        model.addAttribute("todayWorkHourRecord", todayWorkHourRecord);
        model.addAttribute("allWorkHourRecords", allWorkHourRecords);
        model.addAttribute("todayOvertimeRecord", todayOvertimeRecord);
        model.addAttribute("allOvertimeRecords", allOvertimeRecords);
        model.addAttribute("workHour", workHour);
        model.addAttribute("listenerStatus", listenerStatus);

        return "screenTracker";
    }

    @PostMapping("/update-workhour")
    public String updateWorkHour(@RequestParam("startTime") String startTime,
                                 @RequestParam("endTime") String endTime) {
        workHourService.saveOrUpdateWorkHour(LocalTime.parse(startTime), LocalTime.parse(endTime));
        return "redirect:/screen-tracker";
    }



    @GetMapping("/start-listener")
    public String startListener() {
        listenerService.startListening();
        return "redirect:/screen-tracker";
    }

    @GetMapping("/stop-listener")
    public String stopListener() {
        listenerService.stopListening();
        return "redirect:/screen-tracker";
    }
}