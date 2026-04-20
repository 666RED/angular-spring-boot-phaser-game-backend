package com.example.backend.service.game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
  // redis

  // todo: change later for more games
  // @Override
  // public List<GameRoom> listGames() {
  //   List<GameRoom> list = new ArrayList<>();
  //   gameRoomRepository.findAll().forEach(list::add);
  //   return list;
  // }
}
