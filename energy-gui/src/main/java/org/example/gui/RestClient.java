package org.example.gui;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

// Holt die Daten ueber die REST-API. Die GUI redet nie direkt mit der Datenbank.
public class RestClient {

    private static final String BASE_URL = "http://localhost:8080"; //das ist die feste webadresse für mein server //da rufe ich meine rest endpunkte auf

    private final HttpClient http = HttpClient.newHttpClient(); //hier erstelle ich eine neue httpclient da kann ich anfragen schicken an den server und bekomme antworten zurück

    // GET /energy/current
    public String getCurrent() throws Exception {  //der methoden name und sie ruft die aktuellen energy daten zurück
        HttpRequest request = HttpRequest.newBuilder() //ich erstelle eine neue http anfrage
                .uri(URI.create(BASE_URL + "/energy/current")) //an diese uri sende ich meinen http request
                .GET() //das ist eine get anfrage
                .build(); // da ist die anfrage erstellt
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString()); //da sende ich die anfrage an den server und die antwort wird gelesen
        if (response.statusCode() >= 400) { //wenn der statuscode größer als 400 ist ist ein fehler
            throw new RuntimeException("Server antwortete mit Statuscode " + response.statusCode()); //runtimeexeption damit das programm bei einem server fehler niht normal weiter macht
        }
        return response.body(); //gib die antwort vom server zurück als text
    }

    // GET /energy/historical?start=...&end=...
    public String getHistorical(String start, String end) throws Exception { //diese methode holt historische daten zwischen start und end //exception gibt fehler weiter oder wirft sie oben an die methode
        String url = BASE_URL + "/energy/historical" //da baue ich die zieladreese
                + "?start=" + URLEncoder.encode(start, StandardCharsets.UTF_8) //ich füge die startzeit zur url und kodiere sie mit utf 8
                + "&end=" + URLEncoder.encode(end, StandardCharsets.UTF_8); //so füge ich die endzeit zur url hinzu und kodiere sie mit utf 8
        HttpRequest request = HttpRequest.newBuilder() //da wird eine anfrage erstellt
                .uri(URI.create(url)) //erstellt die zieladresse für die anfrage
                .GET() //das ist eine http get anfrage
                .build(); // da wird die anfrage fertig erstellt
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString()); //da wird die anfrage an den server  gesendet und die antwort als string text zurück
        if (response.statusCode() >= 400) {
            throw new RuntimeException("Server antwortete mit Statuscode " + response.statusCode()); //damit das programm nicht nach dem fehler weiter macht
        }
        return response.body(); //da bekomme ich die antwort des server zurück
    }
    // GET /energy/historical/summary?start=...&end=... //Eine Methode, die diesen neuen Endpoint per HTTP aufruft:
    public String getSummary(String start, String end) throws Exception {
        String url = BASE_URL + "/energy/historical/summary"
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
 //der restapi kommuniziert mit der rest api und ruf die 2 endpoints auf