package fall2018.csc2017.GameCentre;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import java.io.Serializable;

public class SeaInvadersGameActivity extends AbstractGameActivity implements Serializable {

    SeaInvadersBoardManager seaInvadersBoardManager;
    AppCompatActivity appCompatActivity = this;

    //https://stackoverflow.com/questions/4597690/android-timer-how-to

//    Handler timerHandler = new Handler();
    HandlerSerializable timerHandler = new HandlerSerializable();
    Runnable timerRunnable = new RunnerSerializable() {

        @Override
        public void run() {
             seaInvadersBoardManager.setAppCompatActivity(appCompatActivity);
            seaInvadersBoardManager.swim();
            seaInvadersBoardManager.spawnTheInvaders();
            if (seaInvadersBoardManager.gameOver()) {
                Toast.makeText(seaInvadersBoardManager.getAppCompatActivity(), "YOU LOSE! You're a Loser :) \nyou scored: " + seaInvadersBoardManager.computeScore(),
                        Toast.LENGTH_SHORT).show();
                seaInvadersBoardManager.updateScoreboard();
                seaInvadersBoardManager.resetGame();
                timerHandler.removeCallbacks(timerRunnable);
                timerHandler = null;
            }
            else {
                if (seaInvadersBoardManager.puzzleSolved()) {
                    Toast.makeText(seaInvadersBoardManager.getAppCompatActivity(), "YOU WON! Holy *!#$@#%!@#%:) \nyou scored: " + seaInvadersBoardManager.computeScore(),
                            Toast.LENGTH_SHORT).show();
                    seaInvadersBoardManager.updateScoreboard();
                    seaInvadersBoardManager.resetGame();
                    timerHandler.removeCallbacks(timerRunnable);
                    timerHandler = null;
                }
                else {
                    timerHandler.postDelayed(this,
                            1000 * (int) ((SeaInvadersSettings) seaInvadersBoardManager.gameSettings).getSecsBeforeMove());
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        seaInvadersBoardManager = SaveAndLoad.loadGameHubTemp(this).getSeaInvadersBoardManager();
        setAbstractBoardManager(seaInvadersBoardManager);
        super.onCreate(savedInstanceState);
         seaInvadersBoardManager.setAppCompatActivity(this);

        createTileButtons(this);
        setContentView(R.layout.activity_sea_invader);

        // Add View to activity
        gridView = findViewById(R.id.activity_sea_invader);
        gridView.setNumColumns(seaInvadersBoardManager.getGameSettings().getBoardSize());
        gridView.setAbstractBoardManager(seaInvadersBoardManager);
        seaInvadersBoardManager.getBoard().addObserver(this);
//        seaInvadersBoardManager.startGame();
        timerHandler.postDelayed(timerRunnable, 0);
        // Observer sets up desired dimensions as well as calls our display function
        final int COL_FINAL = seaInvadersBoardManager.getGameSettings().getBoardSize();
        final int ROW_FINAL = seaInvadersBoardManager.getGameSettings().getBoardSize();

        gridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        gridView.getViewTreeObserver().removeOnGlobalLayoutListener(
                                this);
                        int displayWidth = gridView.getMeasuredWidth();
                        int displayHeight = gridView.getMeasuredHeight();

                        columnWidth = displayWidth / COL_FINAL;
                        columnHeight = displayHeight / ROW_FINAL;

                        display();
                    }
                });
    }

    @Override
    protected void autoSave() {
        GameData gameData = SaveAndLoad.loadGameHubTemp(this);
        gameData.setSeaInvadersBoardManager(seaInvadersBoardManager);
        SaveAndLoad.saveGameHubPermanent(gameData, this);
        SaveAndLoad.saveGameHubTemp(gameData, this);
    }
}
