package com.santiago.service;

import com.santiago.exception.InvalidGameException;
import com.santiago.exception.InvalidParamException;
import com.santiago.exception.NotFoundException;
import com.santiago.model.Game;
import com.santiago.model.GamePlay;
import com.santiago.model.Player;
import com.santiago.model.TicToe;
import com.santiago.repository.GameRepository;
import com.santiago.repository.PlayerRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.santiago.model.GameStatus.*;

@Service
public class GameService {
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;

    public GameService(GameRepository gameRepository, PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    public Game createGame(Player player) {
        Game game = new Game();
        game.setBoard(new int[3][3]);
        game.setGameId(UUID.randomUUID().toString());
        game.setPlayer1(player);
        game.setStatus(NEW);

        if (!playerRepository.existsById(player.getId())) {
            playerRepository.save(player);
        }

        gameRepository.save(game);

        return game;
    }

    public Game connectToGame(Player player2, String gameId)
            throws InvalidParamException, InvalidGameException {
        if (!gameRepository.existsById(gameId)) {
            throw new InvalidParamException("Game with provided id does not exist");
        }

        Game game = gameRepository.findById(gameId).get();
        if (game.getPlayer2() != null) {
            throw new InvalidGameException("Game does not allow any more players");
        }

        if (!playerRepository.existsById(player2.getId())) {
            playerRepository.save(player2);
        }

        game.setPlayer2(player2);
        game.setStatus(IN_PROGRESS);
        gameRepository.save(game);
        return game;
    }

    public Game connectToRandomGame(Player player2) throws NotFoundException {
        List<Game> games = gameRepository.findByStatus(NEW);

        if (games.size() == 0) {
            throw new NotFoundException("No new games found");
        }

        Game game = games.get(0);

        game.setPlayer2(player2);
        game.setStatus(IN_PROGRESS);
        gameRepository.save(game);
        return game;
    }

    public Game gamePlay(GamePlay gamePlay) throws NotFoundException, InvalidGameException {
        if (!gameRepository.existsById(gamePlay.getGameId())) {
            throw new NotFoundException("Game not found");
        }

        Game game = gameRepository.findById(gamePlay.getGameId()).get();

        if (game.getStatus().equals(FINISHED)) {
            throw new InvalidGameException("Game is already finished");
        }

        int[][] board = game.getBoard();
        board[gamePlay.getCoordinateY()][gamePlay.getCoordinateX()] = gamePlay.getType().getValue();

        if (checkWinner(board, TicToe.X)) {
            game.setWinner(TicToe.X);
            game.setStatus(FINISHED);
        } else if (checkWinner(board, TicToe.O)) {
            game.setWinner(TicToe.O);
            game.setStatus(FINISHED);
        }

        gameRepository.save(game);

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
            for (int index : line) {
                if (boardArray[index] == playerSymbol.getValue()) {
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
