package com.example.backend.domain.request.game.ticTacToe;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MakeMoveEntity {
  private UUID gameId;
  private int row;
  private int column;
}
