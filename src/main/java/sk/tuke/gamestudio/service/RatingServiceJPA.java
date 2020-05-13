package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Transactional
public class RatingServiceJPA implements RatingService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
        if (contains(rating)) {
            entityManager.createNamedQuery("Rating.updateRating")
                    .setParameter(1, rating.getRating())
                    .setParameter(2, rating.getGame())
                    .setParameter(3, rating.getPlayer());
            entityManager.getTransaction().commit();
        }
        else
            entityManager.persist(rating);
    }

    @Override
    public int getAverageRating(String game){
        double rating = (double) entityManager.createNamedQuery("Rating.getAverageRating").setParameter(1, game).getResultList().get(0);
        return (int) Math.round(rating);
    }

    @Override
    public int getRating(String game, String player){
        List list = entityManager.createNamedQuery("Rating.getRating").setParameter(1, game).setParameter(2, player).getResultList();
        return list.isEmpty() ? -1 : (int)list.get(0);
    }

    private boolean contains(Rating rating) {
        AtomicBoolean contains = new AtomicBoolean(false);
        List<Rating> ratings = entityManager.createNamedQuery("Rating.allRating").getResultList();
        ratings.forEach(rating1 -> {
            if (rating1.getPlayer().equals(rating.getPlayer()))
                contains.set(true);
        });
        return contains.get();
    }
}