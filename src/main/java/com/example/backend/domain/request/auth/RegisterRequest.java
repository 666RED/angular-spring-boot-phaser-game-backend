package com.example.backend.domain.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
  @NotBlank(message = "Name is required")
  @Size(min = 3, max = 50, message = "Name should be between 3 to 50 characters")
  private String name;

  @NotBlank(message = "Email is required")
  @Email(message = "Email is invalid")
  @Size(max = 100, message = "Email should not exceed 100 characters")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 50, message = "Password should be between 8 and 50 characters")
  private String password;

  private String provider;
}
