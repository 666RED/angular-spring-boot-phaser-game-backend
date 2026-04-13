package com.example.backend.security;

import com.example.backend.service.auth.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final AuthenticationService authenticationService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String token = extractToken(request);

      if (token != null) {
        UserDetails userDetails = authenticationService.validateToken(token);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (userDetails instanceof CustomUserDetails) {
          request.setAttribute("userId", ((CustomUserDetails) userDetails).getId());
        }
      }
    } catch (Exception e) {
      // note: Do not throw exception, just don't authenticate the user
      log.warn("Received invalid auth token");
    }

    filterChain.doFilter(request, response);
  }

  private String extractToken(HttpServletRequest request) {
    if (request.getCookies() == null) return null;

    for (Cookie cookie : request.getCookies()) {
      if ("jwt".equals(cookie.getName())) {
        return cookie.getValue();
      }
    }

    return null;
  }
}
