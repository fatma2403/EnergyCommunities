package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// Holt die aktuelle Bewoelkung von der kostenlosen Open-Meteo API (kein API-Key noetig).
// Bei Sonne (wenig Wolken) soll der Producer mehr Strom erzeugen.
@Component
public class WeatherClient {

    // Beispiel-Standort (Wien). Kann beliebig geaendert werden.
    private static final String URL =
            "https://api.open-meteo.com/v1/forecast?latitude=48.2&longitude=16.4&current=cloud_cover";

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // Gibt die Bewoelkung in Prozent zurueck (0 = klar, 100 = bedeckt).
    // Wenn die API nicht erreichbar ist, nehmen wir 50 als Standardwert.
    public double getCloudCover() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .GET()
                    .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body());
            return root.get("current").get("cloud_cover").asDouble();
        } catch (Exception e) {
            System.out.println("Wetter-API nicht erreichbar, nutze 50%: " + e.getMessage());
            return 50.0;
        }
    }
}
