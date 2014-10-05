package edu.nyu.pqs.connect4.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import edu.nyu.pqs.connect4.listener.Connect4Listener;
import edu.nyu.pqs.connect4.model.GameEnums.GameType;

/**
 * This the Model for the Connect4 Game.
 * 
 * @author ajaykhanna
 * 
 */
public class Connect4Model {
  private final int GRIDROWSIZE;
  private final int GRIDCOLUMNSIZE;
  private final int WINNINGSIZE;
  private final int TOTALGAMEMOVES;
  private int remainingMoves;
  private AtomicBoolean isGameStarted;
  private GameEnums.GameType gameType;
  private boolean computerWon;
  private GameEnums.PlayerType gameGrid[][];
  private List<Connect4Listener> views;
  private GameEnums.PlayerType nextTurn;

  private enum GameState {
    WON, DRAW, INPLAY
  };

  /**
   * Constructor of the Connect4Model. Initializes all the values.
   * 
   * @param rowSize Number of Rows in the game.
   * @param colSize Number of Columns in the game.
   * @param winSize Winning Size of the game.
   */
  public Connect4Model(int rowSize, int colSize, int winSize) {
    GRIDROWSIZE = rowSize;
    GRIDCOLUMNSIZE = colSize;
    WINNINGSIZE = winSize;
    TOTALGAMEMOVES = GRIDCOLUMNSIZE * GRIDROWSIZE;
    remainingMoves = TOTALGAMEMOVES;
    gameGrid = new GameEnums.PlayerType[GRIDROWSIZE][GRIDCOLUMNSIZE];
    views = new ArrayList<Connect4Listener>();
    isGameStarted = new AtomicBoolean(Boolean.FALSE);
    setGameToInitialState();
  }

  /**
   * Starts the game and fires views game started event.
   * 
   * @param listenerRequest The listener who made the request to start the game
   * @param gameType Type of the game
   * @return true if game was not in a started state
   * @throws NullPointerException if gameType or listerRequest is Null
   */
  public boolean startGame(Connect4Listener listenerRequest,
      GameEnums.GameType gameType) {
    if (isGameStarted.get()) {
      return false;
    }
    if (gameType == null) {
      throw new NullPointerException("Game Type cannot be Null");
    }
    if (listenerRequest == null) {
      throw new NullPointerException("Listener cannot be Null");
    }
    setGameToInitialState();
    this.gameType = gameType;
    isGameStarted.set(Boolean.TRUE);
    for (Connect4Listener listener : views) {
      fireGameStartedEvent(listener, gameType);
    }
    return true;
  }

  /**
   * Adds the listener to views and if game is already in start state then it
   * fires the game start and player moved events for the listener to update it
   * to current state
   * 
   * @param listener The listener who joined the game
   * @return true if listener joins the game and was not present in the list
   * @throws NullPointerException if lister is Null
   */
  public boolean joinGame(Connect4Listener listener) {
    if (listener == null) {
      throw new NullPointerException("Listener cannot be null");
    }
    if (views.contains(listener)) {
      return false;
    }
    views.add(listener);
    if (isGameStarted.get()) {
      fireGameStartedEvent(listener, gameType);
      playAllPreviousMoves(listener);
    }
    return true;
  }

  /**
   * Removes the listener from the views and fires game exit event.
   * 
   * @param listener The listener who left the game
   * @return true if the listener was present and is removed from the views
   * @throws NullPointerException if lister is Null
   */
  public boolean exitGame(Connect4Listener listener) {
    if (listener == null) {
      throw new NullPointerException("Listener cannot be null");
    }
    if (!views.contains(listener)) {
      return false;
    }
    views.remove(listener);
    if (views.isEmpty()) {
      isGameStarted.set(Boolean.FALSE);
    }
    fireGameExitEvent(listener);
    return true;
  }

  /**
   * Plays the appropriate move in the game grid and fires player moved event.
   * Also fires gameWon/ gameDraw event if the game state changes.
   * 
   * @param row Row Location of the move
   * @param col Column Location of the move
   * @return true if the move is valid(valid means that no one has played on
   *         this location).
   * @throws IllegalArgumentException if row/col is out of bounds
   */
  public boolean playMove(int row, int col) {
    if (!isGameStarted.get()) {
      return false;
    }
    if (isLegalMove(row, col)) {
      int tempRow = GRIDROWSIZE;
      while (--tempRow >= 0) {
        if (isLegalMove(tempRow, col)) {
          gameGrid[tempRow][col] = nextTurn;
          remainingMoves--;
          firePlayerMovedEvent(tempRow, col);
          GameState currGameState = checkGameState(tempRow, col);
          if (computerWon || currGameState == GameState.WON) {
            fireGameWonEvent(tempRow, col);
          } else if (currGameState == GameState.DRAW) {
            fireGameDrawEvent();
          }
          setNextPlayer();
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Helps joinGame to update view.
   * 
   * @param listener The listener who joined the game
   */
  private void playAllPreviousMoves(Connect4Listener listener) {
    for (int i = 0; i < GRIDROWSIZE; i++) {
      for (int j = 0; j < GRIDCOLUMNSIZE; j++) {
        if (!isLegalMove(i, j)) {
          listener.playNextMove(i, j, gameGrid[i][j], this);
        }
      }
    }
  }

  /**
   * fires game draw event for all the joined views.
   */
  private void fireGameDrawEvent() {
    isGameStarted.set(Boolean.FALSE);
    for (Connect4Listener c4l : views) {
      c4l.gameDraw(this);
    }
  }

  /**
   * fires game started event for all the joined views.
   * 
   * @param listenerRequest The Listener who requested game start.
   * @param gameType Type of the game requested.
   */
  private void fireGameStartedEvent(Connect4Listener listenerRequest,
      GameEnums.GameType gameType) {
    listenerRequest.gameStarted(nextTurn, gameType, this);
  }

  /**
   * fires game stopped event for the requested view.
   * 
   * @param listenerRequest The Listener who requested game stop.
   */
  private void fireGameExitEvent(Connect4Listener listenerRequest) {
    listenerRequest.gameStopped(this);
  }

  /**
   * fires game won event for all the joined views.
   * 
   * @param row Row Location of the winning move
   * @param col Column Location of the winning move
   */
  private void fireGameWonEvent(int row, int col) {
    isGameStarted.set(Boolean.FALSE);
    for (Connect4Listener c4l : views) {
      c4l.gameWon(row, col, nextTurn, this);
    }
  }

  /**
   * fires play move event for all the joined views.
   * 
   * @param row Row Location of the move
   * @param col Column Location of the move
   */
  private void firePlayerMovedEvent(int row, int col) {
    for (Connect4Listener c4l : views) {
      c4l.playNextMove(row, col, nextTurn, this);
    }
  }

  /**
   * Check for the location to be valid and legal. valid - when move is in grid
   * bounds legal - when no player has moved on that location
   * 
   * @param row Row Location of the move
   * @param col Column Location of the move
   * @return true if move is legal and valid
   * @throws IllegalArgumentException if row/col is out of bounds
   */
  private boolean isLegalMove(int row, int col) {
    if ((row < 0 || row >= GRIDROWSIZE) || (col < 0 || col >= GRIDCOLUMNSIZE)) {
      throw new IllegalArgumentException("Invalid Grid Location");
    }
    if (gameGrid[row][col] == GameEnums.PlayerType.NONE) {
      return true;
    }
    return false;
  }

  /**
   * Checks the current state of the game relevant to row and col.
   * 
   * @param row Row Location
   * @param col Column Location
   * @return the current game state after making the move GameState.WON -
   *         Someone won by making move to this location GameState.DRAW - The
   *         game is draw by making move to this location GameState.INPLAY - The
   *         game is in play means no state change for the game
   */
  private GameState checkGameState(int row, int col) {
    if (checkHorizontals(row, col, nextTurn) == GameState.WON
        || checkVerticals(row, col, nextTurn) == GameState.WON
        || checkDiagonal(row, col, nextTurn) == GameState.WON) {
      return GameState.WON;
    }
    return checkDraw(row, col, nextTurn);
  }

  /**
   * Horizontally checks the current state of the game relevant to row and col
   * 
   * @param row Row Location
   * @param col Column Location
   * @param turn Player turn
   * @return the current game state after making the move
   */
  private GameState
      checkHorizontals(int row, int col, GameEnums.PlayerType turn) {
    return checkHorizontalAsElement(row, col, nextTurn);
  }

  /**
   * Checks the current state of the game relevant to row and col for all the
   * ways of winning horizontally
   * 
   * @param row Row Location
   * @param col Column Location
   * @param turn Player turn
   * @return the current game state after making the move
   */
  private GameState checkHorizontalAsElement(int row, int col,
      GameEnums.PlayerType turn) {
    GameState gameState = GameState.INPLAY;
    for (int i = 0; i < WINNINGSIZE; i++) {
      gameState = checkHorizontal(row, col - i, turn);
      if (gameState == GameState.WON) {
        return gameState;
      }
    }
    return gameState;

  }

  /**
   * Horizontally checks the current state of the game for row and col
   * 
   * @param row Row Location
   * @param col Column Location
   * @param turn Player turn
   * @return the current game state after making the move
   */
  private GameState
      checkHorizontal(int row, int col, GameEnums.PlayerType turn) {
    for (int n = 0, i = col; n < WINNINGSIZE; i++, n++) {
      if ((i < 0 || i >= GRIDCOLUMNSIZE) || gameGrid[row][i] != turn) {
        return GameState.INPLAY;
      }
    }
    return GameState.WON;
  }

  /**
   * Vertically checks the current state of the game relevant to row and col
   * 
   * @param row Row Location
   * @param col Column Location
   * @param turn Player turn
   * @return the current game state after making the move
   */
  private GameState checkVerticals(int row, int col, GameEnums.PlayerType turn) {
    return checkVerticalAsElement(row, col, nextTurn);
  }

  /**
   * Checks the current state of the game relevant to row and col for all the
   * ways of winning vertically
   * 
   * @param row Row Location
   * @param col Column Location
   * @param turn Player turn
   * @return the current game state after making the move
   */
  private GameState checkVerticalAsElement(int row, int col,
      GameEnums.PlayerType turn) {
    GameState gameState = GameState.INPLAY;
    for (int i = 0; i < WINNINGSIZE; i++) {
      gameState = checkVertical(row - i, col, turn);
      if (gameState == GameState.WON) {
        return gameState;
      }
    }
    return gameState;
  }

  /**
   * Vertically checks the current state of the game for row and col
   * 
   * @param row Row Location
   * @param col Column Location
   * @param turn Player turn
   * @return the current game state after making the move
   */
  private GameState checkVertical(int row, int col, GameEnums.PlayerType turn) {
    for (int n = 0, i = row; n < WINNINGSIZE; i++, n++) {
      if ((i < 0 || i >= GRIDROWSIZE) || gameGrid[i][col] != turn) {
        return GameState.INPLAY;
      }
    }
    return GameState.WON;
  }

  /**
   * Diagonally checks the current state of the game relevant to row and col for
   * both left and right diagonal
   * 
   * @param row Row Location
   * @param col Column Location
   * @param turn Player turn
   * @return the current game state after making the move
   */
  private GameState checkDiagonal(int row, int col, GameEnums.PlayerType turn) {
    if (checkLeftDiagonal(row, col, nextTurn) == GameState.WON
        || checkRightDiagonal(row, col, nextTurn) == GameState.WON) {
      return GameState.WON;
    }
    return GameState.INPLAY;
  }

  /**
   * Right diagonally checks the current state of the game relevant to row and
   * col
   * 
   * @param row Row Location
   * @param col Column Location
   * @param turn Player turn
   * @return the current game state after making the move
   */
  private GameState checkRightDiagonal(int row, int col,
      GameEnums.PlayerType turn) {
    return checkRightDiagnolAsElement(row, col, nextTurn);
  }

  /**
   * Checks the current state of the game relevant to row and col for all the
   * ways of winning right diagonally
   * 
   * @param row Row Location
   * @param col Column Location
   * @param turn Player turn
   * @return the current game state after making the move
   */
  private GameState checkRightDiagnolAsElement(int row, int col,
      GameEnums.PlayerType turn) {
    GameState gameState = GameState.INPLAY;
    for (int i = 0; i < WINNINGSIZE; i++) {
      gameState = checkRightDiagnol(row - i, col - i, turn);
      if (gameState == GameState.WON) {
        return gameState;
      }
    }
    return gameState;
  }

  /**
   * Right diagonally checks the current state of the game for row and col
   * 
   * @param row Row Location
   * @param col Column Location
   * @param turn Player turn
   * @return the current game state after making the move
   */
  private GameState checkRightDiagnol(int row, int col,
      GameEnums.PlayerType turn) {
    for (int n = 0, i = row, j = col; n < WINNINGSIZE; i++, j++, n++) {
      if (((i < 0 || i >= GRIDROWSIZE) || (j < 0 || j >= GRIDCOLUMNSIZE))
          || gameGrid[i][j] != turn) {
        return GameState.INPLAY;
      }
    }
    return GameState.WON;
  }

  /**
   * Left diagonally checks the current state of the game relevant to row and
   * col
   * 
   * @param row Row Location
   * @param col Column Location
   * @param turn Player turn
   * @return the current game state after making the move
   */
  private GameState checkLeftDiagonal(int row, int col,
      GameEnums.PlayerType turn) {
    return checkLeftDiagnolAsElement(row, col, nextTurn);
  }

  /**
   * Checks the current state of the game relevant to row and col for all the
   * ways of winning left diagonally
   * 
   * @param row Row Location
   * @param col Column Location
   * @param turn Player turn
   * @return the current game state after making the move
   */
  private GameState checkLeftDiagnolAsElement(int row, int col,
      GameEnums.PlayerType turn) {
    GameState gameState = GameState.INPLAY;
    for (int i = 0; i < WINNINGSIZE; i++) {
      gameState = checkLeftDiagnol(row - i, col + i, turn);
      if (gameState == GameState.WON) {
        return gameState;
      }
    }
    return gameState;
  }

  /**
   * Left diagonally checks the current state of the game for row and col
   * 
   * @param row Row Location
   * @param col Column Location
   * @param turn Player turn
   * @return the current game state after making the move
   */
  private GameState
      checkLeftDiagnol(int row, int col, GameEnums.PlayerType turn) {
    for (int n = 0, i = row, j = col; n < WINNINGSIZE; i++, j--, n++) {
      if (((i < 0 || i >= GRIDROWSIZE) || (j < 0 || j >= GRIDCOLUMNSIZE))
          || gameGrid[i][j] != turn) {
        return GameState.INPLAY;
      }
    }
    return GameState.WON;
  }

  /**
   * Checks the current state of the game for draw
   * 
   * @param row Row Location
   * @param col Column Location
   * @param turn Player turn
   * @return GameState.DRAW when moves played is equal or less than total number
   *         of moves possible else GameState.INPLAY
   */
  private GameState checkDraw(int row, int col, GameEnums.PlayerType turn) {
    if (remainingMoves <= 0) {
      return GameState.DRAW;
    }
    return GameState.INPLAY;
  }

  /**
   * Changes the nextTurn for next player If game type is GameType.SINGLEPLAYER
   * then plays the computer move by calling playComputerMove()
   */
  private void setNextPlayer() {
    if (nextTurn == GameEnums.PlayerType.PLAYER1) {
      nextTurn = GameEnums.PlayerType.PLAYER2;
    } else if (nextTurn == GameEnums.PlayerType.PLAYER2) {
      nextTurn = GameEnums.PlayerType.PLAYER1;
    }
    if (gameType == GameType.SINGLEPLAYER
        && nextTurn == GameEnums.PlayerType.PLAYER2) {
      playNextComputerMove();
    }
  }

  /**
   * Chooses a location to move for computer so that if the computer is winning
   * then it makes that move otherwise it chooses randomly.
   */
  private void playNextComputerMove() {
    boolean isLegal = Boolean.FALSE;
    int row = 0;
    int col = 0;
    for (int j = 0; j < GRIDCOLUMNSIZE && !computerWon; j++) {
      boolean columnChecked = Boolean.FALSE;
      for (int i = GRIDROWSIZE - 1; i >= 0 && !columnChecked; i--) {
        if (isLegalMove(i, j)) {
          columnChecked = Boolean.TRUE;
          gameGrid[i][j] = nextTurn;
          if (checkGameState(i, j) == GameState.WON) {
            computerWon = Boolean.TRUE;
            row = i;
            col = j;
          }
          gameGrid[i][j] = GameEnums.PlayerType.NONE;
        }
      }
    }
    if (!computerWon) {
      while (!isLegal) {
        col = getRandomColumn();
        int tempRow = GRIDROWSIZE;
        while (!isLegal && --tempRow >= 0) {
          isLegal = isLegalMove(tempRow, col);
          if (isLegal) {
            row = tempRow;
          }
        }
      }
    }
    playMove(row, col);
  }

  /**
   * @return random column location for computer move
   */
  private int getRandomColumn() {
    return new Random().nextInt(GRIDCOLUMNSIZE);
  }

  /**
   * Clears the game grid
   */
  private void clearGameGrid() {
    for (int i = 0; i < GRIDROWSIZE; i++) {
      for (int j = 0; j < GRIDCOLUMNSIZE; j++) {
        gameGrid[i][j] = GameEnums.PlayerType.NONE;
      }
    }
  }

  /**
   * Sets the game to initial state
   */
  private void setGameToInitialState() {
    clearGameGrid();
    nextTurn = GameEnums.PlayerType.PLAYER1;
    computerWon = Boolean.FALSE;
    remainingMoves = TOTALGAMEMOVES;
  }

  public int getGRIDROWSIZE() {
    return GRIDROWSIZE;
  }

  public int getGRIDCOLUMNSIZE() {
    return GRIDCOLUMNSIZE;
  }

  public GameEnums.PlayerType getNextTurn() {
    return nextTurn;
  }

  public boolean isGameStarted() {
    return isGameStarted.get();
  }

  /**
   * @return String in format of "Model of size 6X7 with winning size 4" where
   *         GRIDROWSIZE=6, GRIDCOLUMNSIZE=7 and WINNINGSIZE=4
   */
  public String toString() {
    return "Model of size " + GRIDROWSIZE + "X" + GRIDCOLUMNSIZE
        + " with winning size " + WINNINGSIZE;
  }
}
