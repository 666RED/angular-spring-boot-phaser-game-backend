package com.example.backend.repository;

import com.example.backend.domain.entity.redis.ticTacToe.TicTacToeGameRoom;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicTacToeGameRoomRepository extends CrudRepository<TicTacToeGameRoom, UUID> {
  TicTacToeGameRoom findFirstByPlayerCount(int playerCount);
}
