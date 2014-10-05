package edu.nyu.pqs.connect4.view;

import java.awt.Color;

/**
 * Info about the player.
 * 
 * @author ajaykhanna
 * 
 */
public class Player {
  private String name;
  private Color discColor;

  public Player(String name, Color discColor) {
    if (name == null) {
      throw new NullPointerException("name cannot be null");
    }
    if (discColor == null) {
      throw new NullPointerException("discColor cannot be null");
    }
    this.name = name;
    this.discColor = discColor;
  }

  public String getName() {
    return name;
  }

  public Color getDiscColor() {
    return discColor;
  }

  /**
   * @return String in format "Player name="+ name + ", discColor=" + discColor
   */
  @Override
  public String toString() {
    return "Player name=" + name + ", discColor=" + discColor;
  }
}
