package lab;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;
import java.io.Serializable;

@Log
@ToString
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

    // JPA requires no-arg constructor
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
        log.info("Game result added for " + name + ", won: " + won);
    }

    public double getWinRate() {
        return gamesPlayed > 0 ? (double) gamesWon / gamesPlayed * 100 : 0;
    }

    public double getAverageMoves() {
        return gamesPlayed > 0 ? (double) totalMoves / gamesPlayed : 0;
    }
}