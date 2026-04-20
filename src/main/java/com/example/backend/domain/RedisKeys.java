package com.example.backend.domain;

import com.example.backend.domain.enums.GameType;

public class RedisKeys {
  private static final String PREFIX = "game";

  public static String queue(GameType gameType) {
    return String.format("%s:%s:queue", PREFIX, gameType.value());
  }

  public static String set(GameType gameType) {
    return String.format("%s:%s:set", PREFIX, gameType.value());
  }

  public static String userStatus(GameType gameType, Long userId) {
    return String.format("%s:%s:user:%d:status", PREFIX, gameType.value(), userId);
  }
}
