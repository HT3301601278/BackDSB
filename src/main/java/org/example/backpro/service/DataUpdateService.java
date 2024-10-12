package org.example.backpro.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class DataUpdateService {
    private static final Logger logger = LoggerFactory.getLogger(DataUpdateService.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceDataRepository deviceDataRepository;

    @Autowired
    private AlertRepository alertRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void updateData(String macAddress, String channel, String duration) {
        String url = String.format("https://api.zhiyun360.com:28090/v2/feeds/712282624484/datastreams/%s_%s?duration=%s", macAddress, channel, duration);
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode datapoints = root.get("datapoints");

            Device device = deviceRepository.findByMacAddress(macAddress);
            if (device == null) {
                throw new ResourceNotFoundException("Device not found with MAC address: " + macAddress);
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

            for (JsonNode datapoint : datapoints) {
                String value = datapoint.get("value").asText();
                String timestamp = datapoint.get("at").asText();
                Date recordTime = dateFormat.parse(timestamp);

                DeviceData deviceData = new DeviceData();
                deviceData.setValue(value);
                deviceData.setRecordTime(new java.sql.Timestamp(recordTime.getTime()));
                deviceData.setDevice(device);

                deviceDataRepository.save(deviceData);

                if (device.getThreshold() != null) {
                    BigDecimal thresholdValue = BigDecimal.valueOf(device.getThreshold());
                    BigDecimal currentValue = new BigDecimal(value);
                    if (currentValue.compareTo(thresholdValue) >= 0) {
                        sendThresholdWarning(device, currentValue);
                    }
                }
            }
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
        }
    }

    @Autowired
    private AlertWebSocketHandler alertWebSocketHandler;

    public void sendThresholdWarning(Device device, BigDecimal currentValue) {
        String message = String.format("警告: 设备 %s (MAC地址: %s) 的当前数值 %.2f 超过阈值 %.2f",
            device.getName(), device.getMacAddress(), currentValue, device.getThreshold());
        logger.warn(message);
        alertWebSocketHandler.sendAlertToAll(message);

        // 保存警告信息到数据库
        Alert alert = new Alert(message, device);
        alertRepository.save(alert);
    }
}
