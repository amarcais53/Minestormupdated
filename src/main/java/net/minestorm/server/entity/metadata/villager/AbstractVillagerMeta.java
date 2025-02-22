package net.minestorm.server.entity.metadata.villager;

import net.minestorm.server.entity.Entity;
import net.minestorm.server.entity.Metadata;
import net.minestorm.server.entity.metadata.AgeableMobMeta;
import org.jetbrains.annotations.NotNull;

public class AbstractVillagerMeta extends AgeableMobMeta {
    public static final byte OFFSET = AgeableMobMeta.MAX_OFFSET;
    public static final byte MAX_OFFSET = OFFSET + 1;

    protected AbstractVillagerMeta(@NotNull Entity entity, @NotNull Metadata metadata) {
        super(entity, metadata);
    }

    public int getHeadShakeTimer() {
        return super.metadata.getIndex(OFFSET, 0);
    }

    public void setHeadShakeTimer(int value) {
        super.metadata.setIndex(OFFSET, Metadata.VarInt(value));
    }
}
