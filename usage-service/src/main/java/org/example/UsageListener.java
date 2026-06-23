package org.example;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

// Hoert auf neue Producer-/User-Nachrichten, rechnet sie in die passende Stunde,
// speichert das Ergebnis in der Datenbank und meldet die Aenderung an den Percentage Service.
@Component
public class UsageListener {

    private final HourlyUsageRepository repository;
    private final RabbitTemplate rabbit;

    public UsageListener(HourlyUsageRepository repository, RabbitTemplate rabbit) {
        this.repository = repository;
        this.rabbit = rabbit;
    }

    @RabbitListener(queues = RabbitConfig.ENERGY_QUEUE)
    public void onMessage(EnergyMessage msg) {
        // Minute auf die volle Stunde abrunden, z.B. 14:33 -> 14:00.
        LocalDateTime hour = msg.getDatetime().truncatedTo(ChronoUnit.HOURS);

        // Vorhandene Stunde laden oder eine neue anlegen.
        HourlyUsage row = repository.findById(hour).orElse(new HourlyUsage(hour));

        if (msg.getType().equals("PRODUCER")) {
            // Producer erhoeht die produzierte Energie der Community.
            row.setCommunityProduced(row.getCommunityProduced() + msg.getKwh());
        } else if (msg.getType().equals("USER")) {
            // Zuerst wird die Community genutzt, der Rest kommt aus dem Netz (Grid).
            double available = row.getCommunityProduced() - row.getCommunityUsed();
            if (available < 0) {
                available = 0;
            }
            double fromCommunity = Math.min(msg.getKwh(), available);
            double fromGrid = msg.getKwh() - fromCommunity;

            row.setCommunityUsed(row.getCommunityUsed() + fromCommunity);
            row.setGridUsed(row.getGridUsed() + fromGrid);
        }

        repository.save(row);
        System.out.println("Usage aktualisiert: " + hour
                + " produced=" + row.getCommunityProduced()
                + " used=" + row.getCommunityUsed()
                + " grid=" + row.getGridUsed());

        // Aenderung an den Percentage Service melden.
        HourlyUsageMessage update = new HourlyUsageMessage(
                row.getHour(),
                row.getCommunityProduced(),
                row.getCommunityUsed(),
                row.getGridUsed());
        rabbit.convertAndSend(RabbitConfig.UPDATE_QUEUE, update);
    }
}
