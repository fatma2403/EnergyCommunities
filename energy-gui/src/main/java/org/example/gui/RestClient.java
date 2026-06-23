package org.example.gui;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

// Holt die Daten ueber die REST-API. Die GUI redet nie direkt mit der Datenbank.
public class RestClient {

    private static final String BASE_URL = "http://localhost:8080";

    private final HttpClient http = HttpClient.newHttpClient();

    // GET /energy/current
    public String getCurrent() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/energy/current"))
                .GET()
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // GET /energy/historical?start=...&end=...
    public String getHistorical(String start, String end) throws Exception {
        String url = BASE_URL + "/energy/historical"
                + "?start=" + URLEncoder.encode(start, StandardCharsets.UTF_8)
                + "&end=" + URLEncoder.encode(end, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
