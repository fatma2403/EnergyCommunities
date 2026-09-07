package org.example;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HourlyUsageRepository extends JpaRepository<HourlyUsage, LocalDateTime> {

    List<HourlyUsage> findByHourBetweenOrderByHour(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(h.communityProduced), SUM(h.communityUsed), SUM(h.gridUsed) " +
            "FROM HourlyUsage h WHERE h.hour BETWEEN :start AND :end")
    Object[] getSums(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}