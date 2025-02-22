package net.minestorm.server.entity;

import net.kyori.adventure.text.Component;
import net.minestorm.server.MinecraftServer;
import net.minestorm.server.coordinate.Point;
import net.minestorm.server.coordinate.Vec;
import net.minestorm.server.item.ItemStack;
import net.minestorm.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestorm.server.utils.Direction;
import net.minestorm.server.utils.binary.BinaryReader;
import net.minestorm.server.utils.binary.BinaryWriter;
import net.minestorm.server.utils.binary.Readable;
import net.minestorm.server.utils.binary.Writeable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTEnd;
import org.jglrxavpok.hephaistos.nbt.NBTException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class Metadata {

    // METADATA TYPES

    public static Value<Byte> Byte(byte value) {
        return new Value<>(TYPE_BYTE, value, writer -> writer.writeByte(value), BinaryReader::readByte);
    }

    public static Value<Integer> VarInt(int value) {
        return new Value<>(TYPE_VARINT, value, writer -> writer.writeVarInt(value), BinaryReader::readVarInt);
    }

    public static Value<Float> Float(float value) {
        return new Value<>(TYPE_FLOAT, value, writer -> writer.writeFloat(value), BinaryReader::readFloat);
    }

    public static Value<String> String(@NotNull String value) {
        return new Value<>(TYPE_STRING, value, writer -> writer.writeSizedString(value), BinaryReader::readSizedString);
    }

    public static Value<Component> Chat(@NotNull Component value) {
        return new Value<>(TYPE_CHAT, value, writer -> writer.writeComponent(value), BinaryReader::readComponent);
    }

    public static Value<Component> OptChat(@Nullable Component value) {
        return new Value<>(TYPE_OPTCHAT, value, writer -> {
            final boolean present = value != null;
            writer.writeBoolean(present);
            if (present) {
                writer.writeComponent(value);
            }
        }, reader -> {
            boolean present = reader.readBoolean();
            if (present) {
                return reader.readComponent();
            }
            return null;
        });
    }

    public static Value<ItemStack> Slot(@NotNull ItemStack value) {
        return new Value<>(TYPE_SLOT, value, writer -> writer.writeItemStack(value), BinaryReader::readItemStack);
    }

    public static Value<Boolean> Boolean(boolean value) {
        return new Value<>(TYPE_BOOLEAN, value, writer -> writer.writeBoolean(value), BinaryReader::readBoolean);
    }

    public static Value<Point> Rotation(@NotNull Point value) {
        return new Value<>(TYPE_ROTATION, value, writer -> {
            writer.writeFloat((float) value.x());
            writer.writeFloat((float) value.y());
            writer.writeFloat((float) value.z());
        }, reader -> new Vec(reader.readFloat(), reader.readFloat(), reader.readFloat()));
    }

    public static Value<Point> Position(@NotNull Point value) {
        return new Value<>(TYPE_POSITION, value, writer -> writer.writeBlockPosition(value), BinaryReader::readBlockPosition);
    }

    public static Value<Point> OptPosition(@Nullable Point value) {
        return new Value<>(TYPE_OPTPOSITION, value, writer -> {
            final boolean present = value != null;
            writer.writeBoolean(present);
            if (present) {
                writer.writeBlockPosition(value);
            }
        }, reader -> {
            boolean present = reader.readBoolean();
            if (present) {
                return reader.readBlockPosition();
            } else {
                return null;
            }
        });
    }

    public static Value<Direction> Direction(@NotNull Direction value) {
        return new Value<>(TYPE_DIRECTION, value, writer -> writer.writeVarInt(value.ordinal()), reader -> Direction.values()[reader.readVarInt()]);
    }

    public static Value<UUID> OptUUID(@Nullable UUID value) {
        return new Value<>(TYPE_OPTUUID, value, writer -> {
            final boolean present = value != null;
            writer.writeBoolean(present);
            if (present) {
                writer.writeUuid(value);
            }
        }, reader -> {
            boolean present = reader.readBoolean();
            if (present) {
                return reader.readUuid();
            } else {
                return null;
            }
        });
    }

    public static Value<Integer> OptBlockID(@Nullable Integer value) {
        return new Value<>(TYPE_OPTBLOCKID, value, writer -> {
            final boolean present = value != null;
            writer.writeVarInt(present ? value : 0);
        }, reader -> {
            boolean present = reader.readBoolean();
            if (present) {
                return reader.readVarInt();
            } else {
                return null;
            }
        });
    }

    public static Value<NBT> NBT(@NotNull NBT nbt) {
        return new Value<>(TYPE_NBT, nbt, writer ->
                writer.writeNBT("", nbt), reader -> {
            try {
                return reader.readTag();
            } catch (IOException | NBTException e) {
                MinecraftServer.getExceptionManager().handleException(e);
                return null;
            }
        });
    }

    public static Value<int[]> VillagerData(int villagerType,
                                            int villagerProfession,
                                            int level) {
        return new Value<>(TYPE_VILLAGERDATA, new int[]{villagerType, villagerProfession, level}, writer -> {
            writer.writeVarInt(villagerType);
            writer.writeVarInt(villagerProfession);
            writer.writeVarInt(level);
        }, reader -> new int[]{
                reader.readVarInt(),
                reader.readVarInt(),
                reader.readVarInt()
        });
    }

    public static Value<Integer> OptVarInt(@Nullable Integer value) {
        return new Value<>(TYPE_OPTVARINT, value, writer -> {
            final boolean present = value != null;
            writer.writeVarInt(present ? value + 1 : 0);
        }, reader -> {
            boolean present = reader.readBoolean();
            if (present) {
                return reader.readVarInt();
            } else {
                return null;
            }
        });
    }

    public static Value<Entity.Pose> Pose(@NotNull Entity.Pose value) {
        return new Value<>(TYPE_POSE, value, writer -> writer.writeVarInt(value.ordinal()), reader -> Entity.Pose.values()[reader.readVarInt()]);
    }

    public static final byte TYPE_BYTE = 0;
    public static final byte TYPE_VARINT = 1;
    public static final byte TYPE_FLOAT = 2;
    public static final byte TYPE_STRING = 3;
    public static final byte TYPE_CHAT = 4;
    public static final byte TYPE_OPTCHAT = 5;
    public static final byte TYPE_SLOT = 6;
    public static final byte TYPE_BOOLEAN = 7;
    public static final byte TYPE_ROTATION = 8;
    public static final byte TYPE_POSITION = 9;
    public static final byte TYPE_OPTPOSITION = 10;
    public static final byte TYPE_DIRECTION = 11;
    public static final byte TYPE_OPTUUID = 12;
    public static final byte TYPE_OPTBLOCKID = 13;
    public static final byte TYPE_NBT = 14;
    public static final byte TYPE_PARTICLE = 15;
    public static final byte TYPE_VILLAGERDATA = 16;
    public static final byte TYPE_OPTVARINT = 17;
    public static final byte TYPE_POSE = 18;

    private final Entity entity;

    private final Map<Byte, Entry<?>> metadataMap = new ConcurrentHashMap<>();

    private volatile boolean notifyAboutChanges = true;
    private final Map<Byte, Entry<?>> notNotifiedChanges = new HashMap<>();

    public Metadata(@Nullable Entity entity) {
        this.entity = entity;
    }

    @SuppressWarnings("unchecked")
    public <T> T getIndex(int index, @Nullable T defaultValue) {
        Entry<?> value = this.metadataMap.get((byte) index);
        return value != null ? (T) value.getMetaValue().value : defaultValue;
    }

    public void setIndex(int index, @NotNull Value<?> value) {
        final Entry<?> entry = new Entry<>((byte) index, value);
        this.metadataMap.put((byte) index, entry);

        // Send metadata packet to update viewers and self
        if (this.entity != null && this.entity.isActive()) {
            if (!this.notifyAboutChanges) {
                synchronized (this.notNotifiedChanges) {
                    this.notNotifiedChanges.put((byte) index, entry);
                }
                return;
            }
            this.entity.sendPacketToViewersAndSelf(new EntityMetaDataPacket(entity.getEntityId(), Collections.singleton(entry)));
        }
    }

    public void setNotifyAboutChanges(boolean notifyAboutChanges) {
        if (this.notifyAboutChanges == notifyAboutChanges) {
            return;
        }
        Collection<Entry<?>> entries = null;
        synchronized (this.notNotifiedChanges) {
            this.notifyAboutChanges = notifyAboutChanges;
            if (notifyAboutChanges) {
                entries = this.notNotifiedChanges.values();
                if (entries.isEmpty()) {
                    return;
                }
                this.notNotifiedChanges.clear();
            }
        }
        if (entries == null || this.entity == null || !this.entity.isActive()) {
            return;
        }
        this.entity.sendPacketToViewersAndSelf(new EntityMetaDataPacket(entity.getEntityId(), entries));
    }

    @NotNull
    public Collection<Entry<?>> getEntries() {
        return metadataMap.values();
    }

    public static class Entry<T> implements Writeable {

        protected byte index;
        protected Value<T> value;

        public Entry(byte index, @NotNull Value<T> value) {
            this.index = index;
            this.value = value;
        }

        public Entry(BinaryReader reader) {
            this.index = reader.readByte();
            int type = reader.readVarInt();
            value = Metadata.read(type, reader);
        }

        @Override
        public void write(@NotNull BinaryWriter writer) {
            writer.writeByte(index);
            this.value.write(writer);
        }

        public byte getIndex() {
            return index;
        }

        @NotNull
        public Value<T> getMetaValue() {
            return value;
        }
    }

    private static <T> Value<T> getCorrespondingNewEmptyValue(int type) {
        switch (type) {
            case TYPE_BYTE:
                return (Value<T>) Byte((byte) 0);
            case TYPE_VARINT:
                return (Value<T>) VarInt(0);
            case TYPE_FLOAT:
                return (Value<T>) Float(0);
            case TYPE_STRING:
                return (Value<T>) String("");
            case TYPE_CHAT:
                return (Value<T>) Chat(Component.empty());
            case TYPE_OPTCHAT:
                return (Value<T>) OptChat(null);
            case TYPE_SLOT:
                return (Value<T>) Slot(ItemStack.AIR);
            case TYPE_BOOLEAN:
                return (Value<T>) Boolean(false);
            case TYPE_ROTATION:
                return (Value<T>) Rotation(Vec.ZERO);
            case TYPE_POSITION:
                return (Value<T>) Position(Vec.ZERO);
            case TYPE_OPTPOSITION:
                return (Value<T>) OptPosition(null);
            case TYPE_DIRECTION:
                return (Value<T>) Direction(Direction.DOWN);
            case TYPE_OPTUUID:
                return (Value<T>) OptUUID(null);
            case TYPE_OPTBLOCKID:
                return (Value<T>) OptBlockID(null);
            case TYPE_NBT:
                return (Value<T>) NBT(new NBTEnd());
            case TYPE_PARTICLE:
                throw new UnsupportedOperationException();
            case TYPE_VILLAGERDATA:
                return (Value<T>) VillagerData(0, 0, 0);
            case TYPE_OPTVARINT:
                return (Value<T>) OptVarInt(null);
            case TYPE_POSE:
                return (Value<T>) Pose(Entity.Pose.STANDING);

            default:
                throw new UnsupportedOperationException();
        }
    }

    private static <T> Value<T> read(int type, BinaryReader reader) {
        Value<T> value = getCorrespondingNewEmptyValue(type);
        value.read(reader);
        return value;
    }

    public static class Value<T> implements Writeable, Readable {

        protected final int type;
        protected T value;
        protected final Consumer<BinaryWriter> valueWriter;
        protected final Function<BinaryReader, T> readingFunction;

        public Value(int type, T value, @NotNull Consumer<BinaryWriter> valueWriter, @NotNull Function<BinaryReader, T> readingFunction) {
            this.type = type;
            this.value = value;
            this.valueWriter = valueWriter;
            this.readingFunction = readingFunction;
        }

        @Override
        public void write(@NotNull BinaryWriter writer) {
            writer.writeVarInt(type);
            this.valueWriter.accept(writer);
        }

        @Override
        public void read(@NotNull BinaryReader reader) {
            // skip type, will be read somewhere else
            value = readingFunction.apply(reader);
        }

        public int getType() {
            return type;
        }

        public T getValue() {
            return value;
        }
    }

}
