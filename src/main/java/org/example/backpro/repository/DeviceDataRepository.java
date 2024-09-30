package org.example.backpro.repository;

import org.example.backpro.entity.Device;
import org.example.backpro.entity.DeviceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface DeviceDataRepository extends JpaRepository<DeviceData, Long> {
	List<DeviceData> findByDeviceAndValueGreaterThanEqual(Device device, String threshold);

	@Query("SELECT dd FROM DeviceData dd WHERE dd.device = :device AND dd.recordTime BETWEEN :startTime AND :endTime")
	Page<DeviceData> findByDeviceAndRecordTimeBetween(Device device, Date startTime, Date endTime, Pageable pageable);
}
