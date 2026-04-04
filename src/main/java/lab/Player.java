package lab;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.java.Log;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Log
@ToString(exclude = "wonGames") // Exclude list to prevent circular references in logs
@AllArgsConstructor
@Entity
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Getter @Setter private String name; // Primární klíč

    @Getter private int gamesPlayed;
    @Getter private int gamesWon;
    @Getter private int totalMoves;
    @Getter private long totalTimeMillis;

    // Definice vazby 1:N
    // Jeden hráč může mít mnoho vyhraných her
    @OneToMany(mappedBy = "winnerPlayer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Getter private List<GameResult> wonGames = new ArrayList<>();

    // JPA vyžaduje konstruktor bez parametrů
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