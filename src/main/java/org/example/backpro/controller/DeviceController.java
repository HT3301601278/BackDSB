package org.example.backpro.controller;

import org.example.backpro.entity.Device;
import org.example.backpro.entity.DeviceData;
import org.example.backpro.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;

import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    @PostMapping
    public ResponseEntity<Device> addDevice(@RequestBody Device device) {
        return ResponseEntity.ok(deviceService.addDevice(device));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<Device>> getAllDevices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(deviceService.getAllDevices(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getDevice(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.getDevice(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(@PathVariable Long id, @RequestBody Device device) {
        return ResponseEntity.ok(deviceService.updateDevice(id, device));
    }

    @PutMapping("/{id}/threshold")
    public ResponseEntity<Device> setThreshold(@PathVariable Long id, @RequestParam Double threshold) {
        return ResponseEntity.ok(deviceService.setThreshold(id, threshold));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<Device> toggleDevice(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.toggleDevice(id));
    }

    @GetMapping("/{id}/data")
    public ResponseEntity<Page<DeviceData>> getDeviceDataByTimeRange(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DeviceData> deviceData = deviceService.getDeviceDataByTimeRange(id, startTime, endTime, page, size);
        return ResponseEntity.ok(deviceData);
    }

    @PostMapping("/{id}/data")
    public ResponseEntity<DeviceData> addDeviceData(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date recordTime,
            @RequestParam String value) {
        DeviceData deviceData = deviceService.addDeviceData(id, recordTime, value);
        return ResponseEntity.ok(deviceData);
    }

    @GetMapping("/{id}/data/above-threshold")
    public ResponseEntity<List<DeviceData>> getDeviceDataAboveThreshold(@PathVariable Long id) {
        List<DeviceData> deviceData = deviceService.getDeviceDataAboveThreshold(id);
        return ResponseEntity.ok(deviceData);
    }
}
