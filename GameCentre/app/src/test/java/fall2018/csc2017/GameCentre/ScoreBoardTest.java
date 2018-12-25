package fall2018.csc2017.GameCentre;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ScoreBoardTest {

    String user1 = "bill";
    String user2 = "bob";
    GameSettings gameSettings1 = new SlidingTilesSettings(4,4);
    GameSettings gameSettings2 = new SeaInvadersSettings(4,4);
    GameSettings gameSettings3 = new SlidingTilesSettings(3,3);
    ScoreBoard scoreBoard;

    @Before
    public void setUp() {
        scoreBoard = new ScoreBoard(user1, gameSettings1);
    }

    @Test
    public void testUpdateScoreBoard() {
    }

    @Test
    public void testIsNewGame() {
        scoreBoard.setGameSettings(gameSettings2);
        assertTrue(scoreBoard.isNewGame());
        scoreBoard.updateScoreBoard(2.0);
        assertFalse(scoreBoard.isNewGame());
        scoreBoard.setUser(user2);
        assertFalse(scoreBoard.isNewGame());
    }

    @Test
    public void testIsNewGameForUser() {
        assertTrue(scoreBoard.isNewGameForUser());
        scoreBoard.updateScoreBoard(2.0);
        assertFalse(scoreBoard.isNewGameForUser());
        scoreBoard.setUser(user2);
        assertTrue(scoreBoard.isNewGameForUser());
    }



    public void testUpdateGameScoreboard(long score, int size) {
        boolean isNewGame = scoreBoard.isNewGame();
        scoreBoard.updateGameScoreBoard(score);
        assertEquals(scoreBoard.getPerGameScoreBoard().size(), size);
        if (isNewGame) {
            assertEquals((long) scoreBoard.getPerGameScoreBoard().get(gameSettings1.getGameId()).getMaxScore(),
                    score);
        }
    }

    public void testUpdateUserScoreboard(long score, int size) {
        boolean isNewGame = scoreBoard.isNewGameForUser();
        scoreBoard.updateUserScoreBoard(score);
        assertEquals(scoreBoard.getPerUserScoreBoard().size(), size);
        if (isNewGame) {
            long storedScore = (long) scoreBoard.getPerUserScoreBoard().get(gameSettings1.getGameId()).getMaxScore();
            assertEquals(storedScore,
                    score);
        }
    }

    @Test
    public void testUpdateUserScoreBetterScore() {
        testUpdateUserScoreboard(1, 1);
        testUpdateUserScoreboard(2, 1);
        assertEquals(
                (long) scoreBoard.getPerUserScoreBoard().get(gameSettings1.getGameId()).getMaxScore(),
                (long) 2);
    }

    @Test
    public void testUpdateUserScoreWorseScore() {
        testUpdateUserScoreboard(1,
                1);
        testUpdateUserScoreboard((long) .5, 1);
        assertEquals(
                (long) scoreBoard.getPerUserScoreBoard().get(gameSettings1.getGameId()).getMaxScore(),
                (long) 1);
    }

    @Test
    public void testUpdateGameScoresBetterScore() {
        testUpdateGameScoreboard(1, 1);
        testUpdateGameScoreboard(2, 1);
        assertEquals(
                (long) scoreBoard.getPerGameScoreBoard().get(gameSettings1.getGameId()).getMaxScore(),
                (long) 2);
    }

    @Test
    public void testGameScoreboardAddingNewGames() {
        testUpdateGameScoreboard(1, 1);
        scoreBoard.setGameSettings(gameSettings3);
        testUpdateGameScoreboard(1, 2);
        scoreBoard.setGameSettings(gameSettings2);
        testUpdateGameScoreboard(1, 3);
    }

    @Test
    public void testUpdateGameScoresWorseScore() {
        testUpdateGameScoreboard(1, 1);
        testUpdateGameScoreboard((long) .5, 1);
        assertEquals(
                (long) scoreBoard.getPerGameScoreBoard().get(gameSettings1.getGameId()).getMaxScore(),
                1);
    }

    @Test
    public void setAndGetScoreBoards() {
        HashMap perGameScoreboard = scoreBoard.getPerGameScoreBoard();
        HashMap perUserScoreboard = scoreBoard.getPerUserScoreBoard();
        ScoreBoard newScoreboard = new ScoreBoard(user2, gameSettings2);
        newScoreboard.setPerUserScoreBoard(perUserScoreboard);
        newScoreboard.setPerGameScoreBoard(perGameScoreboard);
        assertEquals(newScoreboard.getPerUserScoreBoard(),
                scoreBoard.getPerUserScoreBoard());
        assertEquals(newScoreboard.getPerGameScoreBoard(),
                scoreBoard.getPerGameScoreBoard());
    }

    @Test
    public void getAndSetUser() {
        scoreBoard.setUser(user1);
        assertEquals(scoreBoard.getUser(), user1);
    }

    @Test
    public void getAndSetGameSettings() {
        scoreBoard.setGameSettings(gameSettings3);
        assertEquals(scoreBoard.getGameSettings(), gameSettings3);
    }

}