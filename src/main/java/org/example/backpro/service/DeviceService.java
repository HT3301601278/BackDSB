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
import java.math.BigDecimal;

@Service
public class DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceDataRepository deviceDataRepository;

    @Autowired
    private DataUpdateService dataUpdateService;

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
        if (deviceDetails.getName() != null) {
            device.setName(deviceDetails.getName());
        }
        if (deviceDetails.getMacAddress() != null) {
            device.setMacAddress(deviceDetails.getMacAddress());
        }
        if (deviceDetails.getCommunicationChannel() != null) {
            device.setCommunicationChannel(deviceDetails.getCommunicationChannel());
        }
        if (deviceDetails.getThreshold() != null) {
            device.setThreshold(deviceDetails.getThreshold());
        }
        return deviceRepository.save(device);
    }

    public Device setThreshold(Long id, Double threshold) {
        Device device = getDevice(id);
        device.setThreshold(threshold);
        Device savedDevice = deviceRepository.save(device);

        // 立即检查当前值是否超过新设置的阈值
        List<DeviceData> recentData = deviceDataRepository.findTopByDeviceOrderByRecordTimeDesc(device, PageRequest.of(0, 1));
        if (!recentData.isEmpty()) {
            DeviceData latestData = recentData.get(0);
            BigDecimal currentValue = new BigDecimal(latestData.getValue());
            BigDecimal thresholdValue = BigDecimal.valueOf(threshold);
            if (currentValue.compareTo(thresholdValue) >= 0) {
                dataUpdateService.sendThresholdWarning(device, currentValue);
            }
        }

        return savedDevice;
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
