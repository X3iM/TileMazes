package sk.tuke.gamestudio.game.core;


public class Player {

    private int      x;
    private int      y;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void move(String move) {
        if (move == null)
            return;
        switch (move) {
            case "up": x -= 1;break;
            case "down": x += 1;break;
            case "left": y -= 1;break;
            case "right": y += 1;break;
        }
    }

    public boolean isMovePossible(Cell[][] maze, String move) {
        if (move == null)
            return false;
        if (move.equals("up") && (x > 0 && !maze[x-1][y].isWallBottom()))
            return true;
        else if (move.equals("down") && (x < maze.length-1 && !maze[x][y].isWallBottom()))
            return true;
        else if (move.equals("right") && !maze[x][y].isWallRight())
            return true;
        else return move.equals("left") && (y > 0 && !maze[x][y - 1].isWallRight());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}