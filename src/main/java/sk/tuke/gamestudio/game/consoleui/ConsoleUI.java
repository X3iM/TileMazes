package sk.tuke.gamestudio.game.consoleui;

import sk.tuke.gamestudio.game.core.GameDifficulty;
import sk.tuke.gamestudio.game.core.GameState;
import sk.tuke.gamestudio.game.core.Player;
import sk.tuke.gamestudio.game.core.Cell;
import sk.tuke.gamestudio.game.core.CellType;
import sk.tuke.gamestudio.game.core.mazegenerator.MazeGenerator;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.service.CommentService;
import sk.tuke.gamestudio.service.RatingService;
import sk.tuke.gamestudio.service.ScoreService;
import sk.tuke.gamestudio.game.core.utils.InputAnalyzer;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Random;
import java.util.stream.LongStream;

@Component
public class ConsoleUI {

    public static final String ANSI_BLUE   = "\u001B[34m";
    public static final String ANSI_WHITE  = "\u001B[37m";
    public static final String GAME_NAME = "tilemazes";
    public static final long   SECOND    = 1000000000L;

    private String             userName;

    private Player player;
    private GameState gameState;
    private GameDifficulty gameDifficulty;
    private InputAnalyzer analyzer;

    private final ScoreService scoreService;
    private RatingService ratingService;
    private CommentService commentService;

    private Cell[][] maze;
    private long     time;
    private int      score;
    private int      width;
    private int      height;

    public ConsoleUI(ScoreService scoreService, CommentService commentService, RatingService ratingService) {
        player = new Player(0, 0);
        gameState = GameState.PLAYING;
        userName = "";
        analyzer = new InputAnalyzer(new BufferedReader(new InputStreamReader(System.in)));

        gameDifficulty = GameDifficulty.MEDIUM;

        width = 2;
        height = 2;

        this.scoreService = scoreService;
        this.commentService = commentService;
        this.ratingService = ratingService;
    }

    public void play() {
        userName = analyzer.userInitialization(scoreService, gameDifficulty);

        while (true) {
//            analyzer.printBestScore(scoreService);
//            System.out.println(gameDifficulty.numeric());
//            width = (new Random().nextInt(gameDifficulty.numeric()+1)) * new Random(10).nextInt();
//            height = (new Random().nextInt(gameDifficulty.numeric()+1)) * (int)factorial(gameDifficulty.numeric());
            //System.out.println(width + " " + height);

            enterSize();
            int x1 = new Random().nextInt(width);
            int y1 = new Random().nextInt(height);
            player.setPosition(x1, y1);

            MazeGenerator mazeGenerator = new MazeGenerator(width, height, x1, y1);
            maze = mazeGenerator.getMaze();

            System.out.println("Good luck)");
            displayMaze();
            time = System.nanoTime() / SECOND;
            while (gameState != GameState.SOLVED) {
                player.move(maze, analyzer.enterPlayerMove());
                displayMaze();
                changeGameState();
            }
            int min = 0;
            long seconds = System.nanoTime()/SECOND - time;
            while (seconds > 60) {
                seconds -= 60;
                min++;
            }

            System.out.println(("Congratulations! You completed this level in " + min +" minutes, " + seconds +" seconds"));
            countOfScore();
            addScore();
            //analyzer.addComment(commentService, userName);
            analyzer.addRating(ratingService, userName);

            if (!analyzer.endLevel(time))
                break;
        }
    }

    private void enterSize() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please enter width and height(>1): ");
        String answer = "";
        int x = 0;
        int y = 0;
        do {
            try {
                answer = reader.readLine();
                if (!answer.isEmpty()) {
                    x = Integer.parseInt(answer.split(" ")[0]);
                    y = Integer.parseInt(answer.split(" ")[1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (x <= 1 || y <= 1);

        width = x;
        height = y;
    }

    private long factorial(int n) {
        return LongStream.rangeClosed(1, n)
                .reduce(1, (long x, long y) -> x * y);
    }

    private void countOfScore() {
        score = width*height;
    }

    private void addScore() {
        scoreService.addScore(new Score(GAME_NAME, userName, score, new Date()));
    }

    private void displayMaze() {
        for (int i = 0; i < height; i++) {
            if (maze[0][i].getType() != CellType.EXIT) System.out.print(ANSI_BLUE + "+---");
            else System.out.print(ANSI_BLUE + "+   ");
        }
        System.out.println("+");

        for (int i = 0; i < width; i++) {
            if (maze[i][0].getType() != CellType.EXIT) System.out.print("|");
            else System.out.print(" ");

            for (int j = 0; j < height; j++) {
                String print = "";
                if (maze[i][j].isWallRight())
                    print = "   |";
                else
                    print = "    ";

                if (i == player.getX() && j == player.getY())
                    print = (print.equals("   |")) ? " 0 |" : " 0  ";

                System.out.print(print);
            }
            System.out.println();
            for (int j = 0; j < height; j++) {
                if (maze[i][j].isWallBottom())
                    System.out.print("+---");
                else
                    System.out.print("+   ");
            }
            System.out.println("+");
        }
        System.out.println(ANSI_WHITE);
    }

    public GameDifficulty getGameDifficulty() {
        return gameDifficulty;
    }

    private void changeGameState() {
        if (maze[player.getX()][player.getY()].getType() == CellType.EXIT)
            gameState = GameState.SOLVED;
    }

    private boolean isCorrectUserName() {
        return (userName.charAt(0) >= 'A' || userName.charAt(0) <= 'Z') && (userName.charAt(0) >= 'a' || userName.charAt(0) <= 'z');
    }
}