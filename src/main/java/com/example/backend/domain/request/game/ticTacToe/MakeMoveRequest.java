package com.example.backend.domain.request.game.ticTacToe;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MakeMoveRequest {
  @NotNull(message = "gameId is required")
  private UUID gameId;

  @NotNull(message = "row is required")
  @Min(value = 0, message = "row must be >= 0")
  @Max(value = 2, message = "row must be <= 2")
  private Integer row;

  @NotNull(message = "column is required")
  @Min(value = 0, message = "column must be >= 0")
  @Max(value = 2, message = "column must be <= 2")
  private Integer column;
}
