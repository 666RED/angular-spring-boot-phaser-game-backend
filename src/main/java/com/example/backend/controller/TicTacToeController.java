package com.example.backend.controller;

import com.example.backend.domain.entity.redis.ticTacToe.TicTacToeGameRoom;
import com.example.backend.domain.request.game.ticTacToe.MakeMoveRequest;
import com.example.backend.domain.response.websocket.ticTacToe.MakeMoveResponse;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.ticTacToe.TicTacToeService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/tic-tac-toe")
public class TicTacToeController {
  private final TicTacToeService ticTacToeService;

  @GetMapping("/{id}")
  public ResponseEntity<TicTacToeGameRoom> getGame(@PathVariable("id") UUID gameId) {
    TicTacToeGameRoom gameRoom = ticTacToeService.getGame(gameId);

    return ResponseEntity.ok(gameRoom);
  }

  @PostMapping
  public ResponseEntity<Void> matchGame(@AuthenticationPrincipal CustomUserDetails userDetails) {
    ticTacToeService.matchGame(userDetails.getId());

    return ResponseEntity.noContent().build();
  }

  @PostMapping("/make-move")
  public ResponseEntity<MakeMoveResponse> makeMove(
      @RequestBody MakeMoveRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    ticTacToeService.makeMove(request, userDetails.getId());

    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/rounds")
  public ResponseEntity<TicTacToeGameRoom> newGame(
      @PathVariable("id") UUID gameId, @AuthenticationPrincipal CustomUserDetails userDetails) {
    ticTacToeService.newGame(gameId, userDetails.getId());

    return ResponseEntity.noContent().build();
  }
}
