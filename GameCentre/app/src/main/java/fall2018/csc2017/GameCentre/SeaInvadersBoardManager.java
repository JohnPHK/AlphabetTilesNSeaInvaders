package fall2018.csc2017.GameCentre;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class SeaInvadersBoardManager extends AbstractBoardManager implements Serializable {


    int lastOccupiedColumn;
    private int currentRound = 0;
    private ArrayList<Integer> invaderPositions = new ArrayList<>();
    private boolean gameOver = false;
    boolean puzzleSolved = false;

    /**
     * Manage a board that has been pre-populated.
     */
    SeaInvadersBoardManager(String user, SeaInvadersSettings seaInvadersSettings) {
        super(user, seaInvadersSettings, new SeaInvadersTileFactory());
        this.lastOccupiedColumn = seaInvadersSettings.getBoardSize() - 1;
    }

    /**
     * Return whether currentRound == numRounds, and all enemies killed
     *
     * @return whether the tiles are in row-major order
     */
    @Override
    boolean puzzleSolved() {
        return this.puzzleSolved;
    }

    void checkIfPuzzleIsSolved() {
        boolean noMoreInvaders = getInvaderPositions().size() == 0;
        boolean finishedFinalRound = this.currentRound == ((SeaInvadersSettings) this.gameSettings).getNumRounds();
        boolean gameAintOver = !gameOver();
        puzzleSolved = (noMoreInvaders && finishedFinalRound && gameAintOver);
    }

    /**
     * Return whether tap is in 1st row, and col is a new col
     *
     * @param position the tile to check
     * @return whether the tile at position is surrounded by a blank tile
     */
    @Override
    boolean isValidTap(int position) {
        if (this.lastOccupiedColumn == -1) {
            setLastOccupiedColumnToStart();
        }
        int row = position / getGameSettings().getBoardSize();
        int col = position % getGameSettings().getBoardSize();
        return (row == getGameSettings().getBoardSize() - 1 && col != this.lastOccupiedColumn);

    }


    /**
     * Process a touch at position in the board, swapping tiles as appropriate.
     *
     * @param position the position
     */
    @Override
    void touchMove(int position) {

        int col = position % this.getGameSettings().getBoardSize();
        if (isValidTap(position)) {
            board.swapTiles(
                    this.getGameSettings().getBoardSize() - 1,
                    col,
                    this.getGameSettings().getBoardSize() - 1,
                    this.lastOccupiedColumn);
            this.lastOccupiedColumn = col;
        }
    }

    @Override
    void swipeTo(int direction) {
    }


    /**
     * we loop over the enemy positions until we find one in this row
     * then we return this
     * we know this is the closest bc we sort in descending order
     * when we get invader positions
     * @param position the position of the shooter
     * @return Returns an integer representing the position of the closest enemy in the column, and -1 if no enemy is available.
     */
    int getClosestEnemyPosInThisCol(int position) {
        int shooterCol = position % this.getGameSettings().getBoardSize();
        for (int pos : getInvaderPositions()) {
            int enemyCol = pos % this.getGameSettings().getBoardSize();
            if (shooterCol == enemyCol) {
                return pos;
            }
        }
        return -1;
    }


    /**
     * return true if there's an enemy to shoot at
     *
     * @param position the position of the shooter
     * @return Returns true if there is a kraken to shoot, and false otherwise.
     */
    @Override
    boolean isValidShoot(int position) {
        int row = position / this.getGameSettings().getBoardSize();
        int col = position % this.getGameSettings().getBoardSize();
        return (row+1 == this.getGameSettings().getBoardSize() &&
                col == this.lastOccupiedColumn &&
                getClosestEnemyPosInThisCol(position) >= 0);
    }

    /**
     * fire at the enemy and update the grid
     * see MovementController processTapMovement for ideas on where to start
     * Should be somewhat similar to undo
     *
     * @param position the position of the shooter
     */
    @Override
    public void fireAndUpdate(int position) {
        int closestEnemy = getClosestEnemyPosInThisCol(position);
        board.updateTile(closestEnemy, new TileSeaInvader(- 1));
    }


    /**
     * start the invaders moving towards the bottom of the board
     * - for every invader on the board we need to move them down 1 at a time
     * based on the time inverval specified in SeaInvadersSettings
     */
    void spawnTheInvaders() {
        checkIfPuzzleIsSolved();
        if (!puzzleSolved() && !gameOver() &&
                this.currentRound < ((SeaInvadersSettings) this.gameSettings).getNumRounds()) {
            ArrayList<Integer> newSpawnPositions = getInvaderSpawnPositions();
            this.currentRound += 1;
            for (int pos : newSpawnPositions) {
                //            board.updateTile(pos, new InvaderTile());
                //            board.swapTiles(0, 0, 1, 1, false);
                board.updateTile(pos, new InvaderTile(), true);
            }
        }
    }


    /**
     * starts all the invaders swimming towards the bottom of the board
     * one row at a time
     */
    void swim() {
        checkIfPuzzleIsSolved();
        if (!puzzleSolved() && !gameOver()) {
            ArrayList<Integer> invaderPositions = getInvaderPositions();
            for (int pos : invaderPositions) {
                int row1 = pos / this.gameSettings.getBoardSize();
                int col1 = pos % this.gameSettings.getBoardSize();
                if (row1 + 1 < this.gameSettings.getBoardSize()) {
                    board.swapTiles(row1, col1, row1 + 1, col1, true);
                } else {
                    setGameOver(true);
                }
            }
        }
    }

    /**
     * loop over the board and identify all the invaders
     * @return Returns an ArrayList representing the positions of every enemy.
     */
    ArrayList<Integer> getInvaderPositions() {
        ArrayList<Integer> invaderPositions = new ArrayList<Integer>();
        Iterator boardIterator = board.iterator();
        for (int pos = 0;
             pos < this.gameSettings.getBoardSize() * this.gameSettings.getBoardSize();
             pos++) {
            int row1 = pos / this.gameSettings.getBoardSize();
            int col1 = pos % this.gameSettings.getBoardSize();
            if (board.getTile(row1, col1) instanceof InvaderTile) {
                invaderPositions.add(pos);
            }
        }
        Collections.sort(invaderPositions, Collections.<Integer>reverseOrder());
        // so when we swap tiles we swap from the bottom up
        return invaderPositions;
    }

    /**
     * return new positions for invader spawning
     * @return ArrayList of new spawn positions
     */
    ArrayList getInvaderSpawnPositions() {
        ArrayList positions = new ArrayList();

        for (int i = 0; i < this.gameSettings.getBoardSize(); i++) {
            positions.add(i);
        }
        Collections.shuffle(positions);
//        ArrayList randomPositions = (ArrayList) positions.subList(0,
        positions = new ArrayList(
                positions.subList(0,
                ((SeaInvadersSettings)  this.gameSettings).getNumSpawn()));
        return positions;
    }

    /**
     * get a score for this point in the game
     * @return double = score
     */
    @Override
    public double computeScore() {
        double round = getCurrentRound();
        boolean invadersLeft = getInvaderPositions().size() > 0;
        if (!invadersLeft) {
            return round * 2 + 1;
        } else {
            return round;
        }
    }

    @Override
    public void resetGame() {
        setCurrentRound(0);
        recomputeStartTime();
        List<Tile> tiles;
        tiles = (getTilesFactory()).getTiles(gameSettings.getBoardSize());
        setLastOccupiedColumnToStart();
        this.board.setTiles(tiles);
        setGameOver(false);
        this.puzzleSolved = false;
//        setBoard(new Board(tiles));
//        this.board = new Board(tiles);
//        this = new SeaInvadersBoardManager(
//                getUser(),
//                (SeaInvadersSettings) getGameSettings());

    }


    private SeaInvadersSettings getSeaInvaderSettings() {
        return (SeaInvadersSettings) getGameSettings();
    }

    private void setLastOccupiedColumnToStart() {
        this.lastOccupiedColumn = getSeaInvaderSettings().getBoardSize() - 1;
    }
//
//    public int getLastOccupiedColumn() {
//        return lastOccupiedColumn;
//    }
//
//    public void setLastOccupiedColumn(int lastOccupiedColumn) {
//        this.lastOccupiedColumn = lastOccupiedColumn;
//    }

    private int getCurrentRound() {
        return currentRound;
    }

    void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }
//
//    public void setInvaderPositions(ArrayList<Integer> invaderPositions) {
//        this.invaderPositions = invaderPositions;
//    }

    @Override
    public boolean gameOver() {
        return gameOver;
    }


    void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}
