package sk.tuke.gamestudio.game.core.utils;

import org.springframework.http.converter.json.GsonBuilderUtils;
import org.w3c.dom.ls.LSOutput;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.game.consoleui.ConsoleUI;
import sk.tuke.gamestudio.game.core.GameDifficulty;
import sk.tuke.gamestudio.service.CommentService;
import sk.tuke.gamestudio.service.RatingService;
import sk.tuke.gamestudio.service.ScoreService;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

import static sk.tuke.gamestudio.game.consoleui.ConsoleUI.GAME_NAME;

public class InputAnalyzer {

    private BufferedReader reader;

    public InputAnalyzer(BufferedReader reader) {
        this.reader = reader;
    }

    public String userInitialization(ScoreService scoreService, GameDifficulty difficulty) {
        String userName = "";
        try {
            System.out.print("Pleas enter your name: ");
            userName = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        List<String> userNameList = new ArrayList<>();
//        scoreService.getBestScores(ConsoleUI.GAME_NAME).forEach(score -> userNameList.add(score.getPlayer()));
//
//        while (userNameList.contains(userName)) {
//            try {
//                System.out.print("This username(" + userName + ") is already in use, pleas enter another user name: ");
//                userName = reader.readLine();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        System.out.print("Want you choose a difficulty level(default medium)?(yes/no) ");
        try {
            String answer = reader.readLine();
            if (!answer.isEmpty() && answer.toLowerCase().charAt(0) == 'y') {
                System.out.print("Please enter easy(1), hard(3) or if you're crazy try the highest level of difficult \"crazy\"(4)) ");
                answer = reader.readLine();
                if (!answer.isEmpty()) {
                    switch (answer.toLowerCase()) {
                        case "easy": case "1":
                            difficulty = GameDifficulty.EASY;
                            break;
                        case "medium": case "3":
                            difficulty = GameDifficulty.MEDIUM;
                            break;
                        case "crazy": case "4":
                            difficulty = GameDifficulty.CRAZY;
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userName;
    }

    public String enterPlayerMove() {
        String move = "";

        try {
            System.out.print("Pleas enter direction: ");
            move = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(!(move.equals("up") || move.equals("right") || move.equals("left") || move.equals("down"))) {
            try {
                System.out.print("Please enter right direction(up, down, left or right): ");
                move = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return move;
    }

    public void addComment(CommentService commentService, String userName) {
        System.out.println("Would you like to comment on the game?)(yes/no)");
        try {
            String answer = reader.readLine();
            if (answer.equals("yes")) {
                System.out.println("Ohh, thank you so much");
                String comment = reader.readLine();
                while (comment.isEmpty()) {
                    System.out.println("Please, enter you comment");
                    comment = reader.readLine();
                }
                Comment comment1 = new Comment(userName, GAME_NAME, comment, new Date());
                commentService.addComment(comment1);

            } else {
                System.out.println("Thank you, i hope you enjoyed my game. See you soon");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addRating(RatingService ratingService, String userName) {
        System.out.println("Would you like to rating on the game?)");
        try {
            String answer = reader.readLine();
            if (answer.equals("yes")) {
                System.out.println("Ohh, thank you so much");
                String comment = reader.readLine();
                while (comment.isEmpty()) {
                    System.out.println("Please, enter you comment");
                    comment = reader.readLine();
                }
                ratingService.setRating(new Rating(userName, GAME_NAME, 9, new Date()));
            } else {
                System.out.println("Thank you, i hope you enjoyed my game. See you soon");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean endLevel(long time) {

        //System.out.println("Congratulations! You completed this level in " + (System.nanoTime()/SECOND - time) + " seconds");
        String answer = "";
        try {
            System.out.print("Would you play again?(y/n)");
            answer = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer.charAt(0) == 'y';
    }

    public void printBestScore(ScoreService scoreService) {
        if (!scoreService.getBestScores(GAME_NAME).isEmpty()) {
            System.out.println("Would you like to see a list of the best players?(yes/no)");
            try {
                String answer = reader.readLine();
                if (!answer.isEmpty() && answer.toLowerCase().charAt(0) == 'y') {
                    System.out.println("Best players:");
                    scoreService.getBestScores(ConsoleUI.GAME_NAME).forEach(score -> System.out.println("\t" + score.toString()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}