package org.example.backpro.controller;

import org.example.backpro.service.DataUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data")
public class DataUpdateController {
    @Autowired
    private DataUpdateService dataUpdateService;

    @PostMapping("/update")
    public ResponseEntity<String> updateData(
            @RequestParam String macAddress,
            @RequestParam String channel,
            @RequestParam String duration) {
        dataUpdateService.updateData(macAddress, channel, duration);
        return ResponseEntity.ok("数据更新成功");
    }
}