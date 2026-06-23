package org.example;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface HourlyUsageRepository extends JpaRepository<HourlyUsage, LocalDateTime> {
}
