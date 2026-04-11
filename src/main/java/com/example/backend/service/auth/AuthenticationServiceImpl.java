package com.example.backend.service.auth;

import com.example.backend.domain.entity.User;
import com.example.backend.domain.request.auth.RegisterRequest;
import com.example.backend.domain.request.auth.RegisterUser;
import com.example.backend.exception.DuplicateResourceException;
import com.example.backend.mapper.AuthMapper;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.CustomUserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final AuthMapper authMapper;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.expiry-ms}")
  private Long jwtExpiryMs;

  @Override
  public UserDetails authenticate(String email, String password) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

    return userDetailsService.loadUserByUsername(email);
  }

  @Override
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();

    return Jwts.builder()
        .claims(claims)
        .subject(userDetails.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + jwtExpiryMs))
        .signWith(getSigningKey())
        .compact();
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  @Override
  public UserDetails validateToken(String token) {
    String username = extractUsername(token);
    return userDetailsService.loadUserByUsername(username);
  }

  private String extractUsername(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  @Override
  public UserDetails register(RegisterRequest request) {
    RegisterUser registerUserEntity = authMapper.toEntity(request);

    String name = registerUserEntity.getName();
    String email = registerUserEntity.getEmail();
    String password = registerUserEntity.getPassword();

    boolean isEmailExisted = userRepository.existsByEmail(email);

    if (isEmailExisted) {
      throw new DuplicateResourceException("Email already existed: " + email);
    }

    // todo: Check google / github provider
    User newUser =
        User.builder().name(name).email(email).password(passwordEncoder.encode(password)).build();

    userRepository.save(newUser);

    return new CustomUserDetails(newUser);
  }
}
