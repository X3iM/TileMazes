package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class RatingServiceJPA implements RatingService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
        entityManager.persist(rating);
    }

    @Override
    public int getAverageRating(String game){
        int sum = 0;
        List<Rating> list = entityManager.createNamedQuery("Rating.getAverageRating").setParameter("game", game).getResultList();
        for (Rating rate : list) {
            sum = sum + rate.getRating();
        }
        return list.size() != 0 ? sum/list.size() : 0;
//        return (int) entityManager.createNamedQuery("Rating.getAverageRating")
//                .setParameter("game", game)
//                .setMaxResults(1).getSingleResult();
    }

    @Override
    public int getRating(String game, String player){
        return (int) entityManager.createNamedQuery("Rating.getRating")
                .setParameter(1, game)
                .setParameter(2, player)
                .setMaxResults(1).getSingleResult();
    }
}
