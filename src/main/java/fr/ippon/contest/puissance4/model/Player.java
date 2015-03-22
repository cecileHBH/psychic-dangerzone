package fr.ippon.contest.puissance4.model;

public enum Player {

	R(Constants.RED_PLAYER) {
		@Override
		Player switchPlayer() {
			return J;
		}
	},
	J(Constants.YELLOW_PLAYER) {
		@Override
		Player switchPlayer() {
			return R;
		}
	};

	private char value;

	private Player(char value) {
		this.value = value;
	}

	abstract Player switchPlayer();

	public static Player findJoueurByValue(char valueToFind) {
		for (Player player : values()) {
			if (player.value == valueToFind) {
				return player;
			}
		}

		return null;
	}

	public char getValue() {
		return value;
	}

}
