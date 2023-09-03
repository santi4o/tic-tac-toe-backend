package com.santiago.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.santiago.model.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
