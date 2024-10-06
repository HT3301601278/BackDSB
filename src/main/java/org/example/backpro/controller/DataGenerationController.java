package org.example.backpro.controller;

import org.example.backpro.service.DataGenerationService;
import org.example.backpro.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/data-generation")
public class DataGenerationController {

    @Autowired
    private DataGenerationService dataGenerationService;

    private static final Logger logger = LoggerFactory.getLogger(DataGenerationController.class);

    @PostMapping("/start")
    public ResponseEntity<String> startDataGeneration(
            @RequestParam Long deviceId,
            @RequestParam int durationMinutes,
            @RequestParam int intervalSeconds,
            @RequestParam int minValue,
            @RequestParam int maxValue,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime) {
        logger.info("接收到数据生成请求：deviceId={}, startTime={}", deviceId, startTime);
        try {
            dataGenerationService.startDataGeneration(deviceId, durationMinutes, intervalSeconds, minValue, maxValue, startTime);
            return ResponseEntity.ok("数据生成任务已启动");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopDataGeneration(@RequestParam Long deviceId) {
        try {
            dataGenerationService.stopDataGeneration(deviceId);
            return ResponseEntity.ok("数据生成任务已停止");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("停止数据生成任务失败: " + e.getMessage());
        }
    }

    @PostMapping("/test-save")
    public ResponseEntity<String> testDataSave(@RequestParam Long deviceId) {
        dataGenerationService.testDataSave(deviceId);
        return ResponseEntity.ok("测试数据保存完成，请检查日志和数据库");
    }
}