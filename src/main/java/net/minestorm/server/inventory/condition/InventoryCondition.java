package net.minestorm.server.inventory.condition;

import net.minestorm.server.entity.Player;
import net.minestorm.server.inventory.AbstractInventory;
import net.minestorm.server.inventory.Inventory;
import net.minestorm.server.inventory.PlayerInventory;
import net.minestorm.server.inventory.click.ClickType;

/**
 * Can be added to any {@link AbstractInventory}
 * using {@link Inventory#addInventoryCondition(InventoryCondition)}
 * or {@link PlayerInventory#addInventoryCondition(InventoryCondition)}
 * in order to listen to any issued clicks.
 */
@FunctionalInterface
public interface InventoryCondition {

    /**
     * Called when a {@link Player} clicks in the inventory where this {@link InventoryCondition} has been added to.
     *
     * @param player                   the player who clicked in the inventory
     * @param slot                     the slot clicked, can be -999 if the click is out of the inventory
     * @param clickType                the click type
     * @param inventoryConditionResult the result of this callback
     */
    void accept(Player player, int slot, ClickType clickType, InventoryConditionResult inventoryConditionResult);
}
