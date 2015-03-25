package fr.ippon.contest.puissance4.impl;

import fr.ippon.contest.puissance4.Analysis;
import fr.ippon.contest.puissance4.model.Constants;
import fr.ippon.contest.puissance4.model.Game;
import fr.ippon.contest.puissance4.model.GameScore;

public class DiagonalTopToBottomAnalysis implements Analysis {

	@Override
	public boolean analyzeCells(Game game, int... cell) {

		int line = cell[0];
		int column = cell[1];
		int firstColIndex = column - line < Constants.FIRST_INDEX ? Constants.FIRST_INDEX
				: column - line;
		int firstRowIndex = line - column < Constants.FIRST_INDEX ? Constants.FIRST_INDEX
				: line - column;

		if (firstRowIndex < Constants.DIAG_TOP_LINE_INDEX_LIMIT
				&& firstColIndex < Constants.DIAG_COLUMN_INDEX_LIMIT) {
			gameStatusOfDiagTopToBottom(game, firstColIndex, firstRowIndex);
		}

		return game.hasWinner();
	}

	@Override
	public void analyzeDimensions(Game game) {
		boolean winnerFound = false;

		for (int i = Constants.FIRST_INDEX; i < Constants.DIAG_TOP_LINE_INDEX_LIMIT
				&& !winnerFound; i++) {

			winnerFound = analyzeCells(game, i, Constants.FIRST_INDEX);

		}

		for (int i = 1; i < Constants.DIAG_COLUMN_INDEX_LIMIT && !winnerFound; i++) {

			winnerFound = analyzeCells(game, Constants.FIRST_INDEX, i);

		}
	}

	private void gameStatusOfDiagTopToBottom(Game game, int colIndex,
			int rowIndex) {

		GameScore gameScore = new GameScore();
		boolean keepLookingForWinner = true;

		for (int j = colIndex; j < Constants.NB_OF_COLUMNS
				&& rowIndex < Constants.NB_OF_LINES && keepLookingForWinner; j++, rowIndex++) {

			gameScore = updateGameScore(rowIndex, j, gameScore, game);

			keepLookingForWinner = gameScore.needToContinue(
					Constants.NB_OF_COLUMNS - colIndex, game);
		}

	}
}
