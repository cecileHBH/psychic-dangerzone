package fr.ippon.contest.puissance4.impl;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import fr.ippon.contest.puissance4.Puissance4;
import fr.ippon.contest.puissance4.model.ComputationType;
import fr.ippon.contest.puissance4.model.Constants;
import fr.ippon.contest.puissance4.model.Game;
import fr.ippon.contest.puissance4.model.GameScore;
import fr.ippon.contest.puissance4.model.Player;

public class Puissance4Impl implements Puissance4, Observer {

	private static final Logger LOG = Logger.getLogger(Puissance4Impl.class);

	private Game game = new Game();

	private boolean keepComputing = true;

	private int cptIter = 0;

	@Override
	public void nouveauJeu() {
		keepComputing = true;
		game = new Game();
		game.initGame();
		game.addObserver(this);

	}

	@Override
	public void chargerJeu(char[][] grid, char tour) {
		keepComputing = true;
		game = new Game();
		game.initGame(grid, tour);
		game.addObserver(this);

		updateGameStatus();
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
		return game.getCell(ligne, colonne);
	}

	@Override
	public void jouer(int colonne) {

		if (colonne < Constants.FIRST_INDEX
				|| colonne >= Constants.NB_OF_COLUMNS) {
			throw new IllegalArgumentException(String.format(
					"La colonne %s n'est pas valide", colonne));
		}

		int lineIndex = findEmptyLineIndex(colonne);
		if (!lineFound(lineIndex)) {
			throw new IllegalStateException(String.format(
					"La colonne %s est pleine.", colonne));
		}

		game.setCell(lineIndex, colonne, game.getPlayer().getValue());
		setNextPlayer();

		this.cptIter = 0;
		keepComputing = true;
		updateGameStatusAfterMove(lineIndex, colonne);

		LOG.debug("Compute game status in " + cptIter + " iterations.");
	}

	/**
	 * Update game status with the actual game status
	 */
	private void updateGameStatus() {
		this.cptIter = 0;
		gameStatus();

		LOG.debug("Compute game status in " + cptIter + " iterations.");

	}

	/**
	 * Compute game status and returns it
	 * 
	 * @return
	 */
	private void gameStatus() {

		if (game.getEtat() != null && !game.getEtat().equals(EtatJeu.EN_COURS)) {
			return;
		}

		simpleGameStatus(game.getGrid());
		if (!keepComputing) {
			return;
		}
		diagonalStatus(game.getGrid());
		if (!keepComputing) {
			return;
		}
		if (isGameOver()) {
			this.game.setEtat(EtatJeu.MATCH_NUL);
		} else {
			this.game.setEtat(EtatJeu.EN_COURS);
		}
	}

	/**
	 * Determine the game status based on the horizontal and vertical
	 * computation
	 * 
	 * @param grid
	 * @param nbOfLines
	 * @param nbOfColumns
	 * @return
	 */
	private void simpleGameStatus(char[][] grid) {

		horizontalStatus(grid);

		if (keepComputing) {
			verticalStatus(grid);
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
			if (game.getCell(Constants.FIRST_INDEX, i) != Player.DEFAULT
					.getValue()) {
				full++;
			}
		}

		return full == Constants.NB_OF_COLUMNS;
	}

	private boolean lineFound(int lineNumber) {
		return lineNumber > -1;
	}

	/**
	 * @param grid
	 * @param nbLines
	 * @param nbColumns
	 * @return
	 */
	private void horizontalStatus(char[][] grid) {

		for (int i = Constants.NB_OF_LINES - 1; i >= Constants.FIRST_INDEX; i--) {

			computeGameStatus(i, Constants.FIRST_INDEX,
					ComputationType.HORIZONTAL);

			if (!keepComputing) {
				break;
			}
		}

	}

	/**
	 * @param grid
	 * @param nbLines
	 * @param nbColumns
	 * @return
	 */
	private void verticalStatus(char[][] grid) {

		for (int i = Constants.FIRST_INDEX; i < Constants.NB_OF_COLUMNS; i++) {

			computeGameStatus(Constants.FIRST_INDEX, i,
					ComputationType.VERTICAL);

			if (!keepComputing) {
				break;
			}

		}
	}

	/**
	 * @param grid
	 * @param nbLines
	 * @param nbColumns
	 * @return
	 */
	private EtatJeu diagonalStatus(char[][] grid) {

		for (int i = Constants.FIRST_INDEX; i < Constants.DIAG_TOP_LINE_INDEX_LIMIT; i++) {

			computeGameStatus(i, Constants.FIRST_INDEX,
					ComputationType.DIAGONAL_TOP_BOTTOM);

			if (keepComputing) {
				computeGameStatus(i + Constants.DIAG_TOP_LINE_INDEX_LIMIT,
						Constants.FIRST_INDEX,
						ComputationType.DIAGONAL_BOTTOM_TOP);

			}

			if (!keepComputing) {
				break;
			}
		}

		for (int i = 1; i < Constants.DIAG_COLUMN_INDEX_LIMIT; i++) {

			computeGameStatus(Constants.FIRST_INDEX, i,
					ComputationType.DIAGONAL_TOP_BOTTOM);

			if (keepComputing) {
				computeGameStatus(5, i, ComputationType.DIAGONAL_BOTTOM_TOP);

			}

			if (!keepComputing) {
				break;
			}
		}

		return EtatJeu.EN_COURS;
	}

	private void updateGameStatusAfterMove(int line, int column) {

		if (game.getEtat() == null || game.getEtat().equals(EtatJeu.EN_COURS)) {

			for (ComputationType computationType : ComputationType.values()) {
				computeGameStatus(line, column, computationType);
				if (!keepComputing) {
					break;
				}
			}

		}
		if (isGameOver()) {
			game.setEtat(EtatJeu.MATCH_NUL);
		}
	}

	private GameScore gameStatusOfDiagTopToBottom(int colIndex, int rowIndex) {

		GameScore gameScore = new GameScore();

		for (int j = colIndex; j < Constants.NB_OF_COLUMNS
				&& rowIndex < Constants.NB_OF_LINES; j++, rowIndex++) {

			cptIter++;

			gameScore = updateGameScore(rowIndex, j, gameScore);

			if (!gameScore.needToContinue(Constants.NB_OF_COLUMNS - colIndex,
					this.game)) {
				break;
			}
		}

		return gameScore;
	}

	private GameScore gameStatusOfDiagBottomToTop(int colIndex,
			int firstRowIndex) {

		GameScore gameScore = new GameScore();

		for (int j = firstRowIndex; j >= Constants.FIRST_INDEX
				&& colIndex < Constants.NB_OF_COLUMNS; j--, colIndex++) {

			cptIter++;

			gameScore = updateGameScore(j, colIndex, gameScore);

			if (!gameScore.needToContinue(j, this.game)) {
				break;
			}
		}

		return gameScore;
	}

	private GameScore gameStatusOfCol(int column) {

		GameScore gameScore = new GameScore();

		for (int j = Constants.NB_OF_LINES - 1; j >= Constants.FIRST_INDEX; j--) {

			cptIter++;

			gameScore = updateGameScore(j, column, gameScore);

			if (gameScore.getPrecedent() == Player.DEFAULT
					|| !gameScore.needToContinue(j, this.game)) {
				break;
			}
		}

		return gameScore;
	}

	private GameScore gameStatusOfRow(int row) {

		GameScore gameScore = new GameScore();

		for (int j = Constants.FIRST_INDEX; j < Constants.NB_OF_COLUMNS; j++) {
			cptIter++;

			gameScore = updateGameScore(row, j, gameScore);

			if (!gameScore.needToContinue(Constants.NB_OF_COLUMNS - j,
					this.game)) {
				break;
			}
		}

		return gameScore;
	}

	private GameScore updateGameScore(int rowIndex, int colIndex,
			GameScore gameScore) {

		char cellContent = game.getCell(rowIndex, colIndex);

		return computePlayerCpt(gameScore, cellContent);
	}

	private GameScore computePlayerCpt(GameScore gameScore, char cellContent) {

		Player cellPlayer = Player.findJoueurByValue(cellContent);
		if (cellPlayer == gameScore.getPrecedent()) {
			gameScore.addPointToPlayer(cellPlayer);

		} else {
			gameScore.initPlayerScore(cellPlayer);
		}

		gameScore.setPrecedent(cellPlayer);

		return gameScore;
	}

	private void computeGameStatus(int line, int column,
			ComputationType computationType) {

		switch (computationType) {
		case HORIZONTAL:
			gameStatusOfRow(line);
			break;
		case VERTICAL:
			gameStatusOfCol(column);
			break;

		case DIAGONAL_TOP_BOTTOM:
			int firstColIndex = column - line < Constants.FIRST_INDEX ? Constants.FIRST_INDEX
					: column - line;
			int firstRowIndex = line - column < Constants.FIRST_INDEX ? Constants.FIRST_INDEX
					: line - column;
			if (firstRowIndex < Constants.DIAG_TOP_LINE_INDEX_LIMIT
					&& firstColIndex < Constants.DIAG_COLUMN_INDEX_LIMIT) {
				gameStatusOfDiagTopToBottom(firstColIndex, firstRowIndex);
			}
			break;
		case DIAGONAL_BOTTOM_TOP:
			firstRowIndex = column + line >= Constants.NB_OF_LINES ? Constants.NB_OF_LINES - 1
					: column + line;
			firstColIndex = column - (firstRowIndex - line);
			if (firstRowIndex > Constants.DIAG_BOTTOM_LINE_INDEX_LIMIT
					&& firstColIndex < Constants.DIAG_COLUMN_INDEX_LIMIT) {
				gameStatusOfDiagBottomToTop(firstColIndex, firstRowIndex);
			}
			break;
		default:
			break;
		}

	}

	private int findEmptyLineIndex(int colonne) {
		for (int i = Constants.NB_OF_LINES - 1; i >= Constants.FIRST_INDEX; i--) {
			if (game.getCell(i, colonne) == Player.DEFAULT.getValue()) {
				return i;
			}
		}

		return -1;
	}

	private void setNextPlayer() {
		switch (game.getPlayer()) {
		case J:
			game.setPlayer(Player.R);
			break;
		case R:
			game.setPlayer(Player.J);
			break;
		default:
			break;
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		keepComputing = false;
	}

	/**
	 * // * @return //
	 */
	// private EtatJeu diagonalStatus() {
	//
	// char[][] leftDiagonalGridPart1 = transposeGridLeftDiagonalPart1();
	// EtatJeu leftDiagonalGridPart1Status =
	// simpleGameStatus(leftDiagonalGridPart1, Constants.DIAG_NB_OF_LINES,
	// Constants.DIAG_NB_OF_COLUMNS);
	// if (existsWinner(leftDiagonalGridPart1Status)) {
	// return leftDiagonalGridPart1Status;
	// }
	//
	// char[][] leftDiagonalGridPart2 = transposeGridLeftDiagonalPart2();
	// EtatJeu leftDiagonalGridPart2Status =
	// simpleGameStatus(leftDiagonalGridPart2, Constants.DIAG_NB_OF_LINES,
	// Constants.DIAG_NB_OF_COLUMNS);
	// if (existsWinner(leftDiagonalGridPart2Status)) {
	// return leftDiagonalGridPart2Status;
	// }
	//
	// char[][] rightDiagonalGridPart1 = transposeGridRightDiagonalPart1();
	// EtatJeu rightDiagonalGridPart1Status =
	// simpleGameStatus(rightDiagonalGridPart1, Constants.DIAG_NB_OF_LINES,
	// Constants.DIAG_NB_OF_COLUMNS);
	// if (existsWinner(rightDiagonalGridPart1Status)) {
	// return rightDiagonalGridPart1Status;
	// }
	//
	// char[][] rightDiagonalGridPart2 = transposeGridRightDiagonalPart2();
	// EtatJeu rightDiagonalGridPart2Status =
	// simpleGameStatus(rightDiagonalGridPart2, Constants.DIAG_NB_OF_LINES,
	// Constants.DIAG_NB_OF_COLUMNS);
	// if (existsWinner(rightDiagonalGridPart2Status)) {
	// return rightDiagonalGridPart2Status;
	// }
	//
	// return EtatJeu.EN_COURS;
	// }
	//
	// /**
	// * @return
	// */
	// private char[][] transposeGridRightDiagonalPart1() {
	// char[][] rightDiagonalGrid = createDiagonalArray();
	//
	// List<Integer> linesToLookOver = Arrays.asList(1, 0, 0);
	// List<Integer> rowToLookOver = Arrays.asList(0, 1, 3);
	// List<Integer> colBeginIndex = Arrays.asList(1, 0, 1);
	//
	// for (int i = 0; i < linesToLookOver.size(); i++) {
	// int lineIndex = linesToLookOver.get(i);
	// int colIndex = rowToLookOver.get(i);
	//
	// int colCpt = colBeginIndex.get(i);
	//
	// for (int j = lineIndex; j < Constants.NB_OF_LINES && colIndex <
	// Constants.NB_OF_COLUMNS; j++) {
	// rightDiagonalGrid[i][colCpt] = game.getCell(j, colIndex);
	// colIndex++;
	// colCpt++;
	// }
	// }
	//
	// return rightDiagonalGrid;
	// }
	//
	// /**
	// * @return
	// */
	// private char[][] transposeGridRightDiagonalPart2() {
	// char[][] rightDiagonalGrid = createDiagonalArray();
	//
	// List<Integer> linesToLookOver = Arrays.asList(0, 0, 2);
	// List<Integer> rowToLookOver = Arrays.asList(2, 0, 0);
	// List<Integer> colBeginIndex = Arrays.asList(0, 0, 1);
	//
	// for (int i = 0; i < linesToLookOver.size(); i++) {
	// int lineIndex = linesToLookOver.get(i);
	// int colIndex = rowToLookOver.get(i);
	//
	// int colCpt = colBeginIndex.get(i);
	//
	// for (int j = lineIndex; j < Constants.NB_OF_LINES && colIndex <
	// Constants.NB_OF_COLUMNS; j++) {
	// rightDiagonalGrid[i][colCpt] = game.getCell(j, colIndex);
	// colIndex++;
	// colCpt++;
	// }
	// }
	//
	// return rightDiagonalGrid;
	// }
	//
	// /**
	// * @return
	// */
	// private char[][] transposeGridLeftDiagonalPart1() {
	//
	// char[][] leftDiagonalGrid = createDiagonalArray();
	//
	// List<Integer> linesToLookOver = Arrays.asList(4, 5, 5);
	// List<Integer> rowToLookOver = Arrays.asList(0, 1, 3);
	// List<Integer> colBeginIndex = Arrays.asList(0, 0, 1);
	//
	// for (int i = 0; i < linesToLookOver.size(); i++) {
	// int lineIndex = linesToLookOver.get(i);
	// int colIndex = rowToLookOver.get(i);
	//
	// int colCpt = colBeginIndex.get(i);
	//
	// for (int j = lineIndex; j >= 0 && colIndex < Constants.NB_OF_COLUMNS;
	// j--) {
	// leftDiagonalGrid[i][colCpt] = game.getCell(j, colIndex);
	// colIndex++;
	// colCpt++;
	// }
	// }
	//
	// return leftDiagonalGrid;
	// }
	//
	// /**
	// * @return
	// */
	// private char[][] transposeGridLeftDiagonalPart2() {
	//
	// char[][] leftDiagonalGrid = createDiagonalArray();
	//
	// List<Integer> linesToLookOver = Arrays.asList(3, 5, 5);
	// List<Integer> rowToLookOver = Arrays.asList(0, 0, 2);
	// List<Integer> colBeginIndex = Arrays.asList(1, 0, 1);
	//
	// for (int i = 0; i < linesToLookOver.size(); i++) {
	// int lineIndex = linesToLookOver.get(i);
	// int colIndex = rowToLookOver.get(i);
	//
	// int colCpt = colBeginIndex.get(i);
	//
	// for (int j = lineIndex; j >= 0 && colIndex < Constants.NB_OF_COLUMNS;
	// j--) {
	// leftDiagonalGrid[i][colCpt] = game.getCell(j, colIndex);
	// colIndex++;
	// colCpt++;
	// }
	// }
	//
	// return leftDiagonalGrid;
	// }
	//
	// /**
	// * @return
	// */
	// private char[][] createDiagonalArray() {
	// char[][] diagonalGrid = new
	// char[Constants.DIAG_NB_OF_LINES][Constants.DIAG_NB_OF_COLUMNS];
	// for (char[] row : diagonalGrid) {
	// Arrays.fill(row, Player.DEFAULT.getValue());
	// }
	// return diagonalGrid;
	// }

}