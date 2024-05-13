/*
 * Copyright 2024 Martin Absmeier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.am.analyze.cli;

import lombok.Builder;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

/**
 * @author Martin Absmeier
 */
public class CliTerminal {

    private final Terminal terminal;

    @Builder
    public CliTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    /**
     * Print message to the console in the default color.
     *
     * @param message message to print
     */
    public void write(String message) {
        terminal.writer().println(message);
        terminal.flush();
    }

    /**
     * Print message to the console in the success color.
     *
     * @param message message to print
     */
    public void writeSuccess(String message) {
        terminal.writer().println(getColored(message, AttributedStyle.GREEN));
        terminal.flush();
    }

    /**
     * Print message to the console in the info color.
     *
     * @param message message to print
     */
    public void writeInfo(String message) {
        terminal.writer().println(getColored(message, AttributedStyle.CYAN));
        terminal.flush();
    }

    /**
     * Print message to the console in the warning color.
     *
     * @param message message to print
     */
    public void writeWarning(String message) {
        terminal.writer().println(getColored(message, AttributedStyle.YELLOW));
        terminal.flush();
    }

    /**
     * Print message to the console in the error color.
     *
     * @param message message to print
     */
    public void writeError(String message) {
        terminal.writer().println(getColored(message, AttributedStyle.RED));
        terminal.flush();
    }

    // #################################################################################################################

    private String getColored(String message, int color) {
        return (new AttributedStringBuilder()).append(message, AttributedStyle.DEFAULT.foreground(color)).toAnsi();
    }
}
