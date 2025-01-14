package net.minestorm.server.entity.metadata.monster.raider;

import net.minestorm.server.entity.Entity;
import net.minestorm.server.entity.Metadata;
import org.jetbrains.annotations.NotNull;

public class RavagerMeta extends RaiderMeta {
    public static final byte OFFSET = RaiderMeta.MAX_OFFSET;
    public static final byte MAX_OFFSET = OFFSET + 0;

    public RavagerMeta(@NotNull Entity entity, @NotNull Metadata metadata) {
        super(entity, metadata);
    }

}
