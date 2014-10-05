package edu.nyu.pqs.connect4.listener;

import edu.nyu.pqs.connect4.model.Connect4Model;
import edu.nyu.pqs.connect4.model.GameEnums;

/**
 * This the listener interface for Connect4 that the views will implement.
 * 
 * @author ajaykhanna
 */
public interface Connect4Listener {

  /**
   * Model fires this event when the game is started or when some view joins in
   * between a game that is already being started.
   * 
   * @param turn Who's Turn it is.
   * @param gameType Type of the game
   * @param model Reference to the model
   */
  public void gameStarted(GameEnums.PlayerType turn,
      GameEnums.GameType gameType, Connect4Model model);

  /**
   * Model fires this event when the game is stopped for the particular view
   * 
   * @param model Reference to the model
   */
  public void gameStopped(Connect4Model model);

  /**
   * Model fires this event to let the view now where to move in the grid.
   * 
   * @param row Row Location of the move
   * @param col Column Location of the move
   * @param owner Who played this move
   * @param model Reference to the model
   */
  public void playNextMove(int row, int col, GameEnums.PlayerType owner,
      Connect4Model model);

  /**
   * Model fires this event when someone wins the game.
   * 
   * @param row Row Location of the winning move
   * @param col Column Location of the winning move
   * @param owner Who played the winning move
   * @param model Reference to the model
   */
  public void gameWon(int row, int col, GameEnums.PlayerType owner,
      Connect4Model model);

  /**
   * Model fires this event when the game is draw.
   * 
   * @param model Reference to the model
   */
  public void gameDraw(Connect4Model model);
}
