package com.example.backend.controller;

import com.example.backend.domain.request.auth.AuthResponse;
import com.example.backend.domain.request.auth.LoginRequest;
import com.example.backend.domain.request.auth.RegisterRequest;
import com.example.backend.service.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthenticationService authenticationService;

  @Value("${jwt.expires-in}")
  private long jwtExpiresIn;

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
    UserDetails userDetails =
        authenticationService.authenticate(request.getEmail(), request.getPassword());

    String tokenValue = authenticationService.generateToken(userDetails);

    AuthResponse authResponse =
        AuthResponse.builder().token(tokenValue).expiresIn(jwtExpiresIn).build();
    return ResponseEntity.ok(authResponse);
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
    UserDetails userDetails = authenticationService.register(request);

    String tokenValue = authenticationService.generateToken(userDetails);

    AuthResponse authResponse =
        AuthResponse.builder().token(tokenValue).expiresIn(jwtExpiresIn).build();

    return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
  }
}
