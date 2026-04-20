package com.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/games")
public class GameController {
  // private final GameService gameService;

  // todo: change later for more games
  // @GetMapping
  // public ResponseEntity<List<GameRoom>> listGames() {
  //   List<GameRoom> games = gameService.listGames();
  //
  //   return ResponseEntity.ok(games);
  // }
}
