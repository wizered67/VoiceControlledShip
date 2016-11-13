package com.mygdx.game;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Adam on 11/12/2016.
 */
public class Command {
    enum CommandType {
        TURN("(?:turn|rotate|face)\\s+([0-3]?[0-9]?[0-9])"),
        FIRE("(?i)(?:fire|shoot|attack)\\s+([0-3]?[0-9]?[0-9])"),
        MOVE("(?i)(?:engines|engine|thruster|thrusters)\\s+(low|medium|high)"),
        SHIELD("(?i)(?:shield|shields)\\s+(up|down)"),
        SCAN("(?i)scan\\s+([0-3]?[0-9]?[0-9])"),
        REPAIR("(?i)(?:repair|fix)");

        CommandType(String regex) {
            pattern = Pattern.compile(regex);
        }
        Pattern pattern;
    }

    CommandType commandType;
    String[] data;

    public Command(CommandType type, String ... args) {
        commandType = type;
        data = args;
    }

    static Command parseCommand(String command) {
        command = command.trim();
        for (CommandType type : CommandType.values()) {
            Matcher mat = type.pattern.matcher(command);
            if (mat.matches()) {
                String[] operands = new String[mat.groupCount()];
                for (int i = 1; i <= operands.length; i += 1) {
                    operands[i - 1] = mat.group(i);
                }
                return new Command(type, operands);
            }
        }
        return null;
    }


}
