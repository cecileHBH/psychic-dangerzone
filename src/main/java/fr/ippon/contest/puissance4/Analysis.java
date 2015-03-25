package fr.ippon.contest.puissance4;

import fr.ippon.contest.puissance4.model.Game;
import fr.ippon.contest.puissance4.model.GameScore;

public interface Analysis {

	boolean analyzeCells(Game game, int... cell);

	void analyzeDimensions(Game game);

	/**
	 * @param rowIndex
	 * @param colIndex
	 * @param gameScore
	 * @param game
	 * @return
	 */
	default public GameScore updateGameScore(int rowIndex, int colIndex,
			GameScore gameScore, Game game) {

		char cellContent = game.getCell(rowIndex, colIndex);

		if (cellContent == game.getNextPlayer()) {
			gameScore.addPointToPlayer();

		} else {
			gameScore.initPlayerScore();
		}

		return gameScore;
	}

}
