package sk.tuke.gamestudio.game.core;

public enum GameDifficulty {
    EASY(1), MEDIUM(2), HARD(3), CRAZY(4);

    private int n;

    GameDifficulty(int n) {
        this.n = n;
    }

    public int numeric() {
        return n;
    }
}