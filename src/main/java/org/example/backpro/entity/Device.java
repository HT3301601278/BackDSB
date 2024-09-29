package org.example.backpro.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "devices")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String macAddress;
    private String communicationChannel;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<DeviceData> deviceData;

    // 构造函数、getter和setter方法

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getCommunicationChannel() {
        return communicationChannel;
    }

    public void setCommunicationChannel(String communicationChannel) {
        this.communicationChannel = communicationChannel;
    }
}