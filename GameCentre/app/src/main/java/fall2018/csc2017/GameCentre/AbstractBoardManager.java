package fall2018.csc2017.GameCentre;

import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;
import java.util.List;
import java.util.Stack;

abstract public class AbstractBoardManager implements Serializable {

    /**
     * The board being managed.
     */
    protected Board board;
    public double score;
    private String user;
    GameSettings gameSettings;
    Stack<int[]> moves = new Stack<>();
    int moveCount;
    private long startTime;
    private AppCompatActivity appCompatActivity;
    private AbstractTilesFactory tilesFactory;

    /**
     * Manage a board that has been pre-populated.
     * @param board the board
     * @param user instance of class user
     * @param gameSettings child class of class gameSettings
     * @param appCompatActivity from the activity you're working on
     *                          allows us to do saving and loading to scoreboard
     */
    AbstractBoardManager(Board board, String user, GameSettings gameSettings,
                 AppCompatActivity appCompatActivity,
                         AbstractTilesFactory tilesFactory) {
        this.board = board;
        this.user = user;
        this.gameSettings = gameSettings;
        this.moveCount = 0;
        this.moves = new Stack<>();
        this.startTime = System.nanoTime();
        this.appCompatActivity = appCompatActivity;
        this.tilesFactory = tilesFactory;
    }

    /**
     * Manage a board that has been pre-populated.
     * @param board the board
     * @param user instance of class user
     * @param gameSettings child class of class gameSettings
     */
    AbstractBoardManager(Board board, String user, GameSettings gameSettings,
                         AbstractTilesFactory tilesFactory) {
        this.board = board;
        this.user = user;
        this.gameSettings = gameSettings;
        this.moveCount = 0;
        this.moves = new Stack<>();
        this.startTime = System.nanoTime();
        this.tilesFactory = tilesFactory;
    }

    /**
     * Return the current board.
     */
    Board getBoard() {
        return board;
    }

    /**
     * Manage a new board.
     */
    AbstractBoardManager(String user, GameSettings gameSettings,  AbstractTilesFactory tilesFactory) {
        List<Tile> tiles;
        tiles = tilesFactory.getTiles(gameSettings.getBoardSize());

        this.tilesFactory = tilesFactory;
        this.board = new Board(tiles);
        this.user = user;
        this.gameSettings = gameSettings;
    }


//    TODO: get rid of collections.shuffle(tiles) and replace with the method.
//    public void setBoardSize(int boardSize) throws Exception {
//        if (this.tilesFactory != null) {
//            List<Tile> tiles = this.tilesFactory.getTiles(boardSize);
//            this.gameSettings.setBoardSize(boardSize);
//            this.board.setTiles(tiles);
//        } else {
//            throw new Exception("tileFactory is null");
//        }
//    }

    /**
     * Sets the board size and generates and sets a new copy of list of tiles for the board.
     *
     * @param boardSize size of the board indicated by integer.
     */
    public void setBoardSize(int boardSize) {
        List<Tile> tiles = this.tilesFactory.getTiles(boardSize);
        this.gameSettings.setBoardSize(boardSize);
        this.board.setTiles(tiles);

    }


    /**
     * implement
     *
     * @return whether the game is won
     */
    abstract boolean puzzleSolved();

    /**
     * Check if end-game condition is met
     */
    abstract boolean gameOver();

    /**
     * Procecss a swipe by direction, swiping tiles to the direction as appropriate
     *
     * @param direction the direction (0 is up, 1 is down, 2 is left, 3 is right)
     */
    abstract void swipeTo(int direction);

    abstract boolean isValidTap(int position);

    /**
     * Process a touch at position in the board, swapping tiles as appropriate.
     *
     * @param position the position
     */
    abstract void touchMove(int position);

    boolean isValidUndo(int position) {
        return false;
    }

    void tapUndo(int position) {
    }

    boolean isValidShoot(int position) {return false;}

    public void fireAndUpdate(int position) {};

    public boolean moveIsEmpty() {
        return moves.empty();
    }


    int getMoveCount() {
        return this.moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }



    public void setBoard(Board board) {
        this.board = board;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    GameSettings getGameSettings() {
        return gameSettings;
    }

    public void setGameSettings(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    public void setAppCompatActivity(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }

    double getTimePlayed() {
        long endTime = System.nanoTime();
        return (double) (endTime - this.startTime) / 1000000000.0;
    }

    /**
     * your scoring function
     * @return score in the form of double.
     */
    abstract public double computeScore();

    public double getScore() {
        return this.score;
    }

    /**
     * load update and then save the scoreboard
     */
    void updateScoreboard() {
        this.score = computeScore();
        if (this.appCompatActivity != null) {
            ScoreBoard.addScoreToSavedScoreboard(this.user,
                    this.score,
                    this.gameSettings.copy(),
                    this.appCompatActivity);
        } else {
            System.out.println("app compat activity not defined");
        }
    }

    void recomputeStartTime() {
        this.startTime = System.nanoTime();
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    AbstractTilesFactory getTilesFactory() {
        return tilesFactory;
    }

    public void resetGame() {}


}
