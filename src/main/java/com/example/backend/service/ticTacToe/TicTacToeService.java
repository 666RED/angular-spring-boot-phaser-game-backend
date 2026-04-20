package com.example.backend.service.ticTacToe;

import com.example.backend.domain.entity.redis.ticTacToe.TicTacToeGameRoom;
import com.example.backend.domain.request.game.ticTacToe.MakeMoveRequest;
import java.util.UUID;

public interface TicTacToeService {
  TicTacToeGameRoom getGame(UUID gameId);

  void matchGame(Long userId);

  void makeMove(MakeMoveRequest request, Long playerId);

  void newGame(UUID gameId, Long playerId);
}
