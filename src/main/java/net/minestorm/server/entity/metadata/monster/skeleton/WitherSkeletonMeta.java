package net.minestorm.server.entity.metadata.monster.skeleton;

import net.minestorm.server.entity.Entity;
import net.minestorm.server.entity.Metadata;
import org.jetbrains.annotations.NotNull;

public class WitherSkeletonMeta extends AbstractSkeletonMeta {
    public static final byte OFFSET = AbstractSkeletonMeta.MAX_OFFSET;
    public static final byte MAX_OFFSET = OFFSET + 0;

    public WitherSkeletonMeta(@NotNull Entity entity, @NotNull Metadata metadata) {
        super(entity, metadata);
    }

}
