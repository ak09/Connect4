package edu.nyu.pqs.connect4.test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import edu.nyu.pqs.connect4.listener.Connect4Listener;
import edu.nyu.pqs.connect4.model.Connect4Model;
import edu.nyu.pqs.connect4.model.GameEnums.GameType;
import edu.nyu.pqs.connect4.model.GameEnums.PlayerType;

public class Connect4ModelTest {
  private TestConnect4View c4l;
  private Connect4Model c4m;

  private class TestConnect4View implements Connect4Listener {
    public PlayerType owner;
    public boolean gameWon;
    public boolean gameStarted;
    public boolean gameDraw;
    public int row;
    public int col;

    @Override
    public void playNextMove(int row, int col, PlayerType owner,
        Connect4Model model) {
      this.owner = owner;
      this.row = row;
      this.col = col;
    }

    @Override
    public void
        gameWon(int row, int col, PlayerType owner, Connect4Model model) {
      this.owner = owner;
      this.gameWon = true;
    }

    @Override
    public void gameStopped(Connect4Model model) {
    }

    @Override
    public void gameStarted(PlayerType turn, GameType gameType,
        Connect4Model model) {
      this.gameStarted = true;
    }

    @Override
    public void gameDraw(Connect4Model model) {
      this.gameDraw = true;
    }
  };

  @Before
  public void setUp() {
    c4l = new TestConnect4View();
    c4m = new Connect4Model(6, 7, 4);
  }

  @Test
  public void testJoinGame() {
    assertTrue(c4m.joinGame(c4l));
  }

  @Test
  public void testJoinGameForMultipleSameView() {
    assertTrue(c4m.joinGame(c4l));
    assertTrue(!c4m.joinGame(c4l));
  }

  @Test(expected = NullPointerException.class)
  public void testJoinGameForNullListener() {
    c4m.joinGame(null);
  }

  @Test
  public void testExitGame() {
    c4m.joinGame(c4l);
    assertTrue(c4m.exitGame(c4l));
  }

  @Test
  public void testExitGameWithoutJoining() {
    assertTrue(!c4m.exitGame(c4l));
  }

  @Test
  public void testExitGameForMultipleExits() {
    c4m.joinGame(c4l);
    assertTrue(c4m.exitGame(c4l));
    assertTrue(!c4m.exitGame(c4l));
  }

  @Test(expected = NullPointerException.class)
  public void testExitGameForNullListener() {
    c4m.exitGame(null);
  }

  @Test
  public void testStartGame() {
    c4m.joinGame(c4l);
    c4m.startGame(c4l, GameType.TWOPLAYER);
    assertTrue(c4m.isGameStarted());
  }

  @Test
  public void testStartGameMultipleTimes() {
    c4m.joinGame(c4l);
    c4m.startGame(c4l, GameType.TWOPLAYER);
    assertTrue(c4m.isGameStarted());
    assertTrue(!c4m.startGame(c4l, GameType.TWOPLAYER));
  }

  @Test(expected = NullPointerException.class)
  public void testStartGameWithNullListener() {
    c4m.joinGame(c4l);
    c4m.startGame(null, GameType.TWOPLAYER);
    assertTrue(c4m.isGameStarted());
  }

  @Test(expected = NullPointerException.class)
  public void testStartGameWithNullGameType() {
    c4m.joinGame(c4l);
    c4m.startGame(c4l, null);
    assertTrue(c4m.isGameStarted());
  }

  @Test
  public void testPlayMove() {
    c4m.joinGame(c4l);
    c4m.startGame(c4l, GameType.TWOPLAYER);
    c4m.playMove(2, 3);
    assertEquals(c4l.row, 5);
    assertEquals(c4l.col, 3);
    assertEquals(c4l.owner, PlayerType.PLAYER1);
    assertEquals(c4m.getNextTurn(), PlayerType.PLAYER2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlayMoveForInvalidRow() {
    c4m.joinGame(c4l);
    c4m.startGame(c4l, GameType.TWOPLAYER);
    c4m.playMove(10, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlayMoveForInvalidCol() {
    c4m.joinGame(c4l);
    c4m.startGame(c4l, GameType.TWOPLAYER);
    c4m.playMove(3, 9);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlayMoveForInvalidMoveLocation() {
    c4m.joinGame(c4l);
    c4m.startGame(c4l, GameType.TWOPLAYER);
    c4m.playMove(10, 13);
  }

  @Test
  public void testPlayMoveForPlayer1Winning() {
    c4m.joinGame(c4l);
    c4m.startGame(c4l, GameType.TWOPLAYER);
    c4m.playMove(0, 0);
    c4m.playMove(1, 0);
    c4m.playMove(0, 1);
    c4m.playMove(1, 1);
    c4m.playMove(0, 2);
    c4m.playMove(1, 2);
    c4m.playMove(0, 3);
    assertEquals(c4l.owner, PlayerType.PLAYER1);
    assertTrue(c4l.gameWon);
  }

  @Test
  public void testPlayMoveForIllegalMove() {
    c4m.joinGame(c4l);
    c4m.startGame(c4l, GameType.TWOPLAYER);
    c4m.playMove(0, 0);
    assertTrue(!c4m.playMove(5, 0));
  }

  @Test
  public void testPlayMoveWhenGameNotStarted() {
    c4m.joinGame(c4l);
    assertTrue(!c4m.playMove(0, 0));
  }

  @Test
  public void testPlayMoveForDraw() {
    c4m.joinGame(c4l);
    c4m.startGame(c4l, GameType.TWOPLAYER);
    c4m.playMove(5, 0);
    c4m.playMove(4, 0);
    c4m.playMove(3, 0);
    c4m.playMove(2, 0);
    c4m.playMove(1, 0);
    c4m.playMove(0, 0);
    c4m.playMove(5, 6);
    c4m.playMove(4, 6);
    c4m.playMove(3, 6);
    c4m.playMove(2, 6);
    c4m.playMove(1, 6);
    c4m.playMove(0, 6);
    c4m.playMove(5, 3);
    c4m.playMove(4, 3);
    c4m.playMove(3, 3);
    c4m.playMove(2, 3);
    c4m.playMove(1, 3);
    c4m.playMove(0, 3);
    c4m.playMove(5, 2);
    c4m.playMove(5, 1);
    c4m.playMove(4, 2);
    c4m.playMove(4, 1);
    c4m.playMove(3, 2);
    c4m.playMove(3, 1);
    c4m.playMove(2, 1);
    c4m.playMove(2, 2);
    c4m.playMove(1, 1);
    c4m.playMove(1, 2);
    c4m.playMove(0, 1);
    c4m.playMove(0, 2);
    c4m.playMove(5, 5);
    c4m.playMove(5, 4);
    c4m.playMove(4, 5);
    c4m.playMove(4, 4);
    c4m.playMove(3, 5);
    c4m.playMove(3, 4);
    c4m.playMove(2, 4);
    c4m.playMove(2, 5);
    c4m.playMove(1, 4);
    c4m.playMove(1, 5);
    c4m.playMove(0, 4);
    c4m.playMove(0, 5);
    assertTrue(c4l.gameDraw);
  }

}
