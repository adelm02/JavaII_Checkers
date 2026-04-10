package cz.vsb.checkers.api.web;

import cz.vsb.checkers.api.repository.GameResultRepository;
import cz.vsb.checkers.api.repository.GameSessionRepository;
import cz.vsb.checkers.api.repository.PlayerRepository;
import lab.GameResult;
import lab.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerRepository playerRepository;

    @MockBean
    private GameResultRepository gameResultRepository;

    @MockBean
    private GameSessionRepository gameSessionRepository;

    @Test
    void loginReturnsPlayer() throws Exception {
        Player player = new Player("Alice");
        when(playerRepository.findById("Alice")).thenReturn(Optional.of(player));

        mockMvc.perform(post("/api/players/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Alice"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void topPlayersReturnsList() throws Exception {
        Player player = new Player("Bob");
        player.setStats(5, 4, 90, 120000);
        when(playerRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(player)));

        mockMvc.perform(get("/api/players/top"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Bob"))
                .andExpect(jsonPath("$[0].gamesWon").value(4));
    }

    @Test
    void saveResultStoresGame() throws Exception {
        Player alice = new Player("Alice");
        Player bob = new Player("Bob");
        GameResult result = new GameResult("Alice", "Bob", "Alice", 28, 50000, alice);

        when(playerRepository.findById("Alice")).thenReturn(Optional.of(alice));
        when(playerRepository.findById("Bob")).thenReturn(Optional.of(bob));
        when(gameResultRepository.save(any(GameResult.class))).thenReturn(result);

        mockMvc.perform(post("/api/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "whitePlayerName": "Alice",
                                  "blackPlayerName": "Bob",
                                  "winnerName": "Alice",
                                  "totalMoves": 28,
                                  "gameDurationMillis": 50000
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.whitePlayerName").value("Alice"))
                .andExpect(jsonPath("$.winner").value("Alice"));
    }

    @Test
    void allResultsReturnsHistory() throws Exception {
        Player winner = new Player("Alice");
        GameResult result = new GameResult("Alice", "Bob", "Alice", 28, 50000, winner);
        when(gameResultRepository.findAll(any(Sort.class))).thenReturn(List.of(result));

        mockMvc.perform(get("/api/results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].whitePlayerName").value("Alice"))
                .andExpect(jsonPath("$[0].winner").value("Alice"));
    }
}
