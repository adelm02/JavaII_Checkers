package lab;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.java.Log;
import java.time.LocalDateTime;

@Log
@ToString(exclude = "winnerPlayer")
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

    // Vazba 1:N  N výsledků:1 hráč (vítěz)
    @ManyToOne
    @JoinColumn(name = "winner_id")
    @Getter @Setter private Player winnerPlayer;

    public GameResult() {
    }

    public GameResult(String whitePlayerName, String blackPlayerName, String winner,
                      int totalMoves, long gameDurationMillis, Player winnerPlayer) {
        this.whitePlayerName = whitePlayerName;
        this.blackPlayerName = blackPlayerName;
        this.winner = winner;
        this.totalMoves = totalMoves;
        this.gameDurationMillis = gameDurationMillis;
        this.winnerPlayer = winnerPlayer;
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