package sk.tuke.gamestudio.game.core.mazegenerator;

import sk.tuke.gamestudio.game.core.Cell;
import sk.tuke.gamestudio.game.core.CellType;

import java.util.*;

public class MazeGenerator {

    private int      Width;
    private int      Height;
    private int      countOfKey;
    private Cell[][] maze;

    public MazeGenerator(int x1, int y1, int playerX, int playerY) {
        Width = x1;
        Height = y1;
        maze = new Cell[Width][Height];

        for (int x = 0; x < maze.length; x++) {
            for (int y = 0; y < maze[0].length; y++)
                maze[x][y] = new Cell(x, y);
        }

        generate(playerX, playerY);
        for (Cell[] cells : maze) cells[Height - 1].setWallRight(true);
        for (int y = 0; y < maze[1].length; y++)
            maze[Width - 1][y].setWallBottom(true);

        placeExit(playerX, playerY);
    }

    private void generate(int playerX, int playerY) {

        Cell current = maze[playerX][playerY];
        current.setVisited(true);

        Stack<Cell> stack = new Stack<>();
        do {
            List<Cell> unvisitedNeighbours = new ArrayList<>();

            int x = current.getX();
            int y = current.getY();

            if (x > 0 && !maze[x - 1][y].isVisited()) unvisitedNeighbours.add(maze[x - 1][y]);
            if (y > 0 && !maze[x][y - 1].isVisited()) unvisitedNeighbours.add(maze[x][y - 1]);
            if (x < Width - 1 && !maze[x + 1][y].isVisited()) unvisitedNeighbours.add(maze[x + 1][y]);
            if (y < Height - 1 && !maze[x][y + 1].isVisited()) unvisitedNeighbours.add(maze[x][y + 1]);

            if (unvisitedNeighbours.size() > 0) {
                Cell chosen = unvisitedNeighbours.get(new Random().nextInt(unvisitedNeighbours.size()));
                removeWall(current, chosen);

                chosen.setVisited(true);
                stack.push(chosen);
                current = chosen;
                chosen.setDistance(stack.size());
            } else {
                current = stack.pop();
            }
        } while (stack.size() > 0);
    }

    private void removeWall(Cell a, Cell b) {
        if (a.getX() == b.getX()) {
            if (a.getY() < b.getY()) a.setWallRight(false);
            else b.setWallRight(false);
        } else {
            if (a.getX() < b.getX()) a.setWallBottom(false);
            else b.setWallBottom(false);
        }
    }

    private void placeExit(int x, int y) {
        Cell current = maze[x][y];

        for (int i = 0; i < maze.length; i++) {
            if (maze[i][Height - 1].getDistance() > current.getDistance())
                current = maze[i][Height - 1];
            if (maze[i][0].getDistance() > current.getDistance())
                current = maze[i][0];
        }

        for (int i = 0; i < maze[0].length; i++) {
            if (maze[Width - 1][i].getDistance() > current.getDistance())
                current = maze[Width - 1][i];
            if (maze[0][i].getDistance() > current.getDistance())
                current = maze[0][i];
        }
//        System.out.println("\n" + current.getX() + " " + current.getY());

        current.setType(CellType.EXIT);
        if (current.getX() == Width - 1 && current.getY() > 0) current.setWallBottom(false);
        else if (current.getY() == Height - 1 && current.getX() > 0) current.setWallRight(false);

    }

    public Cell[][] getMaze() {
        return maze;
    }



    /*private Cell maze[][];
    private int  x;
    private int  y;

    public MazeGenerator(int x, int y) {
        maze = new Cell[y][x];
        this.x = x;
        this.y = y;

        fillMaze();

//        for (int i = 0; i < x; i++)
//            maze[i][y-1].setWallLeft(false);
//        for (int i = 0; i < x; i++)
//            maze[x-1][i].setWallBottom(false);

        generateMaze();
    }

    private void fillMaze() {
        for(int i = 0; i < y; i++) {
            for(int j = 0; j < x; j++)
                maze[i][j] = new Cell(i, j);
        }
    }*/

    /*private void generateMaze() {
        //Cell current = maze[new Random().nextInt(x)][new Random().nextInt(y)];
        Cell current = maze[0][0];
        current.setVisited(true);

        Stack<Cell> stack = new Stack<>();
        do {
            List<Cell> unvisitedNeighbours = new ArrayList<>();
            int X = current.getX();
            int Y = current.getY();

            if (X > 0 && !maze[X - 1][Y].isVisited()) unvisitedNeighbours.add(maze[X - 1][Y]);
            if (Y > 0 && !maze[X][Y - 1].isVisited()) unvisitedNeighbours.add(maze[X][Y - 1]);

            if ((X < this.x - 2) && !maze[X + 1][Y].isVisited()) unvisitedNeighbours.add(maze[X + 1][Y]);
            if ((Y < this.y - 2) && !maze[X][Y + 1].isVisited()) unvisitedNeighbours.add(maze[X][Y + 1]);

            if (unvisitedNeighbours.size() > 0) {
//                System.out.println(unvisitedNeighbours.size());
                Cell randomCell = unvisitedNeighbours.get(new Random().nextInt(unvisitedNeighbours.size()));
                removeWall(current, randomCell);

                randomCell.setVisited(true);
                stack.push(current);
                current = randomCell;
            } else {
//                Cell[] cells = new Cell[stack.size()];
//                for (int i = 0; i < stack.size(); i++)
//                    cells[i] = stack.get(i);
//
//                Arrays.stream(cells).forEach(cell -> {
//                    System.out.print(" X " + cell.getX() + " Y " + cell.getY());
//                });
//                System.out.println();
                current = stack.pop();
            }
        } while (!stack.empty());

//        DIR[] dirs = DIR.values();
//        Collections.shuffle(Arrays.asList(dirs));
//        for (DIR dir : dirs) {
//            int nx = cx + dir.dx;
//            int ny = cy + dir.dy;
//            if (between(nx, x) && between(ny, y)
//                    && (maze[nx][ny] == 0)) {
//                maze[cx][cy] |= dir.bit;
//                maze[nx][ny] |= dir.opposite.bit;
//                generateMaze(nx, ny);
//            }
//        }
    }*/

    /*private void removeWall(@NotNull Cell current, @NotNull Cell randomCell) {
        System.out.println("currentX " + current.getX() + " Y " + current.getY());
        System.out.println("randomCellX " + randomCell.getX() + " Y " + randomCell.getY());

        if (current.getY() == randomCell.getY()) {
            if (current.getX() > randomCell.getX()) {
                current.setWallLeft(false);
                System.out.println("delete current WallBottom");
            }
            else {
                randomCell.setWallLeft(false);
                System.out.println("delete randomCell WallBottom");
            }
        } else {
            if (current.getY() > randomCell.getY()) {
                current.setWallBottom(false);
                System.out.println("delete current WallLeft");
            }
            else {
                randomCell.setWallBottom(false);
                System.out.println("delete randomCell WallLeft");
            }
        }
    }*/

//    private static boolean between(int v, int upper) {
//        return (v >= 0) && (v < upper);
//    }
//
//    private enum DIR {
//
//        N(1, 0, -1),//^
//        S(2, 0, 1),
//        E(4, 1, 0),//>
//        W(8, -1, 0);//<
//
//        private final int bit;
//        private final int dx;
//        private final int dy;
//        private DIR opposite;
//
//        static {
//            N.opposite = S;
//            S.opposite = N;
//            E.opposite = W;
//            W.opposite = E;
//        }
//
//        private DIR(int bit, int dx, int dy) {
//            this.bit = bit;
//            this.dx = dx;
//            this.dy = dy;
//        }
//    };


}