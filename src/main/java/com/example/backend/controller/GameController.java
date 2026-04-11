package com.example.backend.controller;

import com.example.backend.domain.entity.User;
import com.example.backend.domain.entity.redis.game.GameRoom;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.game.GameService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/games")
public class GameController {
  private final GameService gameService;

  @GetMapping
  public ResponseEntity<List<GameRoom>> listGames() {
    List<GameRoom> games = gameService.listGames();

    return ResponseEntity.ok(games);
  }

  @GetMapping("/{id}")
  public ResponseEntity<GameRoom> getGame(@PathVariable("id") UUID gameId) {
    GameRoom gameRoom = gameService.getGame(gameId);

    return ResponseEntity.ok(gameRoom);
  }

  @PostMapping
  public ResponseEntity<GameRoom> createGame(
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    User user = customUserDetails.getUser();
    GameRoom gameRoom = gameService.createGame(user.getId());
    return new ResponseEntity<>(gameRoom, HttpStatus.CREATED);
  }

  @PostMapping("/{id}")
  public ResponseEntity<GameRoom> joinGame(
      @PathVariable("id") UUID gameId,
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    User user = customUserDetails.getUser();
    GameRoom gameRoom = gameService.joinGame(gameId, user.getId());

    return new ResponseEntity<>(gameRoom, HttpStatus.OK);
  }
}
