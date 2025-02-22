package net.minestorm.server.event.player;

import net.minestorm.server.entity.Player;
import net.minestorm.server.event.trait.EntityInstanceEvent;
import net.minestorm.server.event.trait.PlayerEvent;
import net.minestorm.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player is finished eating.
 */
public class PlayerEatEvent implements PlayerEvent, EntityInstanceEvent {

    private final Player player;
    private final ItemStack foodItem;
    private final Player.Hand hand;

    public PlayerEatEvent(@NotNull Player player, @NotNull ItemStack foodItem, @NotNull Player.Hand hand) {
        this.player = player;
        this.foodItem = foodItem;
        this.hand = hand;
    }

    /**
     * Gets the food item that has been eaten.
     *
     * @return the food item
     */
    public @NotNull ItemStack getFoodItem() {
        return foodItem;
    }

    public @NotNull Player.Hand getHand() {
        return hand;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }
}
