package demo.commands;

import net.kyori.adventure.text.Component;
import net.minestorm.server.MinecraftServer;
import net.minestorm.server.command.CommandSender;
import net.minestorm.server.command.builder.Command;
import net.minestorm.server.command.builder.CommandContext;
import net.minestorm.server.command.builder.arguments.ArgumentType;
import net.minestorm.server.coordinate.Pos;
import net.minestorm.server.entity.Player;
import net.minestorm.server.utils.location.RelativeVec;

public class TeleportCommand extends Command {

    public TeleportCommand() {
        super("tp");

        setDefaultExecutor((source, context) -> source.sendMessage(Component.text("Usage: /tp x y z")));

        var posArg = ArgumentType.RelativeVec3("pos");
        var playerArg = ArgumentType.Word("player");

        addSyntax(this::onPlayerTeleport, playerArg);
        addSyntax(this::onPositionTeleport, posArg);
    }

    private void onPlayerTeleport(CommandSender sender, CommandContext context) {
        final String playerName = context.get("player");
        Player pl = MinecraftServer.getConnectionManager().getPlayer(playerName);
        if (pl != null && sender.isPlayer()) {
            Player player = (Player) sender;
            player.teleport(pl.getPosition());
        }
        sender.sendMessage(Component.text("Teleported to player " + playerName));
    }

    private void onPositionTeleport(CommandSender sender, CommandContext context) {
        final Player player = sender.asPlayer();

        final RelativeVec relativeVec = context.get("pos");
        final Pos position = player.getPosition().withCoord(relativeVec.from(player));
        player.teleport(position);
        player.sendMessage(Component.text("You have been teleported to " + position));
    }
}
