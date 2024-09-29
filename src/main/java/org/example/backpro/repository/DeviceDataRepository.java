package org.example.backpro.repository;

import org.example.backpro.entity.DeviceData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceDataRepository extends JpaRepository<DeviceData, Long> {
}