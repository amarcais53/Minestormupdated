package net.minestorm.server.event.player;

import net.minestorm.server.coordinate.Pos;
import net.minestorm.server.entity.Player;
import net.minestorm.server.event.trait.CancellableEvent;
import net.minestorm.server.event.trait.EntityInstanceEvent;
import net.minestorm.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player is modifying his position.
 */
public class PlayerMoveEvent implements PlayerEvent, EntityInstanceEvent, CancellableEvent {

    private final Player player;
    private Pos newPosition;

    private boolean cancelled;

    public PlayerMoveEvent(@NotNull Player player, @NotNull Pos newPosition) {
        this.player = player;
        this.newPosition = newPosition;
    }

    /**
     * Gets the target position.
     *
     * @return the new position
     */
    public @NotNull Pos getNewPosition() {
        return newPosition;
    }

    /**
     * Changes the target position.
     *
     * @param newPosition the new target position
     */
    public void setNewPosition(@NotNull Pos newPosition) {
        this.newPosition = newPosition;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }
}
