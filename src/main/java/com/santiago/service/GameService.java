package com.santiago.service;

import com.santiago.exception.InvalidGameException;
import com.santiago.exception.InvalidParamException;
import com.santiago.exception.NotFoundException;
import com.santiago.model.Game;
import com.santiago.model.GamePlay;
import com.santiago.model.Player;
import com.santiago.model.TicToe;
import com.santiago.storage.GameStorage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.santiago.model.GameStatus.*;

@Service
@AllArgsConstructor
public class GameService {
    public Game createGame(Player player) {
        Game game = new Game();
        game.setBoard(new int[3][3]);
        game.setGameId(UUID.randomUUID().toString());
        game.setPlayer1(player);
        game.setStatus(NEW);

        GameStorage.getInstance().setGame(game);

        return game;
    }

    public Game connectToGame(Player player2, String gameId)
            throws InvalidParamException, InvalidGameException {
        if (GameStorage.getInstance().getGames().containsKey(gameId)) {
            throw new InvalidParamException("Game with provided id does not exist");
        }

        Game game = GameStorage.getInstance().getGames().get(gameId);
        if (game.getPlayer2() != null) {
            throw new InvalidGameException("Game does not allow any more players");
        }

        game.setPlayer2(player2);
        game.setStatus(IN_PROGRESS);
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game connectToRandomGame(Player player2) throws NotFoundException {
        Game game = GameStorage.getInstance().getGames().values().stream()
                .filter(it -> it.getStatus().equals(NEW)).findFirst()
                .orElseThrow(() -> new NotFoundException("Game not found"));

        game.setPlayer2(player2);
        game.setStatus(IN_PROGRESS);
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game gamePlay(GamePlay gamePlay) throws NotFoundException, InvalidGameException {
        if (!GameStorage.getInstance().getGames().containsKey(gamePlay.getGameId())) {
            throw new NotFoundException("Game not found");
        }

        Game game = GameStorage.getInstance().getGames().get(gamePlay.getGameId());

        if (game.getStatus().equals(FINISHED)) {
            throw new InvalidGameException("Game is already finished");
        }

        int[][] board = game.getBoard();
        board[gamePlay.getCoordinateY()][gamePlay.getCoordinateX()] = gamePlay.getType().getValue();
        checkWinner(game.getBoard(), TicToe.X);
        checkWinner(game.getBoard(), TicToe.O);

        //GameStorage.getInstance().setGame(game);

        return game;
    }

    private Boolean checkWinner(int[][] board, TicToe playerSymbol) {
        int[] boardArray = new int[9];
        int mappingIndex = 0;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                boardArray[mappingIndex++] = board[i][j];
            }
        }

        int[][] winCombinations = {
                { 0, 1, 2 },
                { 3, 4, 5 },
                { 6, 7, 8 },
                { 0, 3, 6 },
                { 1, 4, 7 },
                { 2, 5, 8 },
                { 0, 4, 8 },
                { 2, 4, 6 }
        };

        for (int[] line : winCombinations) {
            int counter = 0;
            for (int symbol : line) {
                if (symbol == playerSymbol.getValue()) {
                    counter++;
                    if (counter == 3) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
