package lab;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Getter @Setter private String whitePlayerName;
    @Getter @Setter private String blackPlayerName;
    @Getter @Setter private String winner;
    @Getter @Setter private int totalMoves;
    @Getter @Setter private long gameDurationMillis;
    @Getter @Setter private LocalDateTime timestamp;

    // Vazba 1:N  N výsledků:1 hráč (vítěz)
    @ManyToOne
    @JoinColumn(name = "winner_id")
    @JsonIgnore
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
