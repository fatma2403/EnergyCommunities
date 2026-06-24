package org.example;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.Random;

// Community Energy User.
// Schickt in zufaelligen 1-5 Sekunden Intervallen eine USER-Nachricht in die Queue.
// In den Stosszeiten (morgens und abends) wird mehr Strom verbraucht.
@SpringBootApplication
public class UserApplication implements CommandLineRunner {

    private final RabbitTemplate rabbit;
    private final Random random = new Random();

    public UserApplication(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    @Override
    public void run(String... args) {
        new Thread(() -> {
            while (true) {
                try {
                    use();
                    // Zufaelliges Intervall zwischen 1 und 5 Sekunden (1000 bis 5000 ms)
                    int delay = 1000 + random.nextInt(4000);
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    public void use() {
        LocalDateTime now = LocalDateTime.now();
        double timeFactor = peakFactor(now.getHour());

        // Grundwert pro Minute zwischen 0.001 und 0.004 kWh, in Stosszeiten mehr.
        double base = 0.001 + random.nextDouble() * 0.003;
        double kwh = base * timeFactor;
        kwh = Math.round(kwh * 100000.0) / 100000.0;

        EnergyMessage msg = new EnergyMessage("USER", "COMMUNITY", kwh, now);
        rabbit.convertAndSend(RabbitConfig.ENERGY_QUEUE, msg);
        System.out.println("User gesendet: " + msg);
    }

    // Morgens (6-9 Uhr) und abends (18-21 Uhr) ist der Verbrauch hoeher.
    private double peakFactor(int hour) {
        if ((hour >= 6 && hour <= 9) || (hour >= 18 && hour <= 21)) {
            return 2.0;
        }
        return 1.0;
    }
}
