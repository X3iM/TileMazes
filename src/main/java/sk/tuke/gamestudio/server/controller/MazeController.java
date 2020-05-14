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
import sk.tuke.gamestudio.game.consoleui.ConsoleUI;
import sk.tuke.gamestudio.game.core.Cell;
import sk.tuke.gamestudio.game.core.Player;
import sk.tuke.gamestudio.game.core.mazegenerator.MazeGenerator;
import sk.tuke.gamestudio.service.CommentException;
import sk.tuke.gamestudio.service.CommentService;
import sk.tuke.gamestudio.service.RatingException;
import sk.tuke.gamestudio.service.RatingService;

import javax.servlet.ServletContext;
import java.util.Date;
import java.util.Random;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
@RequestMapping("/tilemazes")
public class MazeController {

    public final int weight = 20;

    private Player      player;
    private Cell[][]    maze;
    private String      loggedUser;

    @Autowired
    private ServletContext  servletContext;
    @Autowired
    private RatingService   ratingService;
    @Autowired
    private CommentService  commentService;

    @GetMapping()
    public String tileMazes(String row, String column, Model model) {
        if (maze == null)
            newGame(model);
        else {

            prepareModel(model);
        }
        return "tilemazes";
    }

    @RequestMapping("/new")
    public String newGame(Model model) {
        Random rand = new Random();
        maze = new MazeGenerator(weight, weight, rand.nextInt(weight), rand.nextInt(weight)).getMaze();
        player = new Player(rand.nextInt(weight), rand.nextInt(weight));
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
        } catch (CommentException e) {e.printStackTrace();}
        return "tilemazes";
    }

    @RequestMapping("/rategame")
    public String rateGame(String rating) {
        int rate = Integer.parseInt(rating);
        try {
            ratingService.setRating(new Rating(loggedUser, "tilemazes", rate, new Date()));
        } catch (RatingException e) {e.printStackTrace();}
        return "tilemazes";
    }

    public String getHtmlField() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class='field'>\n");
        for (int row = 0; row < weight; row++) {
            sb.append("<tr>\n");
                for (int column = 0; column < weight; column++) {
                Cell tile = maze[row][column];
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

        if (player.getX() == cell.getX() && player.getY() == cell.getY()) {
            builder.append("player");
            if (cell.isWallBottom())
                builder.append("bottom");
            if (cell.isWallRight())
                builder.append("right");
            if (cell.getX() == 0)
                builder.append("up");
            if (cell.getY() == 0)
                builder.append("left");

            return builder.toString();
        }

        if (cell.isWallRight())
            builder.append("right");
        if (cell.isWallBottom())
            builder.append("bottom");

        return builder.toString().isEmpty() ? "empty" : builder.toString();
    }

    private void prepareModel(Model model) {
        model.addAttribute("loggedUser", loggedUser);
        try {
            model.addAttribute("comments", commentService.getComments("tilemazes"));
        } catch (CommentException e) {e.printStackTrace();}
//        model.addAttribute("ratingList", scoreService.getTopScores("mines"));
    }

}