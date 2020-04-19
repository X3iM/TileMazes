package sk.tuke.gamestudio.service;


import sk.tuke.gamestudio.entity.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentServiceJDBC implements CommentService {

    public static final String URL = "jdbc:postgresql://localhost:5432/gamestudio";
    public static final String USER = "postgres";
    public static final String PASSWORD = "password";

    public static final String INSERT_COMMENT =
            "INSERT INTO comment (player, game, points, playedon) VALUES (?, ?, ?, ?)";

    public static final String SELECT_COMMENTS =
            "SELECT player, game, points, playedon FROM comment WHERE game = ? ORDER BY points DESC LIMIT 10;";


    @Override
    public void addComment(Comment comment) throws CommentException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try(PreparedStatement ps = connection.prepareStatement(INSERT_COMMENT)){
                ps.setString(1, comment.getGame());
                ps.setString(2, comment.getPlayer());
                ps.setString(3, comment.getComment());
                ps.setDate(4, new Date(comment.getCommentedOn().getTime()));

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ScoreException("Error saving score", e);
        }
    }

    @Override
    public List<Comment> getComments(String game) throws CommentException {
        List<Comment> scores = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try(PreparedStatement ps = connection.prepareStatement(SELECT_COMMENTS)){
                ps.setString(1, game);
                try(ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        Comment score = new Comment(
                                rs.getString(1),
                                rs.getString(2),
                                rs.getString(3),
                                rs.getTimestamp(4)
                        );
                        scores.add(score);
                    }
                }
            }
        } catch (SQLException e) {
            throw new ScoreException("Error loading score", e);
        }
        return scores;
    }

    public static void main(String[] args) throws Exception {
        Comment comment = new Comment("tilemazes", "marus", "skjrgkrjgkjrgksjg", new java.util.Date());
        CommentService scoreService = new CommentServiceJDBC();
        scoreService.addComment(comment);
        System.out.println(scoreService.getComments("tilemazes"));
    }
}