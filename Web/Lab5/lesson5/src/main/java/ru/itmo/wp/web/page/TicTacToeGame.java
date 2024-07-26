package ru.itmo.wp.web.page;

public class TicTacToeGame {

    public enum Phase {
        RUNNING,
        WON_X,
        WON_O,
        DRAW
    }

    private final int DEFAULT_SIZE = 3;

    private boolean crossesMove;
    private Phase phase;
    private final Character[][] cells;
    private int moves;

    public TicTacToeGame() {
        cells = new Character[DEFAULT_SIZE][DEFAULT_SIZE];
        clear();
    }

    public int getSize() {
        return DEFAULT_SIZE;
    }

    public boolean getCrossesMove() {
        return crossesMove;
    }

    public Phase getPhase() {
        return phase;
    }

    public Character[][] getCells() {
        return cells.clone();
    }

    private boolean isInRange(int index) {
        return 0 <= index && index < DEFAULT_SIZE;
    }

    private void onMoveEnd(int row, int col) {
        if (checkWin(row, col)) {
            phase = (crossesMove ? Phase.WON_X : Phase.WON_O);
        } else if (moves == DEFAULT_SIZE * DEFAULT_SIZE) {
            phase = Phase.DRAW;
        }
    }

    private boolean checkWin(int row, int col) {
        char c = getMoveChar();
        int inMainDiag = countDirection(row, col, c, -1, -1)
                + countDirection(row, col, c, +1, +1) - 1;
        int inSideDiag = countDirection(row, col, c, -1, +1)
                + countDirection(row, col, c, +1, -1) - 1;
        int inRow = countDirection(row, col, c, 0, -1)
                + countDirection(row, col, c, 0, +1) - 1;
        int inCol = countDirection(row, col, c, -1, 0)
                + countDirection(row, col, c, +1, 0) - 1;
        return inMainDiag == getSize() || inSideDiag == getSize() || inRow == getSize() || inCol == getSize();
    }

    private char getMoveChar() {
        return crossesMove ? 'X' : 'O';
    }

    private int countDirection(int row, int col, char turn, final int increaseRow, final int increaseColumn) {
        int result = 0;

        while (isInRange(row) && isInRange(col) && cells[row][col] != null && turn == cells[row][col]) {
            row += increaseRow;
            col += increaseColumn;
            result++;
        }

        return result;
    }

    public void makeMove(int row, int col) {
        if (!(isInRange(row) && isInRange(col)) || phase != Phase.RUNNING || cells[row][col] != null) {
            return;
        }
        cells[row][col] = getMoveChar();
        moves++;
        onMoveEnd(row, col);
        crossesMove = !crossesMove;
    }

    public void clear() {
        for (int i = 0; i < DEFAULT_SIZE; ++i) {
            for (int j = 0; j < DEFAULT_SIZE; ++j) {
                cells[i][j] = null;
            }
        }
        phase = Phase.RUNNING;
        crossesMove = true;
        moves = 0;
    }
}