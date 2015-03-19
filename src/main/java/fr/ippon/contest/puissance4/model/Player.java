package fr.ippon.contest.puissance4.model;

public enum Player {
    
    R('R'), J('J'), DEFAULT('-');

    private char value;

    private Player(char value) {
        this.value = value;
    }

    public static Player findJoueurByValue(char valueToFind) {
        for (int i = 0; i < Player.values().length; i++) {
            if (Player.values()[i].value == valueToFind) {
                return Player.values()[i];
            }
        }

        throw new IllegalArgumentException(String.format("Value to find %s did not match existing values %s", valueToFind, Player.values()));
    }

    public char getValue() {
        return value;
    }

}
