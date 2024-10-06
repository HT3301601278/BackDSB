package org.example.backpro.controller;

import org.example.backpro.entity.Alert;
import org.example.backpro.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @GetMapping
    public ResponseEntity<Page<Alert>> getAllAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(alertService.getAllAlerts(PageRequest.of(page, size)));
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<Page<Alert>> getAlertsByDevice(
            @PathVariable Long deviceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(alertService.getAlertsByDevice(deviceId, PageRequest.of(page, size)));
    }

    @GetMapping("/timerange")
    public ResponseEntity<Page<Alert>> getAlertsByTimeRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(alertService.getAlertsByTimeRange(startDate, endDate, PageRequest.of(page, size)));
    }

    @GetMapping("/device/{deviceId}/timerange")
    public ResponseEntity<Page<Alert>> getAlertsByDeviceAndTimeRange(
            @PathVariable Long deviceId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(alertService.getAlertsByDeviceAndTimeRange(deviceId, startDate, endDate, PageRequest.of(page, size)));
    }
}