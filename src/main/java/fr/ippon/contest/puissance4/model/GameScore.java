package fr.ippon.contest.puissance4.model;

import java.util.HashMap;
import java.util.Map;

import fr.ippon.contest.puissance4.Puissance4.EtatJeu;

public class GameScore {

	private Map<Player, Integer> playersScore = new HashMap<Player, Integer>();

	private Player precedent = Player.DEFAULT;

	public GameScore() {
		super();
		playersScore.put(Player.J, 0);
		playersScore.put(Player.R, 0);
	}

	public GameScore(int redScore, int yellowScore) {
		super();
		playersScore.put(Player.J, yellowScore);
		playersScore.put(Player.R, redScore);
	}

	public GameScore(int redScore, int yellowScore, Player precedent) {
		super();
		playersScore.put(Player.J, yellowScore);
		playersScore.put(Player.R, redScore);
		this.precedent = precedent;
	}

	public boolean needToContinue(int iterationsLeft, Game game) {

		if (this.getYellowScore() >= Constants.MINIMUM_NB_TOKEN) {
			game.setEtat(EtatJeu.JAUNE_GAGNE);
			return false;
		}

		if (this.getRedScore() >= Constants.MINIMUM_NB_TOKEN) {
			game.setEtat(EtatJeu.ROUGE_GAGNE);
			return false;
		}

		if (iterationsLeft + this.getYellowScore() < Constants.MINIMUM_NB_TOKEN
				&& iterationsLeft + this.getRedScore() < Constants.MINIMUM_NB_TOKEN) {
			return false;
		}
		return true;
	}

	public void addPointToPlayer(Player cellPlayer) {
		playersScore.put(cellPlayer, playersScore.get(cellPlayer) == null ? 0
				: playersScore.get(cellPlayer) + 1);
	}

	public void initPlayerScore(Player cellPlayer) {
		playersScore.put(cellPlayer, 1);
	}

	public int getRedScore() {
		return playersScore.get(Player.R);
	}

	public int getYellowScore() {
		return playersScore.get(Player.J);
	}

	public Player getPrecedent() {
		return precedent;
	}

	public void setPrecedent(Player precedent) {
		this.precedent = precedent;
	}

	public void setRedScore(int redScore) {
		playersScore.put(Player.R, redScore);
	}

	public void setYellowScore(int yellowScore) {
		playersScore.put(Player.J, yellowScore);
	}

}
