package net.minestorm.server.network.packet.server.play;

import net.minestorm.server.network.packet.server.ServerPacket;
import net.minestorm.server.network.packet.server.ServerPacketIdentifier;
import net.minestorm.server.utils.binary.BinaryReader;
import net.minestorm.server.utils.binary.BinaryWriter;
import org.jetbrains.annotations.NotNull;

public class SetCooldownPacket implements ServerPacket {

    public int itemId;
    public int cooldownTicks;

    public SetCooldownPacket() {}

    @Override
    public void write(@NotNull BinaryWriter writer) {
        writer.writeVarInt(itemId);
        writer.writeVarInt(cooldownTicks);
    }

    @Override
    public void read(@NotNull BinaryReader reader) {
        itemId = reader.readVarInt();
        cooldownTicks = reader.readVarInt();
    }

    @Override
    public int getId() {
        return ServerPacketIdentifier.SET_COOLDOWN;
    }
}
