package fr.ippon.contest.puissance4.impl;

import fr.ippon.contest.puissance4.Analysis;
import fr.ippon.contest.puissance4.model.Constants;
import fr.ippon.contest.puissance4.model.Game;
import fr.ippon.contest.puissance4.model.GameScore;

public class VerticalAnalysis implements Analysis {

	@Override
	public boolean analyzeCells(Game game, int... cell) {
		GameScore gameScore = new GameScore();
		boolean keepLookingForWinner = true;

		for (int j = Constants.NB_OF_LINES - 1; j >= Constants.FIRST_INDEX
				&& keepLookingForWinner; j--) {

			gameScore = updateGameScore(j, cell[0], gameScore, game);

			keepLookingForWinner = game.getCell(j, cell[0]) != Constants.DEFAULT_PLAYER
					&& gameScore.needToContinue(j, game);
		}

		return keepLookingForWinner;
	}

	@Override
	public void analyzeDimensions(Game game) {
		boolean winnerFound = false;
		for (int i = Constants.FIRST_INDEX; i < Constants.NB_OF_COLUMNS
				&& !winnerFound; i++) {

			winnerFound = analyzeCells(game, i);
		}
	}
}
