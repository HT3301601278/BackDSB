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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DataGenerationService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceDataRepository deviceDataRepository;

    private final Random random = new Random();
    private final Map<Long, ScheduledExecutorService> deviceExecutors = new ConcurrentHashMap<>();

    @Async
    public void startDataGeneration(Long deviceId, int durationMinutes, int intervalSeconds, int minValue, int maxValue) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        deviceExecutors.put(deviceId, executor);

        executor.scheduleAtFixedRate(() -> generateAndSaveData(device, minValue, maxValue),
                0, intervalSeconds, TimeUnit.SECONDS);

        executor.schedule(() -> {
            stopDataGeneration(deviceId);
        }, durationMinutes, TimeUnit.MINUTES);
    }

    private void generateAndSaveData(Device device, int minValue, int maxValue) {
        DeviceData deviceData = new DeviceData();
        deviceData.setDevice(device);
        int randomValue = random.nextInt(maxValue - minValue + 1) + minValue;
        deviceData.setValue(String.valueOf(randomValue));
        deviceData.setRecordTime(new Date());
        deviceDataRepository.save(deviceData);
    }

    public void stopDataGeneration(Long deviceId) {
        ScheduledExecutorService executor = deviceExecutors.remove(deviceId);
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}