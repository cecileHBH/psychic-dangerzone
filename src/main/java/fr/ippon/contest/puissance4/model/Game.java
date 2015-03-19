package fr.ippon.contest.puissance4.model;

import java.util.Arrays;
import java.util.Random;

import fr.ippon.contest.puissance4.Puissance4.EtatJeu;

public class Game {

    private char[][] grid = new char[Constants.NB_OF_LINES][Constants.NB_OF_COLUMNS];

    private Player player = Player.DEFAULT;

    private EtatJeu etat = null;

    /**
     * 
     */
    public void initGame() {

        for (char[] row : grid) {
            Arrays.fill(row, Player.DEFAULT.getValue());
        }

        this.etat = EtatJeu.EN_COURS;

        initFirstPlayer();
    }

    public void initGame(char[][] grid, char tour) {
        Player actualPlayer = Player.findJoueurByValue(tour);

        if (actualPlayer.equals(Player.DEFAULT)) {
            throw new IllegalArgumentException("joueur manquant");
        }

        checkGrid(grid);

        this.grid = grid;
        this.player = actualPlayer;
        this.etat = null;
    }

    private void checkGrid(char[][] grid) {
        if (grid.length != Constants.NB_OF_LINES) {
            throw new IllegalArgumentException(String.format("grille invalide - %s lignes seulement.", grid.length));
        }

        if (grid[0].length != Constants.NB_OF_COLUMNS) {
            throw new IllegalArgumentException(String.format("grille invalide - %s colonnes seulement.", grid[0].length));
        }
    }

    /**
     * 
     */
    private void initFirstPlayer() {
        Random random = new Random();
        if (random.nextBoolean()) {
            this.player = Player.J;
        } else {
            this.player = Player.R;
        }
    }

    public char getCell(int line, int column) {
        return grid[line][column];
    }

    public void setCell(int line, int column, char value) {
        grid[line][column] = value;
    }

    public char[][] getGrid() {
        return grid;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player tour) {
        this.player = tour;
    }

    public EtatJeu getEtat() {
        return etat;
    }

    public void setEtat(EtatJeu etat) {
        this.etat = etat;
    }

}
