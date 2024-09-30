package org.example.backpro.service;

import org.example.backpro.entity.Device;
import org.example.backpro.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.example.backpro.exception.ResourceNotFoundException;
import org.example.backpro.repository.DeviceDataRepository;
import java.util.Date;
import org.example.backpro.entity.DeviceData;

import java.util.List;

import org.springframework.data.repository.query.Param;

@Service
public class DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceDataRepository deviceDataRepository;

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

    public Device setThreshold(Long id, Double threshold) {
        Device device = getDevice(id);
        device.setThreshold(threshold);
        return deviceRepository.save(device);
    }

    public Device toggleDevice(Long id) {
        Device device = getDevice(id);
        device.setIsOn(!device.getIsOn());
        return deviceRepository.save(device);
    }

    public Page<DeviceData> getDeviceDataByTimeRange(Long deviceId, Date startTime, Date endTime, int page, int size) {
        Device device = getDevice(deviceId);
        return deviceDataRepository.findByDeviceAndRecordTimeBetween(device, startTime, endTime, PageRequest.of(page, size));
    }

    public DeviceData addDeviceData(Long deviceId, Date recordTime, String value) {
        Device device = getDevice(deviceId);
        DeviceData deviceData = new DeviceData();
        deviceData.setDevice(device);
        deviceData.setRecordTime(recordTime);
        deviceData.setValue(value);
        return deviceDataRepository.save(deviceData);
    }

    public List<DeviceData> getDeviceDataAboveThreshold(Long deviceId) {
        Device device = getDevice(deviceId);
        if (device.getThreshold() == null) {
            throw new IllegalStateException("Device threshold is not set");
        }
        return deviceDataRepository.findByDeviceAndValueGreaterThanEqual(device, device.getThreshold().toString());
    }
}