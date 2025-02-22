package demo.commands;

import net.kyori.adventure.text.Component;
import net.minestorm.server.command.CommandSender;
import net.minestorm.server.command.builder.Command;
import net.minestorm.server.command.builder.CommandContext;
import net.minestorm.server.command.builder.arguments.ArgumentType;

public class TestCommand extends Command {

    public TestCommand() {
        super("testcmd");
        setDefaultExecutor(this::usage);

        var block = ArgumentType.BlockState("block");
        block.setCallback((sender, exception) -> exception.printStackTrace());

        addSyntax((sender, context) -> System.out.println("executed"), block);
    }

    private void usage(CommandSender sender, CommandContext context) {
        sender.sendMessage(Component.text("Incorrect usage"));
    }

}
