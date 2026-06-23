package org.example;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

// REST-API mit zwei Endpunkten. Liest nur aus der Datenbank.
@RestController
@CrossOrigin(origins = "*")
public class EnergyController {

    private final PercentageRepository percentageRepository;
    private final HourlyUsageRepository usageRepository;

    public EnergyController(PercentageRepository percentageRepository,
                            HourlyUsageRepository usageRepository) {
        this.percentageRepository = percentageRepository;
        this.usageRepository = usageRepository;
    }

    // Liefert die Prozentwerte der aktuellen (neuesten) Stunde.
    @GetMapping("/energy/current")
    public Percentage getCurrent() {
        return percentageRepository.findTopByOrderByHourDesc();
    }

    // Liefert die Stundenwerte fuer einen Zeitraum von start bis end.
    @GetMapping("/energy/historical")
    public List<HourlyUsage> getHistorical(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return usageRepository.findByHourBetweenOrderByHour(start, end);
    }
}
