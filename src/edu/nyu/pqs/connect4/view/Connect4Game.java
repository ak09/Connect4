package edu.nyu.pqs.connect4.view;

import edu.nyu.pqs.connect4.model.Connect4Model;

public class Connect4Game {

  public static void main(String[] args) {
    final Connect4Model c4m = new Connect4Model(6, 7, 4);
    new Connect4View(c4m, GameSettings.getInstance());
    c4m.joinGame(GameStats.getInstance());
  }
}
