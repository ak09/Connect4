package edu.nyu.pqs.connect4.model;

/**
 * Enums used for different purposes in both model and listener
 * 
 * @author ajaykhanna
 * 
 */
public class GameEnums {

  /**
   * Type of the Player None - means No Player
   * 
   * @author ajaykhanna
   * 
   */
  public enum PlayerType {
    PLAYER1, PLAYER2, NONE
  };

  /**
   * Game types that are supported by the model
   * 
   * @author ajaykhanna
   * 
   */
  public enum GameType {
    SINGLEPLAYER, TWOPLAYER;
  }

}