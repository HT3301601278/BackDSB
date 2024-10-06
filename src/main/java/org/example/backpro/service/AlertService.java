package org.example.backpro.service;

import org.example.backpro.entity.Alert;
import org.example.backpro.entity.Device;
import org.example.backpro.repository.AlertRepository;
import org.example.backpro.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.example.backpro.exception.ResourceNotFoundException;

import java.util.Date;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    public Page<Alert> getAllAlerts(Pageable pageable) {
        return alertRepository.findAll(pageable);
    }

    public Page<Alert> getAlertsByDevice(Long deviceId, Pageable pageable) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));
        return alertRepository.findByDevice(device, pageable);
    }

    public Page<Alert> getAlertsByTimeRange(Date startDate, Date endDate, Pageable pageable) {
        return alertRepository.findByTimestampBetween(startDate, endDate, pageable);
    }

    public Page<Alert> getAlertsByDeviceAndTimeRange(Long deviceId, Date startDate, Date endDate, Pageable pageable) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));
        return alertRepository.findByDeviceAndTimestampBetween(device, startDate, endDate, pageable);
    }
}