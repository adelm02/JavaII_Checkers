# Checkers REST Project

Projekt pouziva povodnu JavaFX hru Checkers ako frontend a Spring Boot aplikaciu ako REST backend.

## Co projekt splna

- backend je v Jave pomocou Spring Boot
- komunikacia prebieha cez REST API
- data sa ukladaju cez JPA do databazy H2
- su pouzite minimalne 3 entity: `Player`, `GameResult`, `GameSession`
- je splnena vazba `1:N`: `Player -> GameResult`
- hra Checkers funguje ako klient, ktory vola backend pri logine hracov, starte hry a ukladani vysledku

## Architektura

- `lab.Main` spusta JavaFX klienta a zaroven Spring Boot backend
- `lab.DataManager` je REST klient pre JavaFX cast
- `cz.vsb.checkers.api.web.GameController` je jednoduchy REST controller
- `cz.vsb.checkers.api.repository` obsahuje JPA repository
- `lab.Player`, `lab.GameResult`, `lab.GameSession` su JPA entity

## REST endpointy

- `POST /api/players/login`
- `GET /api/players/top?limit=10`
- `GET /api/results`
- `POST /api/results`

## Spustenie

Predpoklad je Java 17 a Maven.

1. `mvn spring-boot:run`
2. aplikacia spusti backend na porte `8080`
3. nasledne sa otvori JavaFX hra, ktora backend pouziva ako datovu vrstvu

## Poznamka k obhajobe

Najsilnejsi argument projektu je, ze nejde len o hru s lokalnym ukladanim. JavaFX klient komunikuje s REST serverom, server pouziva JPA a databazu a nad hernymi datami eviduje hracov, herne session a historiu vysledkov.
