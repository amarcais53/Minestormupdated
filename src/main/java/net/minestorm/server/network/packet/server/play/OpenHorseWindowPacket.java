package net.minestorm.server.network.packet.server.play;

import net.minestorm.server.network.packet.server.ServerPacket;
import net.minestorm.server.network.packet.server.ServerPacketIdentifier;
import net.minestorm.server.utils.binary.BinaryReader;
import net.minestorm.server.utils.binary.BinaryWriter;
import org.jetbrains.annotations.NotNull;

public class OpenHorseWindowPacket implements ServerPacket {

    public byte windowId;
    public int slotCount;
    public int entityId;

    @Override
    public void write(@NotNull BinaryWriter writer) {
        writer.writeByte(windowId);
        writer.writeVarInt(slotCount);
        writer.writeInt(entityId);
    }

    @Override
    public void read(@NotNull BinaryReader reader) {
        windowId = reader.readByte();
        slotCount = reader.readVarInt();
        entityId = reader.readInt();
    }

    @Override
    public int getId() {
        return ServerPacketIdentifier.OPEN_HORSE_WINDOW;
    }
}
