package paul.fallen.stevebot.mod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomCommand {

    private final String name;
    private List<CommandTemplate> templates = new ArrayList<>();

    public CustomCommand(String name) {
        this.name = name;
    }

    protected void registerTemplate(CommandTemplate template) {
        templates.add(template);
    }

    public void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal(name)
                .executes(this::executeCommand));
    }

    private int executeCommand(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        MinecraftServer server = source.getServer();

        HashMap<String, Object> parameters = new HashMap<>();
        boolean foundCommand = false;

        String[] args = context.getInput().split(" ");
        for (CommandTemplate template : templates) {
            parameters.clear();
            if (parseTemplate(template, source, args, parameters)) {
                template.listener.onCommand(template.templateId, parameters);
                foundCommand = true;
                break;
            }
        }

        if (!foundCommand) {
            ITextComponent errorMessage = new StringTextComponent(getUsage(source));
            throw new CommandSyntaxException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand(), errorMessage);
        }

        return 1; // Return 1 to indicate success
    }

    private boolean parseTemplate(CommandTemplate template, CommandSource source, String[] cmdArgs, Map<String, Object> parametersOut) {
        if (template.tokens.length - 1 != cmdArgs.length) {
            return false;
        }

        if (template.tokens.length == 1 && cmdArgs.length == 0) {
            return true;
        }

        for (int i = 1; i < template.tokens.length; i++) {
            final String token = template.tokens[i];
            final String arg = cmdArgs[i - 1];

            if (token.startsWith("<") && token.endsWith(">")) {
                final String varToken = token.substring(1, token.length() - 1);
                final String[] varParts = varToken.split(":");
                final String varname = varParts[0];
                final String strVartype = varParts[1];
                final CommandTemplate.TokenVarType vartype = strVartype.startsWith("{") ?
                        CommandTemplate.TokenVarType.ENUM : CommandTemplate.TokenVarType.valueOf(strVartype);

                switch (vartype) {
                    case STRING:
                        parametersOut.put(varname, arg);
                        break;
                    case COORDINATE:
                        Integer coordinate = parseBlockPos(new BlockPos(source.getPos().x, source.getPos().y, source.getPos().z), arg);
                        if (coordinate != null) {
                            parametersOut.put(varname, coordinate);
                        } else {
                            return false;
                        }
                        break;
                    case INTEGER:
                        try {
                            parametersOut.put(varname, Integer.parseInt(arg));
                        } catch (NumberFormatException e) {
                            return false;
                        }
                        break;
                    case FLOAT:
                        try {
                            parametersOut.put(varname, Float.parseFloat(arg));
                        } catch (NumberFormatException e) {
                            return false;
                        }
                        break;
                    case BOOLEAN:
                        if ("true".equalsIgnoreCase(arg)) {
                            parametersOut.put(varname, true);
                        } else if ("false".equalsIgnoreCase(arg)) {
                            parametersOut.put(varname, false);
                        } else {
                            return false;
                        }
                        break;
                    case ENUM:
                        String[] enumTokens = strVartype.substring(1, strVartype.length() - 1).split(",");
                        boolean foundEnum = false;
                        for (String et : enumTokens) {
                            if (et.trim().equalsIgnoreCase(arg)) {
                                parametersOut.put(varname, et);
                                foundEnum = true;
                            }
                        }
                        return foundEnum;
                    default:
                        throw new IllegalArgumentException("Unknown token-type: " + varToken.split(":")[1]);
                }
            } else {
                if (!token.equalsIgnoreCase(arg)) {
                    return false;
                }
            }
        }

        return true;
    }

    private Integer parseBlockPos(BlockPos origin, String input) {
        try {
            if (input.startsWith("~")) {
                double value = Double.parseDouble(input.substring(1));
                return (int) Math.round(value + origin.getX());
            } else {
                double value = Double.parseDouble(input);
                return (int) Math.round(value);
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getUsage(CommandSource source) {
        StringBuilder builder = new StringBuilder().append("\n");
        for (int i = 0; i < templates.size(); i++) {
            builder.append(templates.get(i).usage);
            if (i != templates.size() - 1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }
}