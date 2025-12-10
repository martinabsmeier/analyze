package de.ma.analyze.parser.common.listener;

import lombok.extern.log4j.Log4j2;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import static java.util.Objects.nonNull;

/**
 * {@code SyntaxErrorListener} is responsible for logging syntax errors.
 *
 * @author Martin Absmeier
 */
@Log4j2
public class SyntaxErrorListener extends BaseErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e)
    {
        log.error("Syntax error {} in line {} at position {}.", msg, line, charPositionInLine);
        if (nonNull(e)) {
            log.error(e.getMessage(), e);
        }
    }
}