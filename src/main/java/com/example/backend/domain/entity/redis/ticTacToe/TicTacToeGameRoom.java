package com.example.backend.domain.entity.redis.ticTacToe;

import jakarta.persistence.Id;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "TicTacToeGameRoom", timeToLive = 86400) // one day
public class TicTacToeGameRoom {
  @Id private UUID id;
  private List<Long> playerIds;
  private Map<Long, Integer> winRounds; // playerId -> wins
  private Long round;
  private TicTacToeGameState gameState;
}
