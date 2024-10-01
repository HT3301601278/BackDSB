package org.example.backpro.service;

import org.example.backpro.entity.Device;
import org.example.backpro.entity.DeviceData;
import org.example.backpro.repository.DeviceDataRepository;
import org.example.backpro.repository.DeviceRepository;
import org.example.backpro.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DataGenerationService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceDataRepository deviceDataRepository;

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

    private void generateAndSaveData(Device device, int minValue, int maxValue, Date startTime, Long deviceId, int intervalSeconds) {
        DeviceData deviceData = new DeviceData();
        deviceData.setDevice(device);
        int randomValue = random.nextInt(maxValue - minValue + 1) + minValue;
        deviceData.setValue(String.valueOf(randomValue));

        AtomicInteger runCounter = deviceRunCounters.computeIfAbsent(deviceId, k -> new AtomicInteger(0));
        int runNumber = runCounter.incrementAndGet();

        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        ZonedDateTime startZdt = ZonedDateTime.ofInstant(startTime.toInstant(), zoneId);
        ZonedDateTime recordZdt = startZdt.plusSeconds(runNumber * intervalSeconds);
        
        deviceData.setRecordTime(java.sql.Timestamp.from(recordZdt.toInstant()));
        
        deviceDataRepository.save(deviceData);

        logger.info("生成设备ID {} 的数据，记录时间：{}", deviceId, recordZdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
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
}