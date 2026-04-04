package lab;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.java.Log;
import java.time.LocalDateTime;

@Log
@ToString
@AllArgsConstructor
@Entity
public class GameResult implements Comparable<GameResult> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter private Long id;

    @Getter private String whitePlayerName;
    @Getter private String blackPlayerName;
    @Getter private String winner;
    @Getter private int totalMoves;
    @Getter private long gameDurationMillis;
    private LocalDateTime timestamp;

    // JPA requires no-arg constructor
    public GameResult() {
    }

    public GameResult(String whitePlayerName, String blackPlayerName, String winner,
                      int totalMoves, long gameDurationMillis) {
        this.whitePlayerName = whitePlayerName;
        this.blackPlayerName = blackPlayerName;
        this.winner = winner;
        this.totalMoves = totalMoves;
        this.gameDurationMillis = gameDurationMillis;
        this.timestamp = LocalDateTime.now();
    }

    public long getGameDurationSeconds() {
        return gameDurationMillis / 1000;
    }

    @Override
    public int compareTo(GameResult other) {
        return Long.compare(this.gameDurationMillis, other.gameDurationMillis);
    }
}