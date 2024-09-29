package org.example.backpro.service;

import org.example.backpro.entity.Device;
import org.example.backpro.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.example.backpro.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;

    public Device addDevice(Device device) {
        return deviceRepository.save(device);
    }

    public void deleteDevice(Long id) {
        deviceRepository.deleteById(id);
    }

    public Page<Device> getAllDevices(int page, int size) {
        return deviceRepository.findAll(PageRequest.of(page, size));
    }

    public Device getDevice(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));
    }

    public Device updateDevice(Long id, Device deviceDetails) {
        Device device = getDevice(id);
        device.setName(deviceDetails.getName());
        device.setMacAddress(deviceDetails.getMacAddress());
        device.setCommunicationChannel(deviceDetails.getCommunicationChannel());
        return deviceRepository.save(device);
    }
}