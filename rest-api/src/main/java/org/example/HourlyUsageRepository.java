package org.example;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HourlyUsageRepository extends JpaRepository<HourlyUsage, LocalDateTime> {

    // Alle Stunden zwischen start und end (sortiert nach Stunde).
    List<HourlyUsage> findByHourBetweenOrderByHour(LocalDateTime start, LocalDateTime end);
}
