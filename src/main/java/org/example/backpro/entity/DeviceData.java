package org.example.backpro.entity;

import javax.persistence.*;

@Entity
@Table(name = "device_data")
public class DeviceData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value;

    @Column(columnDefinition = "TIMESTAMP")
    private java.sql.Timestamp recordTime;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    // 构造函数、getter和setter方法

    public void setValue(String value) {
        this.value = value;
    }

    public void setRecordTime(java.sql.Timestamp recordTime) {
        this.recordTime = recordTime;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public java.sql.Timestamp getRecordTime() {
        return recordTime;
    }

    public Device getDevice() {
        return device;
    }

    public DeviceData() {
    }
}
