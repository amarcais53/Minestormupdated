package net.minestorm.server.command.builder.arguments;

import net.minestorm.server.MinecraftServer;
import net.minestorm.server.command.builder.CommandDispatcher;
import net.minestorm.server.command.builder.CommandResult;
import net.minestorm.server.command.builder.NodeMaker;
import net.minestorm.server.command.builder.exception.ArgumentSyntaxException;
import net.minestorm.server.network.packet.server.play.DeclareCommandsPacket;
import net.minestorm.server.utils.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class ArgumentCommand extends Argument<CommandResult> {

    public static final int INVALID_COMMAND_ERROR = 1;

    private boolean onlyCorrect;
    private String shortcut = "";

    public ArgumentCommand(@NotNull String id) {
        super(id, true, true);
    }

    @NotNull
    @Override
    public CommandResult parse(@NotNull String input) throws ArgumentSyntaxException {
        final String commandString = !shortcut.isEmpty() ?
                shortcut + StringUtils.SPACE + input
                : input;
        CommandDispatcher dispatcher = MinecraftServer.getCommandManager().getDispatcher();
        CommandResult result = dispatcher.parse(commandString);

        if (onlyCorrect && result.getType() != CommandResult.Type.SUCCESS)
            throw new ArgumentSyntaxException("Invalid command", input, INVALID_COMMAND_ERROR);

        return result;
    }

    @Override
    public void processNodes(@NotNull NodeMaker nodeMaker, boolean executable) {
        final DeclareCommandsPacket.Node[] lastNodes = nodeMaker.getLatestNodes();

        if (!shortcut.isEmpty()) {
            nodeMaker.request(shortcut, (id) -> {
                for (DeclareCommandsPacket.Node node : lastNodes) {
                    node.flags |= 0x08; // Redirection mask
                    node.redirectedNode = id;
                }
            });
        } else {
            for (DeclareCommandsPacket.Node node : lastNodes) {
                node.flags |= 0x08; // Redirection mask
                node.redirectedNode = 0; // Redirect to root
            }
        }
    }

    public boolean isOnlyCorrect() {
        return onlyCorrect;
    }

    public ArgumentCommand setOnlyCorrect(boolean onlyCorrect) {
        this.onlyCorrect = onlyCorrect;
        return this;
    }

    @NotNull
    public String getShortcut() {
        return shortcut;
    }

    @ApiStatus.Experimental
    public ArgumentCommand setShortcut(@NotNull String shortcut) {
        this.shortcut = shortcut;
        return this;
    }

    @Override
    public String toString() {
        return String.format("Command<%s>", getId());
    }
}
