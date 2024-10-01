package org.example.backpro.service;

import org.example.backpro.entity.Device;
import org.example.backpro.entity.DeviceData;
import org.example.backpro.repository.DeviceDataRepository;
import org.example.backpro.repository.DeviceRepository;
import org.example.backpro.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class DataGenerationService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceDataRepository deviceDataRepository;

    private final Random random = new Random();

    @Async
    public void startDataGeneration(Long deviceId, int durationMinutes, int intervalSeconds) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> generateAndSaveData(device),
                0, intervalSeconds, TimeUnit.SECONDS);

        executor.schedule(() -> {
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, durationMinutes, TimeUnit.MINUTES);
    }

    private void generateAndSaveData(Device device) {
        DeviceData deviceData = new DeviceData();
        deviceData.setDevice(device);
        deviceData.setValue(String.valueOf(random.nextDouble() * 100)); // 生成0到100之间的随机数
        deviceData.setRecordTime(new Date());
        deviceDataRepository.save(deviceData);
    }
}