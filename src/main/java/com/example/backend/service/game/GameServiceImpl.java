package com.example.backend.service.game;

import com.example.backend.domain.embeddedId.UserGameId;
import com.example.backend.domain.entity.Game;
import com.example.backend.domain.entity.User;
import com.example.backend.domain.entity.UserGame;
import com.example.backend.domain.entity.redis.game.GameRoom;
import com.example.backend.domain.entity.redis.game.GameState;
import com.example.backend.domain.entity.redis.game.GameStatus;
import com.example.backend.repository.GameRepository;
import com.example.backend.repository.GameRoomRepository;
import com.example.backend.repository.UserGameRepository;
import com.example.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
  public static final int GAME_SIZE = 2;
  // redis
  private final GameRoomRepository gameRoomRepository;

  private final GameRepository gameRepository;
  private final UserRepository userRepository;
  private final UserGameRepository userGameRepository;

  private final SimpMessagingTemplate messagingTemplate;

  @Override
  @Transactional
  public GameRoom createGame(Long firstPlayerId) {
    User user =
        userRepository
            .findById(firstPlayerId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    UUID gameId = UUID.randomUUID();

    Game newGame = Game.builder().id(gameId).round(0L).build();

    gameRepository.save(newGame);

    UserGameId userGameId = UserGameId.builder().user(firstPlayerId).game(gameId).build();

    UserGame userGame = UserGame.builder().user(user).game(newGame).userGameId(userGameId).build();

    UserGame createdUserGame = userGameRepository.save(userGame);

    GameState gameState =
        GameState.builder()
            .currentBoard(Collections.nCopies(9, ""))
            .currentTurnPlayerId(firstPlayerId)
            .turnCount(0)
            .scores(null)
            .status(GameStatus.WAITING)
            .winnerId(null)
            .lastMoveTimestamp(null)
            .gameStartTime(createdUserGame.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli())
            .build();

    GameRoom gameRoom =
        GameRoom.builder()
            .id(gameId)
            .playerIds(List.of(firstPlayerId))
            .round(0L)
            .gameState(gameState)
            .build();

    return gameRoomRepository.save(gameRoom);
  }

  @Override
  public GameRoom getGame(UUID gameId) {
    return gameRoomRepository
        .findById(gameId)
        .orElseThrow(
            () -> new EntityNotFoundException("Game with id: " + gameId + " is not found"));
  }

  @Override
  public GameRoom joinGame(UUID gameId, Long secondPlayerId) {
    GameRoom gameRoom =
        gameRoomRepository
            .findById(gameId)
            .orElseThrow(() -> new EntityNotFoundException("Game is not found with id: " + gameId));

    // check if player is already joined the game
    if (gameRoom.getPlayerIds().contains(secondPlayerId)) {
      return gameRoom;
    }

    // check if game is full
    if (gameRoom.getPlayerIds().size() == GAME_SIZE) {
      throw new IllegalStateException("Game is already full");
    }

    // add user to the game
    User user =
        userRepository
            .findById(secondPlayerId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(() -> new EntityNotFoundException("Game is not found with id: " + gameId));

    UserGameId userGameId = UserGameId.builder().user(secondPlayerId).game(gameId).build();

    UserGame userGame = UserGame.builder().user(user).game(game).userGameId(userGameId).build();

    userGameRepository.save(userGame);

    List<Long> playerIds = gameRoom.getPlayerIds();
    playerIds.add(secondPlayerId);

    gameRoom.setPlayerIds(playerIds);

    // JoinGameEvent event =
    //     JoinGameEvent.builder()
    //         .type("PLAYER_JOINED")
    //         .playerId(secondPlayerId)
    //         .playerIds(playerIds)
    //         .build();

    GameRoom updatedGameRoom = gameRoomRepository.save(gameRoom);

    messagingTemplate.convertAndSend("/topic/games/join", updatedGameRoom);

    return updatedGameRoom;
  }

  @Override
  public List<GameRoom> listGames() {
    List<GameRoom> list = new ArrayList<>();
    gameRoomRepository.findAll().forEach(list::add);
    return list;
  }
}
