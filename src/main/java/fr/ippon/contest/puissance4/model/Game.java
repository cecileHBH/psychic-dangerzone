package fr.ippon.contest.puissance4.model;

import java.util.Arrays;
import java.util.Observable;
import java.util.Random;

import fr.ippon.contest.puissance4.Puissance4.EtatJeu;

public class Game extends Observable {

	private char[][] grid = new char[Constants.NB_OF_LINES][Constants.NB_OF_COLUMNS];

	private Player player = Player.DEFAULT;

	private char nextPlayer = Player.DEFAULT.getValue();

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
		this.nextPlayer = findNextPlayer().getValue();
	}

	private void checkGrid(char[][] grid) {
		if (grid.length != Constants.NB_OF_LINES) {
			throw new IllegalArgumentException(String.format(
					"grille invalide - %s lignes seulement.", grid.length));
		}

		if (grid[0].length != Constants.NB_OF_COLUMNS) {
			throw new IllegalArgumentException(String.format(
					"grille invalide - %s colonnes seulement.", grid[0].length));
		}
	}

	/**
     * 
     */
	private void initFirstPlayer() {

		this.player = getRandomPlayer();

	}

	private Player getRandomPlayer() {
		Random random = new Random();
		if (random.nextBoolean()) {
			return Player.J;
		} else {
			return Player.R;
		}
	}

	/**
	 * 
	 */
	public void setNextRoundPlayer() {

		this.setPlayer(findNextPlayer());
	}

	/**
	 * 
	 */
	public Player findNextPlayer() {
		switch (this.getPlayer()) {
		case J:
			return Player.R;

		case R:
			return Player.J;

		default:
			return getRandomPlayer();
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

	public synchronized EtatJeu getEtat() {
		return etat;
	}

	public void setEtat(EtatJeu etat) {

		synchronized (this) {
			this.etat = etat;
		}
		setChanged();
		notifyObservers();
	}

	public void setNextPlayer(char nextPlayer) {
		this.nextPlayer = nextPlayer;
	}

	public char getNextPlayer() {
		return nextPlayer;
	}

}
