package com.example.backend.interceptor;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Override
  public boolean beforeHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Map<String, Object> attributes) {

    if (request instanceof ServletServerHttpRequest servletRequest) {
      Cookie[] cookies = servletRequest.getServletRequest().getCookies();
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if ("jwt".equals(cookie.getName())) {
            String userId = extractUserId(cookie.getValue());

            attributes.put(
                "user", new UsernamePasswordAuthenticationToken(userId, null, List.of()));
            return true;
          }
        }
      }
    }
    return false; // reject handshake if no valid JWT
  }

  @Override
  public void afterHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Exception exception) {}

  private String extractUserId(String token) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    SecretKey signingKey = Keys.hmacShaKeyFor(keyBytes);
    return Jwts.parser()
        .verifyWith(signingKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getId();
  }
}
