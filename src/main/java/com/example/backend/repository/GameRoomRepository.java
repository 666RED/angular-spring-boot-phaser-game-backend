package com.example.backend.repository;

import com.example.backend.domain.entity.redis.game.GameRoom;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRoomRepository extends CrudRepository<GameRoom, UUID> {}
