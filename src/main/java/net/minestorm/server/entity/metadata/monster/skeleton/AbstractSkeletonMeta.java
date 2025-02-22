package net.minestorm.server.entity.metadata.monster.skeleton;

import net.minestorm.server.entity.Entity;
import net.minestorm.server.entity.Metadata;
import net.minestorm.server.entity.metadata.monster.MonsterMeta;
import org.jetbrains.annotations.NotNull;

public class AbstractSkeletonMeta extends MonsterMeta {
    public static final byte OFFSET = MonsterMeta.MAX_OFFSET;
    public static final byte MAX_OFFSET = OFFSET + 0;

    protected AbstractSkeletonMeta(@NotNull Entity entity, @NotNull Metadata metadata) {
        super(entity, metadata);
    }

}
