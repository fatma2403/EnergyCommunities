package org.example;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Random;

// Community Energy Producer.
// Schickt alle paar Sekunden eine PRODUCER-Nachricht in die Queue.
// Wenn die Sonne scheint (wenig Wolken), wird mehr Strom produziert.
@SpringBootApplication
@EnableScheduling
public class ProducerApplication {

    private final RabbitTemplate rabbit;
    private final WeatherClient weather;
    private final Random random = new Random();

    public ProducerApplication(RabbitTemplate rabbit, WeatherClient weather) {
        this.rabbit = rabbit;
        this.weather = weather;
    }

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    // Laeuft alle 3 Sekunden.
    @Scheduled(fixedDelay = 3000)
    public void produce() {
        double cloud = weather.getCloudCover();
        // Sonnen-Faktor: 0 Wolken -> 1.0, 100 Wolken -> 0.0
        double sunFactor = (100.0 - cloud) / 100.0;

        // Grundwert pro Minute zwischen 0.001 und 0.005 kWh, mit Sonne multipliziert.
        double base = 0.001 + random.nextDouble() * 0.004;
        double kwh = base * sunFactor;
        kwh = Math.round(kwh * 100000.0) / 100000.0; // auf 5 Nachkommastellen runden

        EnergyMessage msg = new EnergyMessage("PRODUCER", "COMMUNITY", kwh, LocalDateTime.now());
        rabbit.convertAndSend(RabbitConfig.ENERGY_QUEUE, msg);
        System.out.println("Producer gesendet: " + msg + " (Wolken " + cloud + "%)");
    }
}
