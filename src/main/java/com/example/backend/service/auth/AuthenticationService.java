package com.example.backend.service.auth;

import com.example.backend.domain.request.auth.RegisterRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
  UserDetails authenticate(String email, String password);

  UserDetails register(RegisterRequest request);

  String generateToken(UserDetails userDetails);

  UserDetails validateToken(String token);
}
