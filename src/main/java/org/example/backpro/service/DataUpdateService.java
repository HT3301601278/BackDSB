package org.example.backpro.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backpro.entity.Device;
import org.example.backpro.entity.DeviceData;
import org.example.backpro.repository.DeviceDataRepository;
import org.example.backpro.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class DataUpdateService {
    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceDataRepository deviceDataRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void updateData(String macAddress, String channel, String duration) {
        String url = String.format("https://api.zhiyun360.com:28090/v2/feeds/712282624484/datastreams/%s_%s?duration=%s", macAddress, channel, duration);
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode datapoints = root.get("datapoints");

            Device device = deviceRepository.findByMacAddress(macAddress);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

            for (JsonNode datapoint : datapoints) {
                String value = datapoint.get("value").asText();
                String timestamp = datapoint.get("at").asText();
                Date recordTime = dateFormat.parse(timestamp);

                DeviceData deviceData = new DeviceData();
                deviceData.setValue(value);
                deviceData.setRecordTime(recordTime);
                deviceData.setDevice(device);

                deviceDataRepository.save(deviceData);

                if (device.getThreshold() != null && Double.parseDouble(value) >= device.getThreshold()) {
                    // 发送阈值警告消息（这里需要实现WebSocket或其他实时通信机制）
                    sendThresholdWarning(device);
                }
            }
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
        }
    }

    private void sendThresholdWarning(Device device) {
        // 这里需要实现WebSocket或其他实时通信机制
    }
}