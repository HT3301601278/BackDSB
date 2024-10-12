package org.example.backpro.service;

import org.example.backpro.entity.Alert;
import org.example.backpro.entity.Device;
import org.example.backpro.entity.DeviceData;
import org.example.backpro.exception.ResourceNotFoundException;
import org.example.backpro.repository.AlertRepository;
import org.example.backpro.repository.DeviceDataRepository;
import org.example.backpro.repository.DeviceRepository;
import org.example.backpro.websocket.AlertWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DataGenerationService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceDataRepository deviceDataRepository;

    @Autowired
    private AlertWebSocketHandler alertWebSocketHandler;

    @Autowired
    private AlertRepository alertRepository;

    private final Random random = new Random();
    private final Map<Long, ScheduledExecutorService> deviceExecutors = new ConcurrentHashMap<>();
    private final Map<Long, AtomicInteger> deviceRunCounters = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(DataGenerationService.class);

    @Async
    public void startDataGeneration(Long deviceId, int durationMinutes, int intervalSeconds, int minValue, int maxValue, Date startTime) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        deviceExecutors.put(deviceId, executor);
        deviceRunCounters.put(deviceId, new AtomicInteger(0)); // 初始化运行计数器

        long initialDelay = 0; // 立即开始任务

        logger.info("开始数据生成任务：deviceId={}, startTime={}, initialDelay={}", deviceId, startTime, initialDelay);

        executor.scheduleAtFixedRate(() -> generateAndSaveData(device, minValue, maxValue, startTime, deviceId, intervalSeconds),
                initialDelay, intervalSeconds * 1000L, TimeUnit.MILLISECONDS); // 使用毫秒作为时间单位

        executor.schedule(() -> {
            stopDataGeneration(deviceId);
        }, initialDelay + durationMinutes * 60 * 1000L, TimeUnit.MILLISECONDS);

        logger.info("数据生成任务已调度：deviceId={}", deviceId);
    }

    private void generateAndSaveData(Device device, int minValue, int maxValue, Date recordTime, Long deviceId, int intervalSeconds) {
        try {
            int value = random.nextInt(maxValue - minValue + 1) + minValue;

            DeviceData deviceData = new DeviceData();
            deviceData.setDevice(device);
            deviceData.setValue(String.valueOf(value));
            deviceData.setRecordTime(new java.sql.Timestamp(recordTime.getTime()));

            DeviceData savedData = deviceDataRepository.save(deviceData);
            logger.info("成功保存设备数据：ID = {}, 设备 ID = {}, 值 = {}, 时间 = {}",
                     savedData.getId(), savedData.getDevice().getId(), savedData.getValue(), savedData.getRecordTime());

            if (device.getThreshold() != null && value >= device.getThreshold()) {
                sendThresholdWarning(device, BigDecimal.valueOf(value));
            }

            // 更新记录时间
            recordTime = new Date(recordTime.getTime() + intervalSeconds * 1000L);
        } catch (Exception e) {
            logger.error("生成和保存数据时发生错误：", e);
        }
    }

    public void stopDataGeneration(Long deviceId) {
        ScheduledExecutorService executor = deviceExecutors.remove(deviceId);
        deviceRunCounters.remove(deviceId); // 移除运行计数器
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

    @Transactional
    public void testDataSave(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

        DeviceData testData = new DeviceData();
        testData.setDevice(device);
        testData.setValue("Test Value");
        testData.setRecordTime(new java.sql.Timestamp(System.currentTimeMillis()));

        DeviceData savedData = deviceDataRepository.save(testData);
        logger.info("测试数据保存成功：ID = {}", savedData.getId());
    }

    private void sendThresholdWarning(Device device, BigDecimal currentValue) {
        String message = String.format("警告: 设备 %s (ID: %d) 的当前数值 %.2f 超过阈值 %.2f",
            device.getName(), device.getId(), currentValue, device.getThreshold());
        logger.warn(message);
        alertWebSocketHandler.sendAlertToAll(message);

        // 保存警告信息到数据库
        Alert alert = new Alert(message, device);
        alertRepository.save(alert);
    }
}
