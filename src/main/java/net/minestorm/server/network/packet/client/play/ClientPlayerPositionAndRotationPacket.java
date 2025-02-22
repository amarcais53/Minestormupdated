package net.minestorm.server.network.packet.client.play;

import net.minestorm.server.network.packet.client.ClientPlayPacket;
import net.minestorm.server.utils.binary.BinaryReader;
import net.minestorm.server.utils.binary.BinaryWriter;
import org.jetbrains.annotations.NotNull;

public class ClientPlayerPositionAndRotationPacket extends ClientPlayPacket {

    public double x, y, z;
    public float yaw, pitch;
    public boolean onGround;

    @Override
    public void read(@NotNull BinaryReader reader) {
        this.x = reader.readDouble();
        this.y = reader.readDouble();
        this.z = reader.readDouble();

        this.yaw = reader.readFloat();
        this.pitch = reader.readFloat();

        this.onGround = reader.readBoolean();
    }

    @Override
    public void write(@NotNull BinaryWriter writer) {
        writer.writeDouble(x);
        writer.writeDouble(y);
        writer.writeDouble(z);

        writer.writeFloat(yaw);
        writer.writeFloat(pitch);

        writer.writeBoolean(onGround);
    }
}
