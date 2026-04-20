package com.example.backend.domain.dto.user;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
  private Long id;
  private String name;

  // todo: may use this later
  // private String email; // note: Used as username in Spring Security
  // private String provider;
  // private String providerId;

  private LocalDateTime createdAt;
}
