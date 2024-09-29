package org.example.backpro.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "device_data")
public class DeviceData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date recordTime;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    // 构造函数、getter和setter方法

    public void setValue(String value) {
        this.value = value;
    }

    public void setRecordTime(Date recordTime) {
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

    public Date getRecordTime() {
        return recordTime;
    }

    public Device getDevice() {
        return device;
    }
}