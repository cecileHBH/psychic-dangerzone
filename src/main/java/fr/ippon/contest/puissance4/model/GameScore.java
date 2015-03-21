package fr.ippon.contest.puissance4.model;

public class GameScore {

    private int redScore = 0;
    private int yellowScore = 0;

    public GameScore() {
        super();
    }

    public GameScore(int redScore, int yellowScore) {
        super();
        this.redScore = redScore;
        this.yellowScore = yellowScore;
    }

    public int getRedScore() {
        return redScore;
    }

    public int getYellowScore() {
        return yellowScore;
    }

}
