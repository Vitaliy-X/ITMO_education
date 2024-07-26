package ru.itmo.wp.web.page;

import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class TicTacToePage {
    private static final String CELL_PARAMETER_NAME_PREFIX = "cell_";

    private TicTacToeGame getState(HttpServletRequest request) {
        TicTacToeGame state = (TicTacToeGame) request.getSession().getAttribute("state");
        if (state == null) {
            state = new TicTacToeGame();
            request.getSession().setAttribute("state", state);
        }
        return state;
    }

    private void action(HttpServletRequest request, Map<String, Object> view) {
        view.put("state", getState(request));
    }

    private void onMove(HttpServletRequest request, Map<String, Object> view) {
        TicTacToeGame state = getState(request);
        Map.Entry<String, String[]> data = request.getParameterMap().entrySet().stream()
                .filter(e -> e.getKey().startsWith(CELL_PARAMETER_NAME_PREFIX))
                .findFirst()
                .orElse(null);
        if (data != null) {
            String key = data.getKey();
            int row = key.charAt(CELL_PARAMETER_NAME_PREFIX.length()) - '0';
            int col = key.charAt(CELL_PARAMETER_NAME_PREFIX.length() + 1) - '0';
            state.makeMove(row, col);
        }
        view.put("state", state);
        throw new RedirectException("/ticTacToe");
    }

    private void newGame(HttpServletRequest request, Map<String, Object> view) {
        TicTacToeGame state = (TicTacToeGame) request.getSession().getAttribute("state");
        if (state == null) {
            return;
        }
        state.clear();
        view.put("state", state);
    }
}