package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController
@CrossOrigin(origins = "*")
public class EnergyController {

    private final CurrentPercentage.CurrentPercentageRepository percentageRepository;
    private final UsageDataRepository usageDataRepository;

    @Autowired
    public EnergyController(CurrentPercentage.CurrentPercentageRepository percentageRepository,
                            UsageDataRepository usageDataRepository) {
        this.percentageRepository = percentageRepository;
        this.usageDataRepository  = usageDataRepository;
    }

    @GetMapping("/energy/current")
    public CurrentPercentage getCurrent() {
        return percentageRepository.findCurrent();
    }

    @GetMapping("/energy/historical")
    public List<UsageData> getHistorical(@RequestParam String start,
                                         @RequestParam String end) {
        return usageDataRepository.findByDateRange(start, end);
    }
}