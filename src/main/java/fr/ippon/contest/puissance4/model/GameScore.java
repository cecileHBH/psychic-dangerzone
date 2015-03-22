package fr.ippon.contest.puissance4.model;

import fr.ippon.contest.puissance4.Puissance4.EtatJeu;

public class GameScore {

	private int playerScore = 0;

	public GameScore() {
		super();
		playerScore = 0;
	}

	public boolean needToContinue(int iterationsLeft, Game game) {

		if (playerScore >= Constants.MINIMUM_NB_TOKEN) {
			if (game.getNextPlayer() == Player.J.getValue()) {
				game.setEtat(EtatJeu.JAUNE_GAGNE);
			} else {
				game.setEtat(EtatJeu.ROUGE_GAGNE);
			}
			return false;
		}

		if (iterationsLeft + playerScore < Constants.MINIMUM_NB_TOKEN) {
			return false;
		}
		return true;
	}

	public void addPointToPlayer() {
		playerScore++;
	}

	public void initPlayerScore() {
		playerScore = 0;
	}

}
