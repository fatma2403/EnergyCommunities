package org.example;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

// Hoert auf Update-Nachrichten vom Usage Service, berechnet die Prozentwerte
// fuer die Stunde und speichert sie in der Datenbank.
@Component
public class PercentageListener {

    private final PercentageRepository repository;

    public PercentageListener(PercentageRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitConfig.UPDATE_QUEUE)
    public void onUpdate(HourlyUsageMessage msg) {
        double produced = msg.getCommunityProduced();
        double used = msg.getCommunityUsed();
        double grid = msg.getGridUsed();

        // Wie viel Prozent der produzierten Community-Energie wurde verbraucht.
        double communityDepleted;
        if (produced == 0) {
            communityDepleted = 0;
        } else {
            communityDepleted = (used / produced) * 100.0;
            if (communityDepleted > 100) {
                communityDepleted = 100;
            }
        }

        // Anteil der Netz-Energie an der gesamten Nutzung (community_used + grid_used).
        double totalUsed = used + grid;
        double gridPortion;
        if (totalUsed == 0) {
            gridPortion = 0;
        } else {
            gridPortion = (grid / totalUsed) * 100.0;
        }

        // Auf zwei Nachkommastellen runden.
        communityDepleted = Math.round(communityDepleted * 100.0) / 100.0;
        gridPortion = Math.round(gridPortion * 100.0) / 100.0;

        // Die Tabelle haelt laut Spezifikation nur die aktuelle Stunde.
        // Deshalb alte Zeilen loeschen und nur die aktuelle speichern.
        Percentage percentage = new Percentage(msg.getHour(), communityDepleted, gridPortion);
        repository.deleteAll();
        repository.save(percentage);

        System.out.println("Percentage gespeichert: " + msg.getHour()
                + " communityDepleted=" + communityDepleted
                + "% gridPortion=" + gridPortion + "%");
    }
}
