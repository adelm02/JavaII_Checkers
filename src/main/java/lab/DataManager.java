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
        // Vytvoření factory na základě persistence.xml
        emf = Persistence.createEntityManagerFactory("checkersPU");
        log.info("JPA EntityManagerFactory vytvořena");
    }

    public Player loginPlayer(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Jméno hráče nesmí být prázdné.");
        }

        String key = name.trim();
        EntityManager em = emf.createEntityManager();

        try {
            Player player = em.find(Player.class, key); // Vyhledání v DB podle ID
            if (player != null) return player;

            player = new Player(key);
            em.getTransaction().begin();
            em.persist(player); // Uložení nového hráče
            em.getTransaction().commit();
            return player;
        } finally {
            em.close();
        }
    }

    public void addGameResult(GameResult result) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Najdeme objekty hráčů v DB, abychom je mohli aktualizovat
            Player white = em.find(Player.class, result.getWhitePlayerName());
            Player black = em.find(Player.class, result.getBlackPlayerName());
            Player winner = em.find(Player.class, result.getWinner());

            if (white != null) {
                white.addGameResult(result.getWinner().equals(white.getName()),
                        result.getTotalMoves(), result.getGameDurationMillis());
                em.merge(white);
            }
            if (black != null) {
                black.addGameResult(result.getWinner().equals(black.getName()),
                        result.getTotalMoves(), result.getGameDurationMillis());
                em.merge(black);
            }

            // Propojení výsledku s vítězem (vazba 1:N)
            // result.setWinnerPlayer(winner); // Pokud bys měl setter
            em.persist(result);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            log.severe("Chyba při ukládání: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public List<GameResult> getAllResults() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT r FROM GameResult r", GameResult.class).getResultList();
        } finally {
            em.close();
        }
    }

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
        if (emf != null && emf.isOpen()) emf.close();
    }
}