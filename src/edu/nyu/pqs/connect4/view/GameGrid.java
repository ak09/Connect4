package edu.nyu.pqs.connect4.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import edu.nyu.pqs.connect4.model.Connect4Model;
import edu.nyu.pqs.connect4.model.GameEnums;
import edu.nyu.pqs.connect4.model.GameEnums.PlayerType;

/**
 * This class is uses Circle class to make the grid for the game The default
 * color when circle's owner(PlayerType) = NONE is Color.LIGHT_GRAY
 * 
 * @author ajaykhanna
 * 
 */
public class GameGrid extends JPanel {
  private static final long serialVersionUID = 1L;
  public Circle grid[];
  private final int ROWSIZE;
  private final int COLSIZE;
  private final int RADIUS;
  private Connect4Model connect4Model;
  private GameSettings gameSettings;

  /**
   * Constructor to initialize gameGrid to its dimensions
   * 
   * @param rows Number of Rows in grid
   * @param cols Number of Columns in grid
   * @param model The reference of model to get the dimensions
   * @param gameSetting setting for the game
   * @throws NullPointerException if model/gameSetting is null
   * @throws IllegalArgumentException if rows/cols is less than or equal to 0
   */
  public GameGrid(int rows, int cols, Connect4Model model,
      GameSettings gameSetting) {
    super();
    if (model == null) {
      throw new NullPointerException("Model cannot be null");
    }
    if (gameSetting == null) {
      throw new NullPointerException("GameSetting cannot be null");
    }
    if (rows <= 0 || cols <= 0) {
      throw new IllegalArgumentException(
          "rows and cols should be greater than 0");
    }
    this.gameSettings = gameSetting;
    this.connect4Model = model;
    ROWSIZE = rows;
    COLSIZE = cols;
    RADIUS = gameSetting.getCircleRadius();
    grid = new Circle[ROWSIZE * COLSIZE];
    for (int i = 0; i < ROWSIZE; i++) {
      for (int j = 0; j < COLSIZE; j++) {
        grid[gridPositionToCircle(i, j)] =
            new Circle((2 * j + 1) * RADIUS, (2 * i + 1) * RADIUS, RADIUS);
      }
    }
    MouseListener listener = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        boolean circleFound = false;
        int gridPos = 0;
        for (int i = 0; i < grid.length && !circleFound; i++) {
          if (grid[i].containPoint(e.getX(), e.getY())) {
            circleFound = true;
            gridPos = i;
          }
        }
        if (circleFound) {
          connect4Model.playMove(rowFromGrid(gridPos), colFromGrid(gridPos));
        }
      }
    };
    addMouseListener(listener);
  }

  /**
   * converts grid position to model's row position
   * 
   * @param gridPosition position in the grid
   * @return row location of the model
   */
  private int rowFromGrid(int gridPosition) {
    return gridPosition / COLSIZE;
  }

  /**
   * converts grid position to model's column position
   * 
   * @param gridPosition position in the grid
   * @return column location of the model
   */
  private int colFromGrid(int gridPosition) {
    return gridPosition % COLSIZE;
  }

  /**
   * converts model's position to grids position
   * 
   * @param row Row location in model's grid
   * @param col Column location in model's grid
   * @return grid position
   */
  public int gridPositionToCircle(int row, int col) {
    return COLSIZE * row + col;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    for (Circle c : grid) {
      switch (c.getOwner()) {
      case PLAYER1:
        g.setColor(gameSettings.getPlayer(PlayerType.PLAYER1).getDiscColor());
        g.fillOval(c.getCenter_x() - RADIUS, c.getCenter_y() - RADIUS,
            RADIUS * 2, RADIUS * 2);
        break;
      case PLAYER2:
        g.setColor(gameSettings.getPlayer(PlayerType.PLAYER2).getDiscColor());
        g.fillOval(c.getCenter_x() - RADIUS, c.getCenter_y() - RADIUS,
            RADIUS * 2, RADIUS * 2);
        break;
      case NONE:
        g.setColor(Color.LIGHT_GRAY);
        g.fillOval(c.getCenter_x() - RADIUS, c.getCenter_y() - RADIUS,
            RADIUS * 2, RADIUS * 2);
      }
    }
  }

  /**
   * Clears grid and sets circle owner as GameEnums.PlayerType.NONE
   */
  public void clearGrid() {
    for (Circle c : grid) {
      c.setOwner(GameEnums.PlayerType.NONE);
    }
  }

  /**
   * @return String in format of "GameGrid of 6X7 and Circle radius=15" where
   *         ROWSIZE=6, COLSIZE=7 and RADIUS=15
   */
  @Override
  public String toString() {
    return "GameGrid of + " + ROWSIZE + "X" + COLSIZE + "and Circle radius="
        + RADIUS;
  }

}
