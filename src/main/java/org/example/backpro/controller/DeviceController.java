package org.example.backpro.controller;

import org.example.backpro.entity.Device;
import org.example.backpro.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
}