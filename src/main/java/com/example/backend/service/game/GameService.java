package com.example.backend.service.game;

import com.example.backend.domain.entity.redis.game.GameRoom;
import java.util.List;
import java.util.UUID;

public interface GameService {
  List<GameRoom> listGames();

  GameRoom createGame(Long firstPlayerId);

  GameRoom getGame(UUID gameId);

  GameRoom joinGame(UUID gameId, Long secondPlayerId);
}
