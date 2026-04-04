/*
 * DataManager
 *
 * Version 2.0
 *
 * 2025 Checkers Project
 */
package lab;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.java.Log;
import java.util.List;

@Log
public class DataManager {

    private final EntityManagerFactory emf;

    public DataManager() {
        emf = Persistence.createEntityManagerFactory("checkersPU");
        log.info("JPA EntityManagerFactory created");
    }

    public Player loginPlayer(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Jméno hráče nesmí být prázdné.");
        }

        String key = name.trim();
        EntityManager em = emf.createEntityManager();

        try {
            Player player = em.find(Player.class, key);
            if (player != null) {
                log.info("Player logged in: " + key);
                return player;
            }

            // new player
            player = new Player(key);
            em.getTransaction().begin();
            em.persist(player);
            em.getTransaction().commit();
            log.info("New player created: " + key);
            return player;
        } finally {
            em.close();
        }
    }

    /**
     * Adds game result and updates player stats.
     */
    public void addGameResult(GameResult result) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // save result
            em.persist(result);

            // update players
            Player whitePlayer = em.find(Player.class, result.getWhitePlayerName());
            Player blackPlayer = em.find(Player.class, result.getBlackPlayerName());

            if (whitePlayer != null) {
                boolean won = result.getWinner().equals(whitePlayer.getName());
                whitePlayer.addGameResult(won, result.getTotalMoves(), result.getGameDurationMillis());
                em.merge(whitePlayer);
            }

            if (blackPlayer != null) {
                boolean won = result.getWinner().equals(blackPlayer.getName());
                blackPlayer.addGameResult(won, result.getTotalMoves(), result.getGameDurationMillis());
                em.merge(blackPlayer);
            }

            em.getTransaction().commit();
            log.info("Game result saved to database");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.severe("Chyba při ukládání výsledku: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Returns all game results from DB.
     */
    public List<GameResult> getAllResults() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT r FROM GameResult r", GameResult.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Returns top players sorted by win rate.
     */
    public List<Player> getTopPlayers(int limit) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Player p ORDER BY p.gamesWon DESC", Player.class)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }


    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            log.info("EntityManagerFactory closed");
        }
    }
}