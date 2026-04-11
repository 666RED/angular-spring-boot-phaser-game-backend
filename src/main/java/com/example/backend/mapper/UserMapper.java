package com.example.backend.mapper;

import com.example.backend.domain.dto.user.UserDto;
import com.example.backend.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
  UserDto toDto(User user);
}
