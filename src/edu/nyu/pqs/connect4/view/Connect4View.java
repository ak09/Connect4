package edu.nyu.pqs.connect4.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import edu.nyu.pqs.connect4.listener.Connect4Listener;
import edu.nyu.pqs.connect4.model.Connect4Model;
import edu.nyu.pqs.connect4.model.GameEnums;

/**
 * The view that implemented Connect4Listener. It uses swing for GUI.
 * 
 * @author ajaykhanna
 * 
 */
public class Connect4View implements Connect4Listener {
  private Connect4Model connect4Model;
  private JFrame gameFrame;
  private GameGrid gameGrid;
  private JLabel player1Text;
  private JLabel player2Text;
  private JRadioButton singlePlayer;
  private JRadioButton twoPlayer;
  private JButton startButton;
  private JButton exitButton;
  private JLabel gameState;
  private GameSettings gameSettings;
  private GameEnums.GameType gameType;

  /**
   * Constructor of the View
   * 
   * @param model The model that has the logic of the game
   * @param gameSetting Game settings for the view
   * @throws NullPointerException if model/gameSetting is null
   */
  public Connect4View(Connect4Model model, GameSettings gameSetting) {
    if (model == null) {
      throw new NullPointerException("Model cannot be null");
    }
    if (gameSetting == null) {
      throw new NullPointerException("GameSetting cannot be null");
    }
    this.connect4Model = model;
    gameSettings = gameSetting;
    gameFrame = new JFrame("Connect4");
    JPanel gamePannel = new JPanel();
    startButton = new JButton("Start");
    startButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        startGame();
      }
    });
    exitButton = new JButton("Exit Game");
    exitButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        exitGame();
      }
    });
    singlePlayer = new JRadioButton("Single Player");
    twoPlayer = new JRadioButton("Two Player");
    twoPlayer.setSelected(true);
    ButtonGroup gameMode = new ButtonGroup();
    gameMode.add(singlePlayer);
    gameMode.add(twoPlayer);
    gamePannel.add(singlePlayer, BorderLayout.WEST);
    gamePannel.add(twoPlayer, BorderLayout.EAST);
    gamePannel.add(startButton, BorderLayout.SOUTH);
    gamePannel.add(exitButton, BorderLayout.SOUTH);
    gameType = GameEnums.GameType.TWOPLAYER;
    JPanel playerInfoPanel = new JPanel();
    JLabel player1Label = new JLabel("Player1: ");
    JLabel player2Label = new JLabel("Player2: ");
    player1Text =
        new JLabel(gameSetting.getPlayer(GameEnums.PlayerType.PLAYER1)
            .getName());
    player2Text =
        new JLabel(gameSetting.getPlayer(GameEnums.PlayerType.PLAYER2)
            .getName());
    playerInfoPanel.add(player1Label, BorderLayout.LINE_START);
    playerInfoPanel.add(player1Text, BorderLayout.LINE_START);
    playerInfoPanel.add(player2Label, BorderLayout.LINE_END);
    playerInfoPanel.add(player2Text, BorderLayout.LINE_END);
    gameState = new JLabel("Not In Play");
    gameGrid =
        new GameGrid(connect4Model.getGRIDROWSIZE(), connect4Model
            .getGRIDCOLUMNSIZE(), connect4Model, gameSetting);
    JLabel gameStateLabel = new JLabel("Game State:");
    JPanel gamePlayPanel = new JPanel();
    gamePlayPanel.add(gameStateLabel, BorderLayout.LINE_START);
    gamePlayPanel.add(gameState, BorderLayout.LINE_END);
    gameFrame.getContentPane().add(playerInfoPanel, BorderLayout.PAGE_START);
    gameFrame.getContentPane().add(gameGrid, BorderLayout.CENTER);
    gameFrame.getContentPane().add(gamePlayPanel, BorderLayout.EAST);
    gameFrame.getContentPane().add(gamePannel, BorderLayout.PAGE_END);
    joinGame();
    gameGrid.setEnabled(false);
    gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    gameFrame.setSize(gameSetting.getFrameSize(), gameSetting.getFrameSize());
    gameFrame.setVisible(true);
  }

  /**
   * Starts the game according to the type of game selected
   */
  public void startGame() {
    startButton.setEnabled(false);
    gameGrid.clearGrid();
    if (singlePlayer.isSelected()) {
      gameType = GameEnums.GameType.SINGLEPLAYER;
    } else {
      gameType = GameEnums.GameType.TWOPLAYER;
    }
    connect4Model.startGame(this, gameType);
  }

  /**
   * joins game by adding itself to model's views
   */
  public void joinGame() {
    connect4Model.joinGame(this);
    if (connect4Model.isGameStarted()) {
      startButton.setEnabled(false);
      gameGrid.setEnabled(true);
    }
  }

  /**
   * Removes it self from the model's views
   */
  public void exitGame() {
    connect4Model.exitGame(this);
  }

  /**
   * @throws NullPointerException if model/gt/turn is null
   */
  @Override
  public void gameStarted(GameEnums.PlayerType turn, GameEnums.GameType gt,
      Connect4Model model) {
    if (model == null) {
      throw new NullPointerException("Model cannot be null");
    }
    if (turn == null) {
      throw new NullPointerException("GameType cannot be null");
    }
    if (gt == null) {
      throw new NullPointerException("PlayerType cannot be null");
    }
    gameGrid.clearGrid();
    gameGrid.setEnabled(true);
    startButton.setEnabled(false);
    gameState.setText(gameSettings.getPlayer(turn).getName() + " Turn");
    if (!gameType.equals(gt)) {
      gameType = gt;
      if (gameType.equals(GameEnums.GameType.SINGLEPLAYER)) {
        singlePlayer.setSelected(true);
      } else {
        twoPlayer.setSelected(true);
      }
    }
  }

  /**
   * @throws NullPointerException if model is null
   */
  @Override
  public void gameStopped(Connect4Model model) {
    if (model == null) {
      throw new NullPointerException("Model cannot be null");
    }
    gameFrame.setVisible(false);
    gameFrame.dispose();
  }

  /**
   * @throws NullPointerException if model/owner is null
   */
  @Override
  public void playNextMove(int row, int col, GameEnums.PlayerType owner,
      Connect4Model model) {
    if (model == null) {
      throw new NullPointerException("Model cannot be null");
    }
    if (owner == null) {
      throw new NullPointerException("PlayerType cannot be null");
    }
    gameGrid.grid[gameGrid.gridPositionToCircle(row, col)].setOwner(owner);
    gameGrid.repaint();
    setGameStateAfterEveryMove(owner);
  }

  /**
   * @throws NullPointerException if model/owner is null
   */
  @Override
  public void gameWon(int row, int col, GameEnums.PlayerType owner,
      Connect4Model model) {
    if (model == null) {
      throw new NullPointerException("Model cannot be null");
    }
    if (owner == null) {
      throw new NullPointerException("PlayerType cannot be null");
    }
    gameState.setText(gameSettings.getPlayer(owner).getName() + " Won");
    startButton.setEnabled(true);
  }

  /**
   * @throws NullPointerException if model is null
   */
  @Override
  public void gameDraw(Connect4Model model) {
    if (model == null) {
      throw new NullPointerException("Model cannot be null");
    }
    startButton.setEnabled(true);
    gameState.setText("Game Draw");
  }

  /**
   * Updates the Player turn after every move.
   * 
   * @param owner Current Player who played
   * @throws NullPointerException if owner is null
   */
  private void setGameStateAfterEveryMove(GameEnums.PlayerType owner) {
    if (owner == null) {
      throw new NullPointerException("PlayerType cannot be null");
    }
    if (owner == GameEnums.PlayerType.PLAYER1) {
      gameState.setText(gameSettings.getPlayer(GameEnums.PlayerType.PLAYER2)
          .getName()
          + " Turn");
    } else {
      gameState.setText(gameSettings.getPlayer(GameEnums.PlayerType.PLAYER1)
          .getName()
          + " Turn");
    }
  }

}
