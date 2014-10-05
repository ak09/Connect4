package edu.nyu.pqs.connect4.view;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import edu.nyu.pqs.connect4.model.GameEnums;
import edu.nyu.pqs.connect4.model.GameEnums.PlayerType;

/**
 * This class contains the setting of the game. It uses builder pattern.
 * 
 * @author ajaykhanna
 * 
 */
public class GameSettings {
  private Map<GameEnums.PlayerType, Player> playerInfo;
  private static final GameSettings INSTANCE = new GameSettings(new Builder());
  private int circleRadius;
  private int frameSize;

  /**
   * Constructor of GameSetting
   * 
   * @param b Builder that is used for creation of GameSettings
   */
  private GameSettings(Builder b) {
    playerInfo = new HashMap<GameEnums.PlayerType, Player>(2);
    playerInfo.put(PlayerType.PLAYER1, b.player1);
    playerInfo.put(PlayerType.PLAYER2, b.player2);
    circleRadius = b.circleRadius;
    frameSize = b.frameSize;
  }

  public static class Builder {
    private final int DEFAULT_RADIUS = 15;
    private final int DEFAULT_FRAME_SIZE = 600;
    private final String DEFAULT_PLAYER1_NAME = "Player1";
    private final String DEFAULT_PLAYER2_NAME = "Player2";
    private Player player1;
    private Player player2;
    private int circleRadius;
    private int frameSize;

    /**
     * Builder constructor that assigns default values to the builder properties
     * Default color for Player1 is Color.RED Default color for Player2 is
     * Color.YELLOW
     */
    public Builder() {
      player1 = new Player(DEFAULT_PLAYER1_NAME, Color.RED);
      player2 = new Player(DEFAULT_PLAYER2_NAME, Color.YELLOW);
      circleRadius = DEFAULT_RADIUS;
      frameSize = DEFAULT_FRAME_SIZE;
    }

    /**
     * If player is null then just return with default player. If player name is
     * null or empty then keep default player1 name If player disc color is null
     * or Color.LIGHT_GRAY or is same as player2 then keep default player1 disc
     * color
     * 
     * @param p Player reference that will be used to set builder's player
     * @return Builder reference
     */
    public Builder setPlayer1(Player p) {
      String name = DEFAULT_PLAYER1_NAME;
      Color color = Color.RED;
      if (p == null) {
        return this;
      } else {
        if (p.getName() != null || !p.getName().isEmpty()) {
          name = p.getName();
        }
        if (p.getDiscColor() != null || p.getDiscColor() != Color.LIGHT_GRAY
            || !player2.getDiscColor().equals(p.getDiscColor())) {
          color = p.getDiscColor();
        }
        player1 = new Player(name, color);
      }
      return this;
    }

    /**
     * If player is null then just return with default player. If player name is
     * null or empty then keep default player2 name If player disc color is null
     * or Color.LIGHT_GRAY or is same as player1 then keep default player2 disc
     * color
     * 
     * @param p Player reference that will be used to set builder's player
     * @return Builder reference
     */
    public Builder setPlayer2(Player p) {
      String name = DEFAULT_PLAYER2_NAME;
      Color color = Color.YELLOW;
      if (p == null) {
        return this;
      } else {
        if (p.getName() != null || !p.getName().isEmpty()) {
          name = p.getName();
        }
        if (p.getDiscColor() != null || p.getDiscColor() != Color.LIGHT_GRAY
            || !player2.getDiscColor().equals(p.getDiscColor())) {
          color = p.getDiscColor();
        }
        player2 = new Player(name, color);
      }
      return this;
    }

    /**
     * @param radius should be greater than 0
     * @return Builder reference
     */
    public Builder setCircleRadius(int radius) {
      if (radius > 0) {
        circleRadius = radius;
      }
      return this;
    }

    /**
     * @param frameSize should be greater than 0
     * @return Builder reference
     */
    public Builder setFrameSize(int frameSize) {
      if (frameSize > 0) {
        this.frameSize = frameSize;
      }
      return this;
    }

    public GameSettings build() {
      return new GameSettings(this);
    }
  }

  public static GameSettings getInstance() {
    return INSTANCE;
  }

  public static GameSettings newInstanceWithCustomPlayerNames(String pl1,
      String pl2) {
    return new Builder().setPlayer1(new Player(pl1, Color.RED)).setPlayer2(
        new Player(pl2, Color.YELLOW)).build();
  }

  public Player getPlayer(PlayerType pt) {
    return playerInfo.get(pt);
  }

  public int getCircleRadius() {
    return circleRadius;
  }

  public int getFrameSize() {
    return frameSize;
  }

  /**
   * @return String in format "GameSettings [playerInfo=" + playerInfo +
   *         ", circleRadius=" + circleRadius + ", frameSize=" + frameSize + "]"
   */
  @Override
  public String toString() {
    return "GameSettings [playerInfo=" + playerInfo + ", circleRadius="
        + circleRadius + ", frameSize=" + frameSize + "]";
  }
}
