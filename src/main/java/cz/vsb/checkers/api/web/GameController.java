package cz.vsb.checkers.api.web;

import cz.vsb.checkers.api.dto.LoginPlayerRequest;
import cz.vsb.checkers.api.dto.SaveGameResultRequest;
import cz.vsb.checkers.api.repository.GameResultRepository;
import cz.vsb.checkers.api.repository.GameSessionRepository;
import cz.vsb.checkers.api.repository.PlayerRepository;
import jakarta.validation.Valid;
import lab.GameResult;
import lab.GameSession;
import lab.Player;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api")
public class GameController {

    private final PlayerRepository playerRepository;
    private final GameResultRepository gameResultRepository;
    private final GameSessionRepository gameSessionRepository;

    public GameController(
            PlayerRepository playerRepository,
            GameResultRepository gameResultRepository,
            GameSessionRepository gameSessionRepository
    ) {
        this.playerRepository = playerRepository;
        this.gameResultRepository = gameResultRepository;
        this.gameSessionRepository = gameSessionRepository;
    }

    @PostMapping("/players/login")
    public Player login(@Valid @RequestBody LoginPlayerRequest request) {
        String name = normalize(request.name());
        return playerRepository.findById(name)
                .orElseGet(() -> playerRepository.save(new Player(name)));
    }

    @GetMapping("/players/top")
    public List<Player> topPlayers(@RequestParam(defaultValue = "10") int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 50));
        return playerRepository.findAll(
                PageRequest.of(
                        0,
                        safeLimit,
                        Sort.by(
                                Sort.Order.desc("gamesWon"),
                                Sort.Order.desc("gamesPlayed"),
                                Sort.Order.asc("name")
                        )
                )
        ).getContent();
    }

    @GetMapping("/results")
    public List<GameResult> allResults() {
        return gameResultRepository.findAll(Sort.by(Sort.Order.desc("timestamp"), Sort.Order.desc("id")));
    }

    @PostMapping("/results")
    public GameResult saveResult(@Valid @RequestBody SaveGameResultRequest request) {
        String whiteName = normalize(request.whitePlayerName());
        String blackName = normalize(request.blackPlayerName());
        String winnerName = normalize(request.winnerName());

        if (whiteName.equalsIgnoreCase(blackName)) {
            throw new ResponseStatusException(BAD_REQUEST, "Players must have different names.");
        }

        Player whitePlayer = playerRepository.findById(whiteName)
                .orElseGet(() -> playerRepository.save(new Player(whiteName)));
        Player blackPlayer = playerRepository.findById(blackName)
                .orElseGet(() -> playerRepository.save(new Player(blackName)));

        Player winnerPlayer;
        if (whitePlayer.getName().equals(winnerName)) {
            winnerPlayer = whitePlayer;
        } else if (blackPlayer.getName().equals(winnerName)) {
            winnerPlayer = blackPlayer;
        } else {
            throw new ResponseStatusException(BAD_REQUEST, "Winner must be one of the game players.");
        }

        whitePlayer.addGameResult(winnerPlayer.getName().equals(whitePlayer.getName()),
                request.totalMoves(), request.gameDurationMillis());
        blackPlayer.addGameResult(winnerPlayer.getName().equals(blackPlayer.getName()),
                request.totalMoves(), request.gameDurationMillis());

        GameResult result = new GameResult(
                whitePlayer.getName(),
                blackPlayer.getName(),
                winnerPlayer.getName(),
                request.totalMoves(),
                request.gameDurationMillis(),
                winnerPlayer
        );
        GameResult savedResult = gameResultRepository.save(result);

        GameSession session = new GameSession(whitePlayer, blackPlayer);
        session.setWinnerPlayer(winnerPlayer);
        session.setMoveCount(request.totalMoves());
        session.setDurationMillis(request.gameDurationMillis());
        session.setFinishedAt(LocalDateTime.now());
        session.setStatus(GameSession.SessionStatus.FINISHED);
        session.setResult(savedResult);

        playerRepository.save(whitePlayer);
        playerRepository.save(blackPlayer);
        gameSessionRepository.save(session);

        return savedResult;
    }

    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Value must not be empty.");
        }
        return value.trim();
    }
}
