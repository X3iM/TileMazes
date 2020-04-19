package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Score;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Transactional
public class ScoreServiceJPA implements ScoreService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addScore(Score score) {
        entityManager.persist(score);
    }

    @Override
    public List<Score> getBestScores(String gameName) {
        return entityManager.createNamedQuery("Score.getBestScores")
                .setParameter("game", gameName)
                .setMaxResults(10).getResultList();
    }
}