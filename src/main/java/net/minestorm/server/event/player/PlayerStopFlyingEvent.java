package net.minestorm.server.event.player;

import net.minestorm.server.entity.Player;
import net.minestorm.server.event.trait.EntityInstanceEvent;
import net.minestorm.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player stop flying.
 */
public class PlayerStopFlyingEvent implements PlayerEvent, EntityInstanceEvent {

    private final Player player;

    public PlayerStopFlyingEvent(@NotNull Player player) {
        this.player = player;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }
}
