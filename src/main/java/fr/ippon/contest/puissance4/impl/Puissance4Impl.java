package fr.ippon.contest.puissance4.impl;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import fr.ippon.contest.puissance4.Puissance4;
import fr.ippon.contest.puissance4.model.ComputationType;
import fr.ippon.contest.puissance4.model.Constants;
import fr.ippon.contest.puissance4.model.Game;

public class Puissance4Impl implements Puissance4, Observer {

	private Game game = new Game();

	private boolean winnerFound = false;

	private Map<ComputationType, Analysis> analylis = new HashMap<>();

	public Puissance4Impl() {
		super();
		analylis.put(ComputationType.HORIZONTAL, new HorizontalAnalysis());
		analylis.put(ComputationType.VERTICAL, new VerticalAnalysis());
		analylis.put(ComputationType.DIAGONAL_BOTTOM_TOP,
				new DiagonalBottomToTopAnalysis());
		analylis.put(ComputationType.DIAGONAL_TOP_BOTTOM,
				new DiagonalTopToBottomAnalysis());
	}

	@Override
	public void nouveauJeu() {
		game = new Game();
		game.initGame();
		initGameStatusObserverData();
	}

	@Override
	public void chargerJeu(char[][] grid, char tour) {

		game = new Game();
		game.initGame(grid, tour);

		initGameStatusObserverData();
		computeGameStatus();

	}

	@Override
	public EtatJeu getEtatJeu() {
		return game.getEtat();
	}

	@Override
	public char getTour() {
		return game.getPlayer().getValue();
	}

	@Override
	public char getOccupant(int ligne, int colonne) {

		checkColumnIndex(colonne);

		checkLineIndex(ligne);

		return game.getCell(ligne, colonne);
	}

	@Override
	public void jouer(int colonne) {

		checkColumnIndex(colonne);

		int lineIndex = findEmptyLineIndex(colonne);
		if (lineIndex == Constants.DEFAULT_INDEX) {
			throw new IllegalStateException(String.format(
					"La colonne %s est pleine.", colonne));
		}

		// play round
		game.setCell(lineIndex, colonne, game.getPlayer().getValue());
		game.setNextRoundPlayer();
		game.setNextPlayer(game.findNextPlayer().getValue());

		// find if there is a winner
		initGameStatusObserverData();
		updateGameStatusAfterMove(lineIndex, colonne);
	}

	/**
	 * check if the column index is correct, throws IllegalArgumentException if
	 * not
	 * 
	 * @param colonne
	 *            index
	 */
	private void checkColumnIndex(int colonne) {
		if (colonne < Constants.FIRST_INDEX
				|| colonne >= Constants.NB_OF_COLUMNS) {
			throw new IllegalArgumentException(String.format(
					"La colonne %s n'est pas valide", colonne));
		}
	}

	/**
	 * check if the ligne index is correct, throws IllegalArgumentException if
	 * not
	 * 
	 * @param ligne
	 *            index
	 */
	private void checkLineIndex(int ligne) {
		if (ligne < Constants.FIRST_INDEX || ligne >= Constants.NB_OF_LINES) {
			throw new IllegalArgumentException(String.format(
					"La ligne %s n'est pas valide", ligne));
		}
	}

	/**
	 * Init game status observer data
	 */
	private void initGameStatusObserverData() {
		winnerFound = false;
		game.addObserver(this);

	}

	/**
	 * Compute game status of the grid
	 * 
	 */
	private void computeGameStatus() {

		if (game.getEtat() != null && !game.getEtat().equals(EtatJeu.EN_COURS)) {
			return;
		}

		Iterator<Analysis> analysisIterator = analylis.values().iterator();
		while (!winnerFound && analysisIterator.hasNext()) {
			Analysis analysis = analysisIterator.next();
			analysis.analyzeDimensions(this.game);
		}

		if (!winnerFound) {
			if (isGameOver()) {
				this.game.setEtat(EtatJeu.MATCH_NUL);
			} else {
				this.game.setEtat(EtatJeu.EN_COURS);
			}
		}
	}

	/**
	 * Determines if the game is over
	 * 
	 * @return
	 */
	private boolean isGameOver() {
		int full = 0;
		for (int i = Constants.FIRST_INDEX; i < Constants.NB_OF_COLUMNS; i++) {
			if (game.getCell(Constants.FIRST_INDEX, i) != Constants.DEFAULT_PLAYER) {
				full++;
			}
		}

		return full == Constants.NB_OF_COLUMNS;
	}

	/**
	 * @param line
	 * @param column
	 */
	private void updateGameStatusAfterMove(int line, int column) {

		if (game.getEtat().equals(EtatJeu.EN_COURS)) {

			Iterator<ComputationType> iterator = EnumSet.allOf(
					ComputationType.class).iterator();
			while (!winnerFound && iterator.hasNext()) {
				ComputationType computationType = iterator.next();
				computeGameStatus(line, column, computationType);
			}

		}
		if (!winnerFound && isGameOver()) {
			game.setEtat(EtatJeu.MATCH_NUL);
		}
	}

	/**
	 * @param line
	 * @param column
	 * @param computationType
	 */
	private void computeGameStatus(int line, int column,
			ComputationType computationType) {

		switch (computationType) {
		case HORIZONTAL:
			analylis.get(ComputationType.HORIZONTAL).analyzeCells(game, line);
			break;
		case VERTICAL:
			analylis.get(ComputationType.VERTICAL).analyzeCells(game, column);
			break;

		case DIAGONAL_TOP_BOTTOM:
			analylis.get(ComputationType.DIAGONAL_TOP_BOTTOM).analyzeCells(
					game, line, column);

			break;
		case DIAGONAL_BOTTOM_TOP:

			analylis.get(ComputationType.DIAGONAL_BOTTOM_TOP).analyzeCells(
					game, line, column);

			break;
		default:
			break;
		}

	}

	/**
	 * @param colonne
	 * @return
	 */
	private int findEmptyLineIndex(int colonne) {
		for (int i = Constants.NB_OF_LINES - 1; i >= Constants.FIRST_INDEX; i--) {
			if (game.getCell(i, colonne) == Constants.DEFAULT_PLAYER) {
				return i;
			}
		}

		return Constants.DEFAULT_INDEX;
	}

	@Override
	public void update(Observable o, Object arg) {
		winnerFound = true;
	}

}