package com.santiago.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "games")
@Data
public class Game {
    @Id
    private String gameId;

    @ManyToOne()
    private Player player1;

    @ManyToOne()
    private Player player2;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @ElementCollection
    @OrderColumn
    private int[][] board;

    @Enumerated(EnumType.STRING)
    private TicToe winner;
}
