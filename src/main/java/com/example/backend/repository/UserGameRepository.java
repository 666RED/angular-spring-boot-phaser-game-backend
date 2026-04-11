package com.example.backend.repository;

import com.example.backend.domain.embeddedId.UserGameId;
import com.example.backend.domain.entity.UserGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGameRepository extends JpaRepository<UserGame, UserGameId> {}
