package org.example.backpro.repository;

import org.example.backpro.entity.Alert;
import org.example.backpro.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    Page<Alert> findByDevice(Device device, Pageable pageable);
    Page<Alert> findByTimestampBetween(Date startDate, Date endDate, Pageable pageable);
    Page<Alert> findByDeviceAndTimestampBetween(Device device, Date startDate, Date endDate, Pageable pageable);
}