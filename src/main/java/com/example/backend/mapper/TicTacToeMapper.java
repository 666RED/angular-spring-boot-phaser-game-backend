package com.example.backend.mapper;

import com.example.backend.domain.request.game.ticTacToe.MakeMoveEntity;
import com.example.backend.domain.request.game.ticTacToe.MakeMoveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicTacToeMapper {
  MakeMoveEntity toMakeMoveEntity(MakeMoveRequest request);
}
