package com.example.backend.domain.dto.websocket.event;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinGameEvent {
  private String type;
  private Long playerId;
  private List<Long> playerIds;
}
