package com.example.backend.interceptor;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = extractTokenFromCookie(accessor);
      if (token != null) {
        String userId = extractUserId(token);
        accessor.setUser(new UsernamePasswordAuthenticationToken(userId, null, List.of()));
      }
    }
    return message;
  }

  private String extractTokenFromCookie(StompHeaderAccessor accessor) {
    List<String> cookieHeaders = accessor.getNativeHeader("cookie");
    if (cookieHeaders == null) return null;
    for (String cookieHeader : cookieHeaders) {
      for (String part : cookieHeader.split(";")) {
        String trimmed = part.trim();
        if (trimmed.startsWith("jwt=")) {
          return trimmed.substring(4);
        }
      }
    }
    return null;
  }

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
