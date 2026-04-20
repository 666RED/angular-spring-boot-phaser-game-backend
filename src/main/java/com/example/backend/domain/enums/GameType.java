package com.example.backend.domain.enums;

public enum GameType {
  TICTACTOE("tic-tac-toe"),
  TETRIS("tetris");

  private final String value;

  GameType(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
