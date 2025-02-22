package net.minestorm.server.data.type.array;

import net.minestorm.server.data.DataType;
import net.minestorm.server.utils.binary.BinaryReader;
import net.minestorm.server.utils.binary.BinaryWriter;
import org.jetbrains.annotations.NotNull;

public class ShortArrayData extends DataType<short[]> {

    @Override
    public void encode(@NotNull BinaryWriter writer, @NotNull short[] value) {
        writer.writeVarInt(value.length);
        for (short val : value) {
            writer.writeShort(val);
        }
    }

    @NotNull
    @Override
    public short[] decode(@NotNull BinaryReader reader) {
        short[] array = new short[reader.readVarInt()];
        for (int i = 0; i < array.length; i++) {
            array[i] = reader.readShort();
        }
        return array;
    }
}
