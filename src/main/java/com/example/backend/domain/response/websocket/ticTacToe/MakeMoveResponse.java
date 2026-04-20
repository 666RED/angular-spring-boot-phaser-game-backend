package com.example.backend.domain.response.websocket.ticTacToe;

import java.util.List;
import java.util.Map;

import com.example.backend.domain.entity.redis.ticTacToe.TicTacToeGameStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MakeMoveResponse {
	private TicTacToeGameStatus status;
  private List<String> currentBoard;
  private Long currentTurnPlayerId;
  private int turnCount;
  private Long winnerId;
  private Map<Long, Integer> winRounds;
}
