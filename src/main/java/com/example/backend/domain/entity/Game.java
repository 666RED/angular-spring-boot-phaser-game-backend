package com.example.backend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "games")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Game {
  @Id private UUID id;

  @Column(nullable = false)
  private Long round;

  @OneToMany(mappedBy = "game")
  @Builder.Default
  private List<UserGame> userGames = new ArrayList<>();
}
