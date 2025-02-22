package net.minestorm.server.data.type;

import net.kyori.adventure.text.Component;
import net.minestorm.server.data.DataType;
import net.minestorm.server.inventory.Inventory;
import net.minestorm.server.inventory.InventoryType;
import net.minestorm.server.utils.binary.BinaryReader;
import net.minestorm.server.utils.binary.BinaryWriter;
import org.jetbrains.annotations.NotNull;

public class InventoryData extends DataType<Inventory> {

    @Override
    public void encode(@NotNull BinaryWriter writer, @NotNull Inventory value) {
        final InventoryType inventoryType = value.getInventoryType();
        final int size = inventoryType.getSize();

        // Inventory title & type
        writer.writeComponent(value.getTitle());
        writer.writeSizedString(inventoryType.name());

        // Write all item stacks
        for (int i = 0; i < size; i++) {
            writer.writeItemStack(value.getItemStack(i));
        }
    }

    @NotNull
    @Override
    public Inventory decode(@NotNull BinaryReader reader) {
        final Component title = reader.readComponent();
        final InventoryType inventoryType = InventoryType.valueOf(reader.readSizedString());
        final int size = inventoryType.getSize();

        Inventory inventory = new Inventory(inventoryType, title);

        // Read all item stacks
        for (int i = 0; i < size; i++) {
            inventory.setItemStack(i, reader.readItemStack());
        }

        return inventory;
    }
}
