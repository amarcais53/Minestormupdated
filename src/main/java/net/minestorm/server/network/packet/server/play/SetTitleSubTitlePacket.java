package net.minestorm.server.network.packet.server.play;

import net.kyori.adventure.text.Component;
import net.minestorm.server.network.packet.server.ServerPacket;
import net.minestorm.server.network.packet.server.ServerPacketIdentifier;
import net.minestorm.server.utils.binary.BinaryReader;
import net.minestorm.server.utils.binary.BinaryWriter;
import org.jetbrains.annotations.NotNull;

public class SetTitleSubTitlePacket implements ServerPacket {

    public Component subtitle = Component.empty();

    public SetTitleSubTitlePacket() {
    }

    public SetTitleSubTitlePacket(Component subtitle) {
        this.subtitle = subtitle;
    }

    @Override
    public void read(@NotNull BinaryReader reader) {
        this.subtitle = reader.readComponent();
    }

    @Override
    public void write(@NotNull BinaryWriter writer) {
        writer.writeComponent(subtitle);
    }

    @Override
    public int getId() {
        return ServerPacketIdentifier.SET_TITLE_SUBTITLE;
    }
}
