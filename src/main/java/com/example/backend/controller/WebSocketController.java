package com.example.backend.controller;

import com.example.backend.domain.response.websocket.WsResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WebSocketController {
  @MessageMapping("/send")
  @SendTo("/topic/messages")
  public WsResponse<Map<String, Object>> greeting(Map<String, Object> message) {
    Map<String, Object> payload = new HashMap<>();

    payload.put("data", message);

    return new WsResponse<>(payload);
  }

  @MessageMapping("/game.join")
  @SendTo("/topic/joined-game")
  public WsResponse<Map<String, Object>> joinedGames(Map<String, Object> message) {
    Map<String, Object> payload = new HashMap<>();

    payload.put("message", "already joined");

    return new WsResponse<>(payload);
  }
}
