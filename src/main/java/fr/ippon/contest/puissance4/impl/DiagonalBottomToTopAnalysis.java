package fr.ippon.contest.puissance4.impl;

import fr.ippon.contest.puissance4.Analysis;
import fr.ippon.contest.puissance4.model.Constants;
import fr.ippon.contest.puissance4.model.Game;
import fr.ippon.contest.puissance4.model.GameScore;

public class DiagonalBottomToTopAnalysis implements Analysis {

	@Override
	public boolean analyzeCells(Game game, int... cell) {

		int line = cell[0];
		int column = cell[1];
		int firstRowIndex = column + line >= Constants.NB_OF_LINES ? Constants.NB_OF_LINES - 1
				: column + line;
		int firstColIndex = column - (firstRowIndex - line);

		if (firstRowIndex > Constants.DIAG_BOTTOM_LINE_INDEX_LIMIT
				&& firstColIndex < Constants.DIAG_COLUMN_INDEX_LIMIT) {
			gameStatusOfDiagBottomToTop(game, firstColIndex, firstRowIndex);
		}

		return game.hasWinner();

	}

	@Override
	public void analyzeDimensions(Game game) {
		boolean winnerFound = false;

		for (int i = Constants.FIRST_INDEX; i < Constants.DIAG_TOP_LINE_INDEX_LIMIT
				&& !winnerFound; i++) {

			winnerFound = analyzeCells(game, i
					+ Constants.DIAG_TOP_LINE_INDEX_LIMIT,
					Constants.FIRST_INDEX);

		}

		for (int i = 1; i < Constants.DIAG_COLUMN_INDEX_LIMIT && !winnerFound; i++) {

			winnerFound = analyzeCells(game, 5, i);

		}
	}

	private void gameStatusOfDiagBottomToTop(Game game, int colIndex,
			int firstRowIndex) {

		GameScore gameScore = new GameScore();
		boolean keepLookingForWinner = true;

		for (int j = firstRowIndex; j >= Constants.FIRST_INDEX
				&& colIndex < Constants.NB_OF_COLUMNS && keepLookingForWinner; j--, colIndex++) {

			gameScore = updateGameScore(j, colIndex, gameScore, game);

			keepLookingForWinner = gameScore.needToContinue(j, game);
		}
	}
}
