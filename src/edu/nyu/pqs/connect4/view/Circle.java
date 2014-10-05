package edu.nyu.pqs.connect4.view;

import edu.nyu.pqs.connect4.model.GameEnums;

/**
 * Circles used by the GameGrid class for GUI
 * 
 * @author ajaykhanna
 * 
 */
public class Circle {
  private final int radius;
  private int center_x, center_y;
  private GameEnums.PlayerType owner;

  Circle(int x, int y, int radius) {
    this.center_x = x;
    this.center_y = y;
    this.radius = radius;
    owner = GameEnums.PlayerType.NONE;
  }

  public int getCenter_x() {
    return center_x;
  }

  public void setCenter_x(int center_x) {
    this.center_x = center_x;
  }

  public int getCenter_y() {
    return center_y;
  }

  public void setCenter_y(int center_y) {
    this.center_y = center_y;
  }

  public GameEnums.PlayerType getOwner() {
    return owner;
  }

  public void setOwner(GameEnums.PlayerType owner) {
    this.owner = owner;
  }

  /**
   * Checks if a point is inside the circle or not
   * 
   * @param x x-coordinate
   * @param y y-coordinate
   * @return
   */
  public boolean containPoint(int x, int y) {
    int difference_x = (x - center_x) * (x - center_x);
    int difference_y = (y - center_y) * (y - center_y);
    int distance = difference_x + difference_y;
    int rad_squ = radius * radius;
    return (distance - rad_squ) <= 0;
  }

  /**
   * @return String in format "Circle [radius=15, center=(0,0), owner=PLAYER1]"
   */
  @Override
  public String toString() {
    return "Circle [radius=" + radius + ", center=(" + center_x + ","
        + center_y + "), owner=" + owner + "]";
  }

}