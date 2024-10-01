package org.example.backpro.controller;

import org.example.backpro.service.DataGenerationService;
import org.example.backpro.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data-generation")
public class DataGenerationController {

    @Autowired
    private DataGenerationService dataGenerationService;

    @PostMapping("/start")
    public ResponseEntity<String> startDataGeneration(
            @RequestParam Long deviceId,
            @RequestParam int durationMinutes,
            @RequestParam int intervalSeconds,
            @RequestParam int minValue,
            @RequestParam int maxValue) {
        try {
            dataGenerationService.startDataGeneration(deviceId, durationMinutes, intervalSeconds, minValue, maxValue);
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
}