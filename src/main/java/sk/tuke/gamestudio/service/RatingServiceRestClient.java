package sk.tuke.gamestudio.service;

import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;

import java.util.Objects;

public class RatingServiceRestClient implements RatingService {
    private static final String URL = "http://localhost:8080/api/rating";

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void setRating(Rating rating) throws RatingException {
        restTemplate.postForEntity(URL, rating, Score.class);
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        return Objects.requireNonNull(restTemplate.getForEntity(URL + "/" + game, Rating.class).getBody()).getRating();
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        return Objects.requireNonNull(restTemplate.getForEntity(URL + "/" + game, Rating.class).getBody()).getRating();
    }
}