package org.example.service;


import org.example.config.EnergyProperties;
import org.example.model.HourlyUsageMessage;
import org.example.model.PercentageData;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 * Service responsible for listening to energy usage updates,
 * calculating percentages, and publishing the results to another queue.
 */
@Service
public class PercentageProcessor {

    private static final Logger log = LoggerFactory.getLogger(PercentageProcessor.class);
    private final RabbitTemplate rabbit;
    private final EnergyProperties props;

    public PercentageProcessor(RabbitTemplate rabbit,
                               EnergyProperties props) {
        this.rabbit = rabbit;
        this.props  = props;
    }

    /**
     * This method is triggered whenever a new usage message is received on the configured queue.
     * It calculates:
     *  - the percentage of energy produced by the community that was used (communityDepleted)
     *  - the portion of the total energy that came from the grid (gridPortion)
     * The result is then published to the final queue.
     */
    @RabbitListener(queues = "${energy.update-queue}")
    public void onUsageUpdate(HourlyUsageMessage msg) {
        double prod = msg.getCommunityProduced();
        double used = msg.getCommunityUsed();
        double grid = msg.getGridUsed();

        // Calculate how much of the produced community energy was used
        double communityDepleted = prod == 0.0
                ? 100.0  // If nothing was produced, assume all demand was unmet
                : Math.min(100.0, (used / prod) * 100.0);  // Cap at 100%

        // Calculate how much of total energy used came from the grid
        double gridPortion = prod + grid == 0.0
                ? 0.0  // Avoid division by zero
                : (grid / (prod + grid)) * 100.0;

        // Wrap the result into a PercentageData object
        PercentageData out = new PercentageData(msg.getHourKey(), communityDepleted, gridPortion);

        log.info("✔ percentage-service publish PercentageData: hourKey={}, communityDepleted={}, gridPortion={}",
                out.getHourKey(), out.getCommunityDepleted(), out.getGridPortion());

        // Send the result to the final queue
        rabbit.convertAndSend(props.getFinalQueue(), out);
    }
}
