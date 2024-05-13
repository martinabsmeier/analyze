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
package de.am.analyze.cli.command;

import de.am.analyze.cli.CliTerminal;
import de.am.analyze.common.AnalyzeConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import static java.io.File.separator;

/**
 * @author Martin Absmeier
 */
@Log4j2
@ShellComponent
public class ParseCommand extends BaseCommand {

    private static final String ABBREVIATION_NOT_FOUND = "Abbreviation not found !";
    private final String rootDir;

    @Autowired
    public ParseCommand(CliTerminal terminal) {
        super(terminal);
        this.rootDir = AnalyzeConstants.COMMON.USER_DIR.concat(separator);

    }

    @ShellMethod(value = "Parses all Connect to remote server")
    public void parse(String abbreviation) {
        String folder = getFolderForAbbreviation(abbreviation);
        if (ABBREVIATION_NOT_FOUND.equals(folder)) {
            logAbbreviations();
        } else {
            String path = rootDir + getFolderForAbbreviation(abbreviation);
            terminal.writeInfo("Parse folder: " + path);
        }
    }

    // #################################################################################################################
    private void logAbbreviations() {
        terminal.writeError(ABBREVIATION_NOT_FOUND);
        terminal.writeInfo("Available abbreviations:");
        terminal.writeInfo(" c -> parse constructor");
        terminal.writeInfo(" w -> parse statements/while");
        terminal.writeInfo(" d -> parse statements/doWhile");
        terminal.writeInfo(" s -> parse statements/switch");
        terminal.writeInfo(" f -> parse statements/for");
        terminal.writeInfo(" i -> parse statements/if");
        terminal.writeInfo(" b -> parse statements/break");
        terminal.writeInfo("fe -> parse statements/forEach");
    }

    private String getFolderForAbbreviation(String abbreviation) {
        return switch (abbreviation) {
            case "c" -> "constructor";
            case "w" -> "statements/while";
            case "d" -> "statements/doWhile";
            case "s" -> "statements/switch";
            case "f" -> "statements/for";
            case "i" -> "statements/if";
            case "b" -> "statements/break";
            case "fe" -> "statements/forEach";
            default -> ABBREVIATION_NOT_FOUND;
        };
    }
}