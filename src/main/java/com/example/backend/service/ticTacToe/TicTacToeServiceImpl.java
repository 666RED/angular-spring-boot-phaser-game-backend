package com.example.backend.service.ticTacToe;

import com.example.backend.domain.RedisKeys;
import com.example.backend.domain.WsChannels;
import com.example.backend.domain.embeddedId.UserGameId;
import com.example.backend.domain.entity.Game;
import com.example.backend.domain.entity.User;
import com.example.backend.domain.entity.UserGame;
import com.example.backend.domain.entity.redis.ticTacToe.TicTacToeGameRoom;
import com.example.backend.domain.entity.redis.ticTacToe.TicTacToeGameState;
import com.example.backend.domain.entity.redis.ticTacToe.TicTacToeGameStatus;
import com.example.backend.domain.enums.GameType;
import com.example.backend.domain.request.game.ticTacToe.MakeMoveEntity;
import com.example.backend.domain.request.game.ticTacToe.MakeMoveRequest;
import com.example.backend.domain.response.websocket.ticTacToe.MakeMoveResponse;
import com.example.backend.mapper.TicTacToeMapper;
import com.example.backend.repository.GameRepository;
import com.example.backend.repository.TicTacToeGameRoomRepository;
import com.example.backend.repository.UserGameRepository;
import com.example.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicTacToeServiceImpl implements TicTacToeService {
  private final TicTacToeGameRoomRepository gameRoomRepository;
  private final GameRepository gameRepository;
  private final UserRepository userRepository;
  private final UserGameRepository userGameRepository;

  private final TicTacToeMapper mapper;

  private final SimpMessagingTemplate messagingTemplate;
  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public TicTacToeGameRoom getGame(UUID gameId) {
    return gameRoomRepository
        .findById(gameId)
        .orElseThrow(
            () -> new EntityNotFoundException("Game with id: " + gameId + " is not found"));
  }

  @Override
  @Transactional
  public void matchGame(Long userId) {
    // 1. Check if user is in the match queue
    boolean exists =
        Boolean.TRUE.equals(
            redisTemplate
                .opsForSet()
                .isMember(RedisKeys.set(GameType.TICTACTOE), userId.toString()));

    if (exists) {
      // waiting
      return;
    }

    // 2. Try to get waiting player (FIFO)
    String waitingUserIdStr =
        redisTemplate.opsForList().rightPop(RedisKeys.queue(GameType.TICTACTOE));

    if (waitingUserIdStr != null) {
      Long waitingUserId = Long.valueOf(waitingUserIdStr);

      User waitingUser =
          userRepository
              .findById(waitingUserId)
              .orElseThrow(() -> new EntityNotFoundException("User not found"));

      User currentUser =
          userRepository
              .findById(userId)
              .orElseThrow(() -> new EntityNotFoundException("User not found"));

      // 2. Found a player -> create game room
      TicTacToeGameRoom room = createGame(userId, waitingUserId);

      // Remove waiting user id from set
      redisTemplate.opsForSet().remove(RedisKeys.set(GameType.TICTACTOE), waitingUserIdStr);

      messagingTemplate.convertAndSendToUser(
          waitingUser.getEmail(), WsChannels.userMatch(GameType.TICTACTOE), room.getId());

      messagingTemplate.convertAndSendToUser(
          currentUser.getEmail(), WsChannels.userMatch(GameType.TICTACTOE), room.getId());
    }

    // 3. No player -> push current user to queue & set
    redisTemplate.opsForList().leftPush(RedisKeys.queue(GameType.TICTACTOE), userId.toString());
    redisTemplate.opsForSet().add(RedisKeys.set(GameType.TICTACTOE), userId.toString());
  }

  @Override
  @Transactional
  public void makeMove(MakeMoveRequest request, Long playerId) {
    MakeMoveEntity entity = mapper.toMakeMoveEntity(request);

    UUID gameId = entity.getGameId();
    int row = entity.getRow();
    int column = entity.getColumn();

    TicTacToeGameRoom room =
        gameRoomRepository
            .findById(gameId)
            .orElseThrow(
                () -> new EntityNotFoundException("Game with id: " + gameId + " is not found"));

    TicTacToeGameState state = room.getGameState();

    // not player turn / try to make move when game is not playing (waiting status)
    if (state.getStatus().equals(TicTacToeGameStatus.WAITING)) {
      throw new IllegalStateException("Game is not playing");
    }

    if (!state.getCurrentTurnPlayerId().equals(playerId)) {
      throw new IllegalStateException("Illegal move");
    }

    List<String> currentBoard = state.getCurrentBoard();

    int index = row * 3 + column;

    // cell already taken
    if (!currentBoard.get(index).isEmpty()) {
      throw new IllegalStateException("Invalid move");
    }

    currentBoard.set(index, playerId.toString());
    boolean hasWinner = checkWinner(currentBoard, playerId);

    state.setCurrentBoard(currentBoard);
    state.setTurnCount(state.getTurnCount() + 1);
    state.setLastMoveTimestamp(Instant.now().toEpochMilli());

    Long currentTurnPlayerId = getOtherId(room.getPlayerIds(), playerId);
    state.setCurrentTurnPlayerId(currentTurnPlayerId);

    MakeMoveResponse response =
        MakeMoveResponse.builder()
            .currentBoard(currentBoard)
            .currentTurnPlayerId(currentTurnPlayerId)
            .turnCount(state.getTurnCount())
            .build();

    // if has winner -> set winner id, increase player win round, set status to waiting
    if (hasWinner) {
      state.setWinnerId(playerId);

      Map<Long, Integer> winRounds = room.getWinRounds();
      winRounds.put(playerId, winRounds.getOrDefault(playerId, 0) + 1);

      state.setStatus(TicTacToeGameStatus.WAITING);

      response.setStatus(TicTacToeGameStatus.WAITING);
      response.setWinnerId(playerId);
      response.setWinRounds(winRounds);
    }

    // if draw
    if (state.getCurrentBoard().stream().allMatch(tile -> !tile.isEmpty())) {
      state.setStatus(TicTacToeGameStatus.WAITING);
      response.setStatus(TicTacToeGameStatus.WAITING);
    }

    room.setGameState(state);
    gameRoomRepository.save(room);

    User currentTurnPlayer =
        userRepository
            .findById(currentTurnPlayerId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    User anotherPlayer =
        userRepository
            .findById(playerId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    messagingTemplate.convertAndSendToUser(
        currentTurnPlayer.getEmail(), WsChannels.userMove(GameType.TICTACTOE), response);

    messagingTemplate.convertAndSendToUser(
        anotherPlayer.getEmail(), WsChannels.userMove(GameType.TICTACTOE), response);
  }

  @Override
  @Transactional
  public void newGame(UUID gameId, Long playerId) {
    TicTacToeGameRoom room =
        gameRoomRepository
            .findById(gameId)
            .orElseThrow(() -> new EntityNotFoundException("Game not found"));

    // if user is not in the room
    if (!room.getPlayerIds().contains(playerId)) {
      throw new IllegalArgumentException("Player is not part of this game");
    }

    TicTacToeGameState state = room.getGameState();

    // if previous game is not finished yet
    if (state.getStatus().equals(TicTacToeGameStatus.PLAYING)) {
      throw new IllegalStateException("Game should be finished before new game starts");
    }

    // let the loser of previous game to be the first turn
    Long previousWinnerId = state.getWinnerId();

    if (previousWinnerId == null) {
      previousWinnerId = playerId;
    }

    List<Long> playerIds = room.getPlayerIds();
    Long firstTurnPlayerId = getOtherId(playerIds, previousWinnerId);

    TicTacToeGameState updatedState = resetGameState(state, firstTurnPlayerId);

    room.setGameState(updatedState);
    room.setRound(room.getRound() + 1);

    TicTacToeGameRoom updatedRoom = gameRoomRepository.save(room);

    Long anotherUserId = getOtherId(playerIds, playerId);

    User currentUser =
        userRepository
            .findById(playerId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    User anotherUser =
        userRepository
            .findById(anotherUserId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    messagingTemplate.convertAndSendToUser(
        currentUser.getEmail(), WsChannels.userNewGame(GameType.TICTACTOE), updatedRoom);

    messagingTemplate.convertAndSendToUser(
        anotherUser.getEmail(), WsChannels.userNewGame(GameType.TICTACTOE), updatedRoom);
  }

  // note: HELPER FUNCTIONS
  @Transactional
  private TicTacToeGameRoom createGame(Long firstPlayerId, Long secondPlayerId) {
    User firstPlayer =
        userRepository
            .findById(firstPlayerId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    User secondPlayer =
        userRepository
            .findById(secondPlayerId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    UUID gameId = UUID.randomUUID();

    Game newGame = Game.builder().id(gameId).round(1L).build();

    gameRepository.save(newGame);

    UserGameId firstPlayerGameId = UserGameId.builder().user(firstPlayerId).game(gameId).build();
    UserGameId secondPlayerGameId = UserGameId.builder().user(secondPlayerId).game(gameId).build();

    UserGame firstPlayerGame =
        UserGame.builder().user(firstPlayer).game(newGame).userGameId(firstPlayerGameId).build();
    UserGame secondPlayerGame =
        UserGame.builder().user(secondPlayer).game(newGame).userGameId(secondPlayerGameId).build();

    userGameRepository.save(firstPlayerGame);
    userGameRepository.save(secondPlayerGame);

    TicTacToeGameState gameState =
        TicTacToeGameState.builder()
            .currentBoard(Collections.nCopies(9, ""))
            .firstTurnPlayerId(firstPlayerId)
            .currentTurnPlayerId(firstPlayerId)
            .turnCount(0)
            .status(TicTacToeGameStatus.PLAYING)
            .winnerId(null)
            .lastMoveTimestamp(null)
            .build();

    Map<Long, Integer> winRounds = new HashMap<>();
    winRounds.put(firstPlayerId, 0);
    winRounds.put(secondPlayerId, 0);

    TicTacToeGameRoom gameRoom =
        TicTacToeGameRoom.builder()
            .id(gameId)
            .playerIds(List.of(firstPlayerId, secondPlayerId))
            .winRounds(winRounds)
            .round(1L)
            .gameState(gameState)
            .build();

    return gameRoomRepository.save(gameRoom);
  }

  private boolean checkWinner(List<String> board, Long playerId) {
    int size = 3;
    // Horizontal check
    for (int r = 0; r < size; r++) {
      boolean win = true;

      for (int c = 0; c < size; c++) {
        if (!playerId.toString().equals(getCell(board, r, c))) {
          win = false;
          break;
        }
      }
      if (win) return true;
    }

    // Vertical check
    for (int c = 0; c < size; c++) {
      boolean win = true;
      for (int r = 0; r < size; r++) {
        if (!playerId.toString().equals(getCell(board, r, c))) {
          win = false;
          break;
        }
      }
      if (win) return true;
    }

    // Main diagonal
    boolean winDig = true;

    for (int i = 0; i < 3; i++) {
      if (!playerId.toString().equals(getCell(board, i, i))) {
        winDig = false;
        break;
      }
    }

    if (winDig) return true;

    // Anti-diagonal
    boolean winAnti = true;

    for (int i = 0; i < 3; i++) {
      if (!playerId.toString().equals(getCell(board, i, 2 - i))) {
        winAnti = false;
        break;
      }
    }
    if (winAnti) return true;

    return false;
  }

  private String getCell(List<String> board, int row, int column) {
    return board.get(row * 3 + column);
  }

  private Long getOtherId(List<Long> ids, Long givenId) {
    return ids.get(0).equals(givenId) ? ids.get(1) : ids.get(0);
  }

  private TicTacToeGameState resetGameState(TicTacToeGameState state, Long firstTurnPlayerId) {
    state.setCurrentBoard(Collections.nCopies(9, ""));
    state.setCurrentTurnPlayerId(firstTurnPlayerId);
    state.setTurnCount(0);
    state.setStatus(TicTacToeGameStatus.PLAYING);
    state.setWinnerId(null);
    state.setLastMoveTimestamp(null);

    return state;
  }
}
