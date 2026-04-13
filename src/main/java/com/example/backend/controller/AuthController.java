package com.example.backend.controller;

import com.example.backend.domain.dto.user.UserDto;
import com.example.backend.domain.request.auth.LoginRequest;
import com.example.backend.domain.request.auth.RegisterRequest;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.service.auth.AuthenticationService;
import com.example.backend.service.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
  private final UserService userService;

  @Value("${jwt.expires-in}")
  private long jwtExpiresIn;

  @PostMapping("/login")
  public ResponseEntity<UserDto> login(
      @RequestBody LoginRequest request, HttpServletResponse response) {
    UserDetails userDetails =
        authenticationService.authenticate(request.getEmail(), request.getPassword());

    String tokenValue = authenticationService.generateToken(userDetails);

    ResponseCookie cookie =
        ResponseCookie.from("jwt", tokenValue)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(86400)
            .sameSite("Lax")
            .build();

    response.addHeader("Set-Cookie", cookie.toString());

    UserDto userDto = userService.getUserByEmail(userDetails.getUsername());

    return ResponseEntity.ok(userDto);
  }

  @PostMapping("/register")
  public ResponseEntity<UserDto> register(
      @RequestBody RegisterRequest request, HttpServletResponse response) {
    UserDetails userDetails = authenticationService.register(request);

    String tokenValue = authenticationService.generateToken(userDetails);

    Cookie cookie = new Cookie("jwt", tokenValue);
    cookie.setHttpOnly(true);
    cookie.setSecure(false); // true in production (HTTPS)
    cookie.setPath("/");
    cookie.setMaxAge(24 * 60 * 60); // 1 day

    response.addCookie(cookie);

    UserDto userDto = userService.getUserByEmail(userDetails.getUsername());

    return ResponseEntity.ok(userDto);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      @AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response) {
    if (userDetails == null) {
      throw new IllegalStateException("You are not allowed to perform this action");
    }

    ResponseCookie cookie =
        ResponseCookie.from("jwt", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(0)
            .sameSite("Lax")
            .build();

    response.addHeader("Set-Cookie", cookie.toString());

    return ResponseEntity.noContent().build();
  }
}
