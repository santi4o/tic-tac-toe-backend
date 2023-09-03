package com.santiago.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.santiago.model.Game;
import com.santiago.model.GameStatus;

import java.util.List;


public interface GameRepository extends JpaRepository<Game, String> {
    List<Game> findByStatus(GameStatus status);
}
