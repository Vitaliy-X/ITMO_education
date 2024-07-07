package expression.parser;

import java.util.Objects;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public abstract class BaseParser {
    private static final char END = '\0';
    protected CharSource source;
    protected char ch;

    protected void take() {
        this.ch = source.hasNext() ? source.next() : END;
    }

    protected boolean test(final char expected) {
        return ch == expected;
    }

    protected boolean take(final char expected) {
        skipWhitespace();
        if (test(expected)) {
            take();
            return true;
        }
        return false;
    }

    protected boolean eof() {
        return take(END);
    }

    protected IllegalArgumentException error(final String message) {
        return source.error(message);
    }

    protected boolean isDigit() {
        return '0' <= ch && ch <= '9';
    }

    protected void skipWhitespace() {
        while (Character.isWhitespace(ch)) {
            take();
        }
    }
}
