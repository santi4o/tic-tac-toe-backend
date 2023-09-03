package com.santiago.model;

import lombok.Data;

@Data
public class GamePlay {
    private String gameId;
    private TicToe type;
    private Integer coordinateX;
    private Integer coordinateY;
}
