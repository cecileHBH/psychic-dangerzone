package fr.ippon.contest.puissance4.impl;

import fr.ippon.contest.puissance4.Analysis;
import fr.ippon.contest.puissance4.model.Constants;
import fr.ippon.contest.puissance4.model.Game;
import fr.ippon.contest.puissance4.model.GameScore;

public class HorizontalAnalysis implements Analysis {

	@Override
	public boolean analyzeCells(Game game, int... cell) {
		GameScore gameScore = new GameScore();
		boolean keepLookingForWinner = true;

		for (int j = Constants.FIRST_INDEX; j < Constants.NB_OF_COLUMNS
				&& keepLookingForWinner; j++) {

			gameScore = updateGameScore(cell[0], j, gameScore, game);

			keepLookingForWinner = gameScore.needToContinue(
					Constants.NB_OF_COLUMNS - j, game);
		}
		return keepLookingForWinner;
	}

	@Override
	public void analyzeDimensions(Game game) {
		boolean winnerFound = false;
		for (int i = Constants.NB_OF_LINES - 1; i >= Constants.FIRST_INDEX
				&& !winnerFound; i--) {

			winnerFound = analyzeCells(game, i);
		}
	}

}
