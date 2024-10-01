package org.example.backpro.controller;

import org.example.backpro.service.DataGenerationService;
import org.example.backpro.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
            @RequestParam int intervalSeconds) {
        try {
            dataGenerationService.startDataGeneration(deviceId, durationMinutes, intervalSeconds);
            return ResponseEntity.ok("数据生成任务已启动");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}