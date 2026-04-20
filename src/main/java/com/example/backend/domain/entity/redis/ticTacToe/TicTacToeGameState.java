package com.example.backend.domain.entity.redis.ticTacToe;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicTacToeGameState {
  private List<String> currentBoard;
	private Long firstTurnPlayerId;
  private Long currentTurnPlayerId;
  private int turnCount;
  private TicTacToeGameStatus status;
  private Long winnerId;
  private Long lastMoveTimestamp; // to kick AFK players
}
