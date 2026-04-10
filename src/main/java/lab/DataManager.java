package lab;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cz.vsb.checkers.api.dto.LoginPlayerRequest;
import cz.vsb.checkers.api.dto.SaveGameResultRequest;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Log
public class DataManager {

    private static final String DEFAULT_API_URL = "http://localhost:8080";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public DataManager() {
        this.baseUrl = System.getProperty("checkers.api.base-url", DEFAULT_API_URL);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        log.info("REST DataManager initialized for " + baseUrl);
    }

    public Player loginPlayer(String name) {
        try {
            return post("/api/players/login", new LoginPlayerRequest(name), Player.class);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Nepodařilo se přihlásit hráče přes REST API.", e);
        } catch (IOException e) {
            throw new IllegalStateException("Nepodařilo se přihlásit hráče přes REST API.", e);
        }
    }

    public void addGameResult(GameResult result) {
        try {
            post("/api/results",
                    new SaveGameResultRequest(
                            result.getWhitePlayerName(),
                            result.getBlackPlayerName(),
                            result.getWinner(),
                            result.getTotalMoves(),
                            result.getGameDurationMillis()
                    ),
                    GameResult.class);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Nepodařilo se uložit výsledek hry přes REST API.", e);
        } catch (IOException e) {
            throw new IllegalStateException("Nepodařilo se uložit výsledek hry přes REST API.", e);
        }
    }

    public List<GameResult> getAllResults() {
        try {
            return getList("/api/results", new TypeReference<>() {});
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Nepodařilo se načíst historii her.", e);
        } catch (IOException e) {
            throw new IllegalStateException("Nepodařilo se načíst historii her.", e);
        }
    }

    public List<Player> getTopPlayers(int limit) {
        try {
            String path = "/api/players/top?limit=" + URLEncoder.encode(String.valueOf(limit), StandardCharsets.UTF_8);
            return getList(path, new TypeReference<>() {});
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Nepodařilo se načíst statistiky hráčů.", e);
        } catch (IOException e) {
            throw new IllegalStateException("Nepodařilo se načíst statistiky hráčů.", e);
        }
    }

    public void close() {
    }

    private <T> T post(String path, Object body, Class<T> responseType) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccess(response);
        return objectMapper.readValue(response.body(), responseType);
    }

    private <T> List<T> getList(String path, TypeReference<List<T>> typeReference) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccess(response);
        return objectMapper.readValue(response.body(), typeReference);
    }

    private void ensureSuccess(HttpResponse<String> response) {
        if (response.statusCode() >= 400) {
            throw new IllegalStateException("REST API error: " + response.statusCode() + " " + response.body());
        }
    }
}
