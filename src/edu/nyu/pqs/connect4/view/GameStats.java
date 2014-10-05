package edu.nyu.pqs.connect4.view;

import java.util.HashMap;
import java.util.Map;
import edu.nyu.pqs.connect4.listener.Connect4Listener;
import edu.nyu.pqs.connect4.model.Connect4Model;
import edu.nyu.pqs.connect4.model.GameEnums.GameType;
import edu.nyu.pqs.connect4.model.GameEnums.PlayerType;

/**
 * Logger view for keeping track of games among different models. It implements
 * Singleton.
 * 
 * @author ajaykhanna
 * 
 */
public class GameStats implements Connect4Listener {
  private Long gameNumber;
  private final StringBuilder LOG;
  private final Map<Connect4Model, Long> models;
  private final static Connect4Listener LOGGER = new GameStats();

  private GameStats() {
    gameNumber = 0L;
    LOG = new StringBuilder();
    models = new HashMap<Connect4Model, Long>();
  }

  public static Connect4Listener getInstance() {
    return LOGGER;
  }

  @Override
  public void gameStarted(PlayerType turn, GameType gameType,
      Connect4Model model) {
    if (models.containsKey(model)) {
      LOG.append("Model is already present and accounted for. Model is related to Game "
          + models.get(model) + ".\n");
    } else {
      models.put(model, ++gameNumber);
      LOG.append("Game " + models.get(model) + ": " + "Started.\n");
    }
  }

  @Override
  public void gameStopped(Connect4Model model) {
    if (models.containsKey(model)) {
      LOG.append("Game " + models.get(model) + ": " + "Stopped.\n");
      models.remove(model);
    } else {
      LOG.append("Model is already removed.\n");
    }
  }

  @Override
  public void playNextMove(int row, int col, PlayerType owner,
      Connect4Model model) {
  }

  @Override
  public void gameWon(int row, int col, PlayerType owner, Connect4Model model) {
    if (models.containsKey(model)) {
      LOG.append(owner + " Won the  game " + models.get(model) + " .\n");
      models.remove(model);
    } else {
      LOG.append("Model is already removed./n");
    }
  }

  @Override
  public void gameDraw(Connect4Model model) {
    if (models.containsKey(model)) {
      LOG.append("Game " + models.get(model) + ": ended in a Draw.\n");
      models.remove(model);
    } else {
      LOG.append("Model is already removed.\n");
    }
  }

  /**
   * @return String form of LOG
   */
  public String toString() {
    return LOG.toString();
  }
}
