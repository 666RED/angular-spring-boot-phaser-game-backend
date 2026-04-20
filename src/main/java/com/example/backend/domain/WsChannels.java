package com.example.backend.domain;

import com.example.backend.domain.enums.GameType;

public class WsChannels {
  private static final String TOPIC = "/topic";
  private static final String QUEUE = "/queue";

  // ===== Broadcast =====
  public static String gameCreated(GameType gameType) {
    return String.format("%s/%s/games/created", TOPIC, gameType.value());
  }

  public static String makeMove(GameType gameType) {
    return String.format("%s/%s/games/move", TOPIC, gameType.value());
  }

  public static String gameRoom(GameType gameType, String gameId) {
    return String.format("%s/%s/games/%s", TOPIC, gameType.value(), gameId);
  }

  // todo: might remove
  // ===== User-specific (public topic, not recommended usually) =====
  // public static String matchTopic(GameType gameType, Long userId) {
  //   return String.format("%s/%s/match/%d", TOPIC, gameType.value(), userId);
  // }

  // ===== Private queue (recommended) =====
  public static String userMatch(GameType gameType) {
    return String.format("%s/%s/match", QUEUE, gameType.value());
  }

  public static String userMove(GameType gameType) {
    return String.format("%s/%s/move", QUEUE, gameType.value());
  }

  public static String userNewGame(GameType gameType) {
    return String.format("%s/%s/new-game", QUEUE, gameType.value());
  }

  public static String userTesting(GameType gameType) {
    return String.format("%s/%s/testing", QUEUE, gameType.value());
  }
}
