# Energy Communities

Verteiltes System fuer eine Energiegemeinschaft. Producer und User schicken ihre
Werte an eine Message-Queue. Ein Usage Service rechnet die Werte pro Stunde zusammen
und speichert sie in einer PostgreSQL-Datenbank. Ein Percentage Service berechnet
daraus Prozentwerte. Eine REST-API liest die Datenbank, und eine JavaFX-GUI zeigt
die Daten an.

## Komponenten

| Modul                | Beschreibung                                                             |
|----------------------|--------------------------------------------------------------------------|
| `producer-service`   | Schickt PRODUCER-Nachrichten. Nutzt die Open-Meteo Wetter-API (Sonne).   |
| `user-service`       | Schickt USER-Nachrichten. Mehr Verbrauch in den Stosszeiten.             |
| `usage-service`      | Aggregiert die Nachrichten pro Stunde und speichert sie in der DB.       |
| `percentage-service` | Berechnet community_depleted und grid_portion und speichert sie.         |
| `rest-api`           | Spring-Boot REST-API mit /energy/current und /energy/historical.         |
| `energy-gui`         | JavaFX-Oberflaeche, holt die Daten ueber die REST-API.                   |

## Ablauf

1. Producer schickt produzierte Energie (abhaengig vom Wetter) in die Queue.
2. User schickt verbrauchte Energie (abhaengig von der Tageszeit) in die Queue.
3. Usage Service rechnet die Minuten in die passende Stunde und speichert sie.
   Verbrauch wird zuerst aus der Community gedeckt, der Rest aus dem Netz (Grid).
4. Usage Service meldet die Aenderung an den Percentage Service.
5. Percentage Service berechnet die Prozentwerte und speichert sie.
6. GUI fragt ueber die REST-API die aktuellen und historischen Daten ab.

## Starten

### 1. Datenbank und RabbitMQ starten

```
docker compose up -d
```

PostgreSQL laeuft auf Port 5432 (DB `energy`, User `disysuser`, Passwort `disyspw`),
RabbitMQ auf Port 5672 (Management-UI: http://localhost:15672, guest/guest).

### 2. Backend-Dienste starten

Jeder Dienst ist eine eigene Anwendung und wird einzeln gestartet (eigenes Terminal):

```
mvn -pl usage-service spring-boot:run
mvn -pl percentage-service spring-boot:run
mvn -pl rest-api spring-boot:run
mvn -pl producer-service spring-boot:run
mvn -pl user-service spring-boot:run
```

### 3. GUI starten

```
cd energy-gui
mvn javafx:run
```

## REST-API

- `GET /energy/current` – Prozentwerte der aktuellen Stunde
- `GET /energy/historical?start=2025-01-10T00:00:00&end=2025-01-10T23:00:00` – Stundenwerte im Zeitraum
