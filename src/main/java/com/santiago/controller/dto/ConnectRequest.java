package com.santiago.controller.dto;

import com.santiago.model.Player;

import lombok.Data;

@Data
public class ConnectRequest {
    private Player player;
    private String gameId;
}
