package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.game.core.Cell;
import sk.tuke.gamestudio.game.core.CellType;
import sk.tuke.gamestudio.game.core.GameState;
import sk.tuke.gamestudio.game.core.Player;
import sk.tuke.gamestudio.game.core.mazegenerator.MazeGenerator;
import sk.tuke.gamestudio.service.*;

import javax.servlet.ServletContext;
import java.util.Date;
import java.util.Random;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
@RequestMapping("/tilemazes")
public class MazeController {

    public final int    weight = 10;

    private int         score;

    private GameState   state;
    private Player      player;
    private Cell[][]    maze;
    private String      loggedUser;

    @Autowired
    private ServletContext  servletContext;

    @Autowired
    private ScoreService    scoreService;
    @Autowired
    private RatingService   ratingService;
    @Autowired
    private CommentService  commentService;

    @GetMapping()
    public String tileMazes(String row, String column, Model model) {
        if (row == null || column == null)
            return newGame(model);
        if (maze == null)
            newGame(model);
        else {
            if (state == GameState.PLAYING) {
                move(Integer.parseInt(row), Integer.parseInt(column));
                prepareModel(model);
            }
        }
        return "tilemazes";
    }

    @RequestMapping("/new")
    public String newGame(Model model) {
        score = 0;
        state = GameState.PLAYING;
        Random rand = new Random();
        player = new Player(rand.nextInt(weight), rand.nextInt(weight));
        maze = new MazeGenerator(weight, weight, player.getX(), player.getY()).getMaze();
        prepareModel(model);
        return "tilemazes";
    }

    @RequestMapping("/login")
    public String login(String login, Model model) {
        if (login.isEmpty())
            loggedUser = null;
        else
            loggedUser = login;
        prepareModel(model);
        return "tilemazes";
    }

    @RequestMapping("/logout")
    public String logout(Model model) {
        loggedUser = null;
        prepareModel(model);
        return "tilemazes";
    }

    @RequestMapping("/commented")
    public String commented(String comment) {
        try {
            commentService.addComment(new Comment(loggedUser, "tilemazes", comment, new Date()));
        } catch (CommentException e) {
            e.printStackTrace();
        }
        return "tilemazes";
    }

    @RequestMapping("/rategame")
    public String rateGame(String rating) {
        int rate = Integer.parseInt(rating);
        try {
            ratingService.setRating(new Rating(loggedUser, "tilemazes", rate, new Date()));
        } catch (RatingException e) {
            e.printStackTrace();
        }
        return "tilemazes";
    }

    public String getHtmlField() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class='field'>\n");
        for (int row = 0; row < weight; row++) {
            sb.append("<tr>\n");
            for (int column = 0; column < weight; column++) {
                Cell tile = maze[row][column];
                if (player.getX() == tile.getX() && player.getY() == tile.getY() && tile.getType() == CellType.EXIT)
                    state = GameState.SOLVED;
                sb.append("<td>\n");
                sb.append("<a href='" + String.format("%s/tilemazes?row=%s&column=%s", servletContext.getContextPath(), row, column) + "'>\n");
                sb.append(String.format("<img src='%s/images/%s.png'>", servletContext.getContextPath(), getImageName(tile)));
                sb.append("</a>\n");
                sb.append("</td>\n");
            }
            sb.append("</tr>\n");
        }
        sb.append("</table>\n");

        return sb.toString();
    }

    private String getImageName(Cell cell) {
        StringBuilder builder = new StringBuilder();

        if (player.getX() == cell.getX() && player.getY() == cell.getY())
            builder.append("player");

        if (cell.isWallRight())
            builder.append("right");
        if (cell.isWallBottom() || cell.getX() == weight - 1)
            builder.append("bottom");
        if (cell.getX() == 0 && cell.getType() != CellType.EXIT)
            builder.append("up");
        if (cell.getY() == 0 && cell.getType() != CellType.EXIT)
            builder.append("left");

        return builder.toString().isEmpty() ? "empty" : builder.toString();
    }

    private void prepareModel(Model model) {
        model.addAttribute("loggedUser", loggedUser);
        try {
            model.addAttribute("comments", commentService.getComments("tilemazes"));
            model.addAttribute("rating", ratingService.getRating("tilemazes", loggedUser));
        } catch (CommentException | RatingException e) {
            e.printStackTrace();
        }
//        model.addAttribute("ratingList", scoreService.getTopScores("mines"));
    }

    private void move(int row, int column) {
        if (player.getX() != row && player.getY() != column) {
            if (new Random().nextInt(2) % 2 == 0) {
                moveToThePos(player.getX(), column);
            } else {
                moveToThePos(row, player.getY());
            }
        } else {
            moveToThePos(row, column);
        }
    }

    private void moveToThePos(int row, int column) {
        while (true) {
            if (player.getX() == row && player.getY() == column)
                break;
            String move = getDirection(row, column);
            System.out.println(move);
            if (!player.isMovePossible(maze, move))
                break;
            else
                player.move(move);
        }
    }

    private String getDirection(int row, int column) {
        if (player.getX() > row)
            return "up";
        if (player.getX() < row)
            return "down";
        if (player.getY() > column)
            return "left";
        if (player.getY() < column)
            return "right";

        return null;
    }

}