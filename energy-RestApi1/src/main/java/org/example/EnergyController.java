package org.example;

import org.springframework.web.bind.annotation.*;
import java.util.*;

/*@RestController
@RequestMapping("/energy")
public class EnergyController {

    @GetMapping("/current")
    public Map<String, Object> getCurrent() {
        Map<String, Object> data = new HashMap<>();
        data.put("hour", "now");
        data.put("consumption", 120);
        return data;
    }

    @GetMapping("/history")
    public List<Map<String, Object>> getHistory(@RequestParam String day) {
        List<Map<String, Object>> list = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("hour", i);
            entry.put("consumption", 100 + i * 10);
            list.add(entry);
        }

        return list;
    }
}*/
@RestController
@CrossOrigin(origins = "*")
public class EnergyController {

    private final CurrentPercentageRepository percentageRepository;
    private final UsageDataRepository usageDataRepository;

    @Autowired
    public EnergyController(CurrentPercentageRepository percentageRepository,
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