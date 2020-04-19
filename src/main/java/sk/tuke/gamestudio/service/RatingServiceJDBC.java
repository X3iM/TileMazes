package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import java.sql.*;

public class RatingServiceJDBC implements RatingService {

    public static final String URL = "jdbc:postgresql://localhost:5432/gamestudio";
    public static final String USER = "postgres";
    public static final String PASSWORD = "password";

    public static final String UPDATE_RATING =
            "UPDATE rating SET rating = ?, ratedon = ? WHERE game = ? AND player = ?;";

    public static final String SELECT_AVERAGE_RATING =
            "SELECT rating FROM rating WHERE game = ? ORDER BY rating DESC LIMIT 10;";

    public static final String SELECT_RATING =
            "SELECT rating FROM rating WHERE game = ? AND player = ? ORDER BY rating DESC LIMIT 10;";

    @Override
    public void setRating(Rating rating) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try(PreparedStatement ps = connection.prepareStatement(UPDATE_RATING)){
                ps.setInt(1, rating.getRating());
                ps.setDate(2, new Date(rating.getRatedon().getTime()));
                ps.setString(3, rating.getGame());
                ps.setString(4, rating.getPlayer());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ScoreException("Error saving rating", e);
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        float averageRating = 0;
        int cnt = 0;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try(PreparedStatement ps = connection.prepareStatement(SELECT_AVERAGE_RATING)){
                ps.setString(1, game);
                try(ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        averageRating += rs.getInt(3);
                        cnt++;
                    }
                }
            }
        } catch (SQLException e) {
            throw new ScoreException("Error loading rating", e);
        }
        return Math.round(averageRating/cnt);
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        int rating = 0;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try(PreparedStatement ps = connection.prepareStatement(SELECT_RATING)){
                ps.setString(1, game);
                ps.setString(2, player);
                try(ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        rating = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new ScoreException("Error loading rating", e);
        }

        return rating;
    }

    public static void main(String[] args) throws Exception {
        Rating rating = new Rating("jaro", "tilemazes", 90000, new java.util.Date());
        RatingServiceJDBC ratingService = new RatingServiceJDBC();
        ratingService.setRating(rating);
        System.out.println(ratingService.getRating("tilemazes", "jaro"));
    }

}
