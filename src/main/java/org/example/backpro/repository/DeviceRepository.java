package org.example.backpro.repository;

import org.example.backpro.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Device findByMacAddress(String macAddress);
}