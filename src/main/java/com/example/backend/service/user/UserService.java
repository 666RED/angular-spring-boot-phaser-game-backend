package com.example.backend.service.user;

import com.example.backend.domain.dto.user.UserDto;

public interface UserService {
  UserDto getUser(Long userId);

  UserDto getUserByEmail(String email);
}
