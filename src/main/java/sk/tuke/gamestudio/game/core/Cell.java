package sk.tuke.gamestudio.game.core;

public class Cell {

    private int     x;
    private int     y;
    private boolean wallRight;
    private boolean wallBottom;
    private boolean visited;
    private int     distance;

    private CellType type;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;

        type = CellType.EMPTY;
        distance = 0;
        wallBottom = true;
        wallRight = true;
        visited = false;
    }

    public boolean isWallRight() {
        return wallRight;
    }

    public void setWallRight(boolean wallRight) {
        this.wallRight = wallRight;
    }

    public boolean isWallBottom() {
        return wallBottom;
    }

    public void setWallBottom(boolean wallBottom) {
        this.wallBottom = wallBottom;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }
}