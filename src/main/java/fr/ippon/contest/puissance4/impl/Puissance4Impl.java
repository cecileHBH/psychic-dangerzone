package fr.ippon.contest.puissance4.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ippon.contest.puissance4.Puissance4;
import fr.ippon.contest.puissance4.model.Constants;
import fr.ippon.contest.puissance4.model.Game;
import fr.ippon.contest.puissance4.model.Player;

public class Puissance4Impl implements Puissance4 {

    private static final Logger LOG = Logger.getLogger(Puissance4Impl.class);

    private Game game = new Game();

    private int cptIter = 0;

    @Override
    public void nouveauJeu() {

        game.initGame();

    }

    @Override
    public void chargerJeu(char[][] grid, char tour) {

        game.initGame(grid, tour);
        updateGameStatus();
    }

    /**
     * Update game status with the actual game status
     */
    private void updateGameStatus() {
        this.cptIter = 0;
        this.game.setEtat(gameStatus());

        LOG.debug("Compute game status in " + cptIter + " iterations.");

    }

    /**
     * Compute game status and returns it
     * 
     * @return
     */
    private EtatJeu gameStatus() {

        if (game.getEtat() != null && !game.getEtat().equals(EtatJeu.EN_COURS)) {
            return game.getEtat();
        }

        EtatJeu simpleStatus = simpleGameStatus(game.getGrid(), Constants.NB_OF_LINES, Constants.NB_OF_COLUMNS);
        if (existsWinner(simpleStatus)) {
            return simpleStatus;
        }

        EtatJeu diagonalStatus = diagonalStatus();
        if (existsWinner(diagonalStatus)) {
            return diagonalStatus;
        }

        if (isGameOver()) {
            return EtatJeu.MATCH_NUL;
        }
        return EtatJeu.EN_COURS;
    }

    /**
     * Return true if there is a winner for the game
     * 
     * @param simpleStatus
     * @return
     */
    private boolean existsWinner(EtatJeu simpleStatus) {
        return simpleStatus.equals(EtatJeu.JAUNE_GAGNE) || simpleStatus.equals(EtatJeu.ROUGE_GAGNE);
    }

    /**
     * Determine the game status based on the horizontal and vertical computation
     * 
     * @param grid
     * @param nbOfLines
     * @param nbOfColumns
     * @return
     */
    private EtatJeu simpleGameStatus(char[][] grid, int nbOfLines, int nbOfColumns) {

        EtatJeu horizontalStatus = horizontalStatus(grid, nbOfLines, nbOfColumns);
        if (existsWinner(horizontalStatus)) {
            return horizontalStatus;
        }
        EtatJeu verticalStatus = verticalStatus(grid, nbOfLines, nbOfColumns);
        if (existsWinner(verticalStatus)) {
            return verticalStatus;
        }
        return EtatJeu.EN_COURS;
    }

    /**
     * Determines if the game is over
     * 
     * @return
     */
    private boolean isGameOver() {
        int full = 0;
        for (int i = 0; i < Constants.NB_OF_COLUMNS; i++) {
            try {
                findEmptyLineIndex(i);
            } catch (IllegalStateException ise) {
                full++;
            }
        }

        return full == Constants.NB_OF_COLUMNS;
    }

    /**
     * @return
     */
    private EtatJeu diagonalStatus() {

        char[][] leftDiagonalGridPart1 = transposeGridLeftDiagonalPart1();
        EtatJeu leftDiagonalGridPart1Status = simpleGameStatus(leftDiagonalGridPart1, Constants.DIAG_NB_OF_LINES,
                Constants.DIAG_NB_OF_COLUMNS);
        if (existsWinner(leftDiagonalGridPart1Status)) {
            return leftDiagonalGridPart1Status;
        }

        char[][] leftDiagonalGridPart2 = transposeGridLeftDiagonalPart2();
        EtatJeu leftDiagonalGridPart2Status = simpleGameStatus(leftDiagonalGridPart2, Constants.DIAG_NB_OF_LINES,
                Constants.DIAG_NB_OF_COLUMNS);
        if (existsWinner(leftDiagonalGridPart2Status)) {
            return leftDiagonalGridPart2Status;
        }

        char[][] rightDiagonalGridPart1 = transposeGridRightDiagonalPart1();
        EtatJeu rightDiagonalGridPart1Status = simpleGameStatus(rightDiagonalGridPart1, Constants.DIAG_NB_OF_LINES,
                Constants.DIAG_NB_OF_COLUMNS);
        if (existsWinner(rightDiagonalGridPart1Status)) {
            return rightDiagonalGridPart1Status;
        }

        char[][] rightDiagonalGridPart2 = transposeGridRightDiagonalPart2();
        EtatJeu rightDiagonalGridPart2Status = simpleGameStatus(rightDiagonalGridPart2, Constants.DIAG_NB_OF_LINES,
                Constants.DIAG_NB_OF_COLUMNS);
        if (existsWinner(rightDiagonalGridPart2Status)) {
            return rightDiagonalGridPart2Status;
        }

        return EtatJeu.EN_COURS;
    }

    /**
     * @return
     */
    private char[][] transposeGridRightDiagonalPart1() {
        char[][] rightDiagonalGrid = createDiagonalArray();

        List<Integer> linesToLookOver = Arrays.asList(1, 0, 0);
        List<Integer> rowToLookOver = Arrays.asList(0, 1, 3);
        List<Integer> colBeginIndex = Arrays.asList(1, 0, 1);

        for (int i = 0; i < linesToLookOver.size(); i++) {
            int lineIndex = linesToLookOver.get(i);
            int colIndex = rowToLookOver.get(i);

            int colCpt = colBeginIndex.get(i);

            for (int j = lineIndex; j < Constants.NB_OF_LINES && colIndex < Constants.NB_OF_COLUMNS; j++) {
                rightDiagonalGrid[i][colCpt] = game.getCell(j, colIndex);
                colIndex++;
                colCpt++;
            }
        }

        return rightDiagonalGrid;
    }

    /**
     * @return
     */
    private char[][] transposeGridRightDiagonalPart2() {
        char[][] rightDiagonalGrid = createDiagonalArray();

        List<Integer> linesToLookOver = Arrays.asList(0, 0, 2);
        List<Integer> rowToLookOver = Arrays.asList(2, 0, 0);
        List<Integer> colBeginIndex = Arrays.asList(0, 0, 1);

        for (int i = 0; i < linesToLookOver.size(); i++) {
            int lineIndex = linesToLookOver.get(i);
            int colIndex = rowToLookOver.get(i);

            int colCpt = colBeginIndex.get(i);

            for (int j = lineIndex; j < Constants.NB_OF_LINES && colIndex < Constants.NB_OF_COLUMNS; j++) {
                rightDiagonalGrid[i][colCpt] = game.getCell(j, colIndex);
                colIndex++;
                colCpt++;
            }
        }

        return rightDiagonalGrid;
    }

    /**
     * @return
     */
    private char[][] transposeGridLeftDiagonalPart1() {

        char[][] leftDiagonalGrid = createDiagonalArray();

        List<Integer> linesToLookOver = Arrays.asList(4, 5, 5);
        List<Integer> rowToLookOver = Arrays.asList(0, 1, 3);
        List<Integer> colBeginIndex = Arrays.asList(0, 0, 1);

        for (int i = 0; i < linesToLookOver.size(); i++) {
            int lineIndex = linesToLookOver.get(i);
            int colIndex = rowToLookOver.get(i);

            int colCpt = colBeginIndex.get(i);

            for (int j = lineIndex; j >= 0 && colIndex < Constants.NB_OF_COLUMNS; j--) {
                leftDiagonalGrid[i][colCpt] = game.getCell(j, colIndex);
                colIndex++;
                colCpt++;
            }
        }

        return leftDiagonalGrid;
    }

    /**
     * @return
     */
    private char[][] transposeGridLeftDiagonalPart2() {

        char[][] leftDiagonalGrid = createDiagonalArray();

        List<Integer> linesToLookOver = Arrays.asList(3, 5, 5);
        List<Integer> rowToLookOver = Arrays.asList(0, 0, 2);
        List<Integer> colBeginIndex = Arrays.asList(1, 0, 1);

        for (int i = 0; i < linesToLookOver.size(); i++) {
            int lineIndex = linesToLookOver.get(i);
            int colIndex = rowToLookOver.get(i);

            int colCpt = colBeginIndex.get(i);

            for (int j = lineIndex; j >= 0 && colIndex < Constants.NB_OF_COLUMNS; j--) {
                leftDiagonalGrid[i][colCpt] = game.getCell(j, colIndex);
                colIndex++;
                colCpt++;
            }
        }

        return leftDiagonalGrid;
    }

    /**
     * @return
     */
    private char[][] createDiagonalArray() {
        char[][] diagonalGrid = new char[Constants.DIAG_NB_OF_LINES][Constants.DIAG_NB_OF_COLUMNS];
        for (char[] row : diagonalGrid) {
            Arrays.fill(row, Player.DEFAULT.getValue());
        }
        return diagonalGrid;
    }

    /**
     * @param grid
     * @param nbLines
     * @param nbColumns
     * @return
     */
    private EtatJeu horizontalStatus(char[][] grid, int nbLines, int nbColumns) {
        int redCpt = 0;
        int yellowCpt = 0;
        Player precedent;

        int cptIter = 0;

        for (int i = nbLines - 1; i >= 0; i--) {
            redCpt = 0;
            yellowCpt = 0;
            precedent = Player.DEFAULT;

            for (int j = 0; j < nbColumns; j++) {
                if (grid[i][j] == Player.J.getValue()) {
                    yellowCpt = computePlayerCpt(grid, yellowCpt, precedent, i, j);

                } else if (grid[i][j] == Player.R.getValue()) {
                    redCpt = computePlayerCpt(grid, redCpt, precedent, i, j);
                }
                precedent = Player.findJoueurByValue(grid[i][j]);
                cptIter++;

                if (!needToContinue(nbColumns - j, yellowCpt, redCpt)) {
                    break;
                }
            }

            if (yellowCpt >= Constants.MINIMUM_NB_TOKEN) {
                return EtatJeu.JAUNE_GAGNE;
            }

            if (redCpt >= Constants.MINIMUM_NB_TOKEN) {
                return EtatJeu.ROUGE_GAGNE;
            }
        }

        LOG.debug("Compute horizontalStatus in " + cptIter + " iteration.");
        this.cptIter += cptIter;
        return EtatJeu.EN_COURS;
    }

    /**
     * @param grid
     * @param nbLines
     * @param nbColumns
     * @return
     */
    private EtatJeu verticalStatus(char[][] grid, int nbLines, int nbColumns) {
        int redCpt = 0;
        int yellowCpt = 0;
        Player precedent;
        int cptIter = 0;

        for (int i = 0; i < nbColumns; i++) {
            redCpt = 0;
            yellowCpt = 0;
            precedent = Player.DEFAULT;

            for (int j = nbLines - 1; j >= 0; j--) {
                if (grid[j][i] == Player.J.getValue()) {
                    yellowCpt = computePlayerCpt(grid, yellowCpt, precedent, j, i);
                } else if (grid[j][i] == Player.R.getValue()) {
                    redCpt = computePlayerCpt(grid, redCpt, precedent, j, i);
                }
                precedent = Player.findJoueurByValue(grid[j][i]);
                cptIter++;

                if (precedent.equals(Player.DEFAULT) || !needToContinue(j, yellowCpt, redCpt)) {
                    break;
                }
            }

            if (yellowCpt >= Constants.MINIMUM_NB_TOKEN) {
                return EtatJeu.JAUNE_GAGNE;
            }

            if (redCpt >= Constants.MINIMUM_NB_TOKEN) {
                return EtatJeu.ROUGE_GAGNE;
            }
        }

        LOG.debug("Compute verticalStatus in " + cptIter + " iteration.");
        this.cptIter += cptIter;
        return EtatJeu.EN_COURS;
    }

    /**
     * Determines if we need to continue to compute the result of this iteration.
     * 
     * @param iter
     * @param yellowCpt
     * @param redCpt
     * @return Return false if there is a winner or no possibility of a winner, true otherwise
     */
    private boolean needToContinue(int iter, int yellowCpt, int redCpt) {

        if (yellowCpt >= Constants.MINIMUM_NB_TOKEN) {
            return false;
        }

        if (redCpt >= Constants.MINIMUM_NB_TOKEN) {
            return false;
        }

        if (iter + yellowCpt < Constants.MINIMUM_NB_TOKEN && iter + redCpt < Constants.MINIMUM_NB_TOKEN) {
            return false;
        }
        return true;
    }

    private int computePlayerCpt(char[][] grid, int yellowCpt, Player precedent, int i, int j) {
        if (precedent.getValue() == grid[i][j]) {
            yellowCpt++;
        } else {
            yellowCpt = 1;
        }
        return yellowCpt;
    }

    @Override
    public EtatJeu getEtatJeu() {
        return game.getEtat();
    }

    @Override
    public char getTour() {
        return game.getPlayer().getValue();
    }

    @Override
    public char getOccupant(int ligne, int colonne) {
        return game.getCell(ligne, colonne);
    }

    @Override
    public void jouer(int colonne) {

        if (colonne < 0 || colonne >= Constants.NB_OF_COLUMNS) {
            throw new IllegalArgumentException(String.format("La colonne %s n'est pas valide", colonne));
        }
        int lineIndex = findEmptyLineIndex(colonne);

        game.setCell(lineIndex, colonne, game.getPlayer().getValue());
        setNextPlayer();
        updateGameStatus();
    }

    private int findEmptyLineIndex(int colonne) {
        for (int i = Constants.NB_OF_LINES - 1; i >= 0; i--) {
            if (game.getCell(i, colonne) == Player.DEFAULT.getValue()) {
                return i;
            }
        }

        throw new IllegalStateException(String.format("La colonne %s est pleine.", colonne));
    }

    private void setNextPlayer() {
        switch (game.getPlayer()) {
        case J:
            game.setPlayer(Player.R);
            break;
        case R:
            game.setPlayer(Player.J);
            break;
        default:
            break;
        }
    }

}
