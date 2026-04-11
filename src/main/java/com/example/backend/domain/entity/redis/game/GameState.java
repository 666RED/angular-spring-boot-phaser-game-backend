package com.example.backend.domain.entity.redis.game;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameState {
  private List<String> currentBoard;
  private Long currentTurnPlayerId;
  private int turnCount;
  private Map<String, Integer> scores;
  private GameStatus status;
  private Long winnerId;
  private Long lastMoveTimestamp; // to kick AFK players
  private Long gameStartTime;
}
