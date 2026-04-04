package lab;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.java.Log;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Log
@ToString(exclude = "wonGames")
@AllArgsConstructor
@Entity
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Getter @Setter private String name;

    @Getter private int gamesPlayed;
    @Getter private int gamesWon;
    @Getter private int totalMoves;
    @Getter private long totalTimeMillis;

    // Vazba 1:N – 1 hráč : N vyhraných
    @OneToMany(mappedBy = "winnerPlayer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Getter private List<GameResult> wonGames = new ArrayList<>();

    public Player() {
    }

    public Player(String name) {
        this.name = name;
    }

    public void setStats(int gamesPlayed, int gamesWon, int totalMoves, long totalTimeMillis) {
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.totalMoves = totalMoves;
        this.totalTimeMillis = totalTimeMillis;
    }

    public void addGameResult(boolean won, int moves, long timeMillis) {
        gamesPlayed++;
        if (won) gamesWon++;
        totalMoves += moves;
        totalTimeMillis += timeMillis;
        log.info("Výsledek hry přidán pro " + name + ", výhra: " + won);
    }

    public double getWinRate() {
        return gamesPlayed > 0 ? (double) gamesWon / gamesPlayed * 100 : 0;
    }
}