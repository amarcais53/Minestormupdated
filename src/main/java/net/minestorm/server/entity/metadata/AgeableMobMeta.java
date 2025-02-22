package net.minestorm.server.entity.metadata;

import net.minestorm.server.collision.BoundingBox;
import net.minestorm.server.entity.Entity;
import net.minestorm.server.entity.Metadata;
import org.jetbrains.annotations.NotNull;

public class AgeableMobMeta extends PathfinderMobMeta {
    public static final byte OFFSET = PathfinderMobMeta.MAX_OFFSET;
    public static final byte MAX_OFFSET = OFFSET + 1;

    protected AgeableMobMeta(@NotNull Entity entity, @NotNull Metadata metadata) {
        super(entity, metadata);
    }

    public boolean isBaby() {
        return super.metadata.getIndex(OFFSET, false);
    }

    public void setBaby(boolean value) {
        if (isBaby() == value) {
            return;
        }
        BoundingBox bb = this.entity.getBoundingBox();
        if (value) {
            setBoundingBox(bb.getWidth() / 2, bb.getHeight() / 2);
        } else {
            setBoundingBox(bb.getWidth() * 2, bb.getHeight() * 2);
        }
        super.metadata.setIndex(OFFSET, Metadata.Boolean(value));
    }

}
