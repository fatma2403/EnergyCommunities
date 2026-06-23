package org.example;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UsageDataRepository {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final Map<String, UsageData> usageData;

    public UsageDataRepository() {
        this.usageData = new LinkedHashMap<>();
        save(new UsageData("2025-01-10T14:00:00", 18.050, 18.020, 1.056));
        save(new UsageData("2025-01-10T13:00:00", 15.015, 14.033, 2.049));
        save(new UsageData("2025-01-10T12:00:00", 20.100, 19.500, 0.876));
        save(new UsageData("2025-01-10T11:00:00", 22.300, 20.100, 0.500));
        save(new UsageData("2025-01-10T10:00:00", 19.800, 18.200, 1.200));
        save(new UsageData("2025-01-09T14:00:00", 16.500, 15.800, 2.300));
        save(new UsageData("2025-01-09T13:00:00", 14.200, 13.900, 1.800));
        save(new UsageData("2025-01-09T12:00:00", 17.600, 16.400, 0.980));
    }

    public UsageData save(UsageData ud) {
        usageData.put(ud.getHour(), ud);
        return ud;
    }

    public List<UsageData> findAll() {
        return new ArrayList<>(usageData.values());
    }

    public List<UsageData> findByDateRange(String start, String end) {
        LocalDateTime startTime = LocalDateTime.parse(start, FMT);
        LocalDateTime endTime   = LocalDateTime.parse(end,   FMT);

        return usageData.values().stream()
                .filter(ud -> {
                    LocalDateTime hour = LocalDateTime.parse(ud.getHour(), FMT);
                    return !hour.isBefore(startTime) && !hour.isAfter(endTime);
                })
                .toList();
    }
}