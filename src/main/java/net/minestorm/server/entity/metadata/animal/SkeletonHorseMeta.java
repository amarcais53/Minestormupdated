package net.minestorm.server.entity.metadata.animal;

import net.minestorm.server.entity.Entity;
import net.minestorm.server.entity.Metadata;
import org.jetbrains.annotations.NotNull;

public class SkeletonHorseMeta extends AbstractHorseMeta {
    public static final byte OFFSET = AbstractHorseMeta.MAX_OFFSET;
    public static final byte MAX_OFFSET = OFFSET + 0;

    public SkeletonHorseMeta(@NotNull Entity entity, @NotNull Metadata metadata) {
        super(entity, metadata);
    }

}
