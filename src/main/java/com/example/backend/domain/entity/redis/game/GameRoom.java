package com.example.backend.domain.entity.redis.game;

import jakarta.persistence.Id;
import java.util.List;
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
@RedisHash(value = "GameRoom", timeToLive = 86400) // one day
public class GameRoom {
  @Id private UUID id;
  private List<Long> playerIds;
  private Long round;
  private GameState gameState;
}
