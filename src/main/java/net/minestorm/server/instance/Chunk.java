package net.minestorm.server.instance;

import net.minestorm.server.Tickable;
import net.minestorm.server.Viewable;
import net.minestorm.server.coordinate.Point;
import net.minestorm.server.coordinate.Vec;
import net.minestorm.server.entity.Player;
import net.minestorm.server.entity.pathfinding.PFColumnarSpace;
import net.minestorm.server.event.EventDispatcher;
import net.minestorm.server.event.GlobalHandles;
import net.minestorm.server.event.player.PlayerChunkLoadEvent;
import net.minestorm.server.event.player.PlayerChunkUnloadEvent;
import net.minestorm.server.instance.block.Block;
import net.minestorm.server.instance.block.BlockGetter;
import net.minestorm.server.instance.block.BlockSetter;
import net.minestorm.server.network.packet.server.play.ChunkDataPacket;
import net.minestorm.server.tag.Tag;
import net.minestorm.server.tag.TagHandler;
import net.minestorm.server.utils.chunk.ChunkSupplier;
import net.minestorm.server.world.biomes.Biome;
import net.minestorm.server.instance.batch.Batch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// TODO light data & API

/**
 * A chunk is a part of an {@link Instance}, limited by a size of 16x256x16 blocks and subdivided in 16 sections of 16 blocks height.
 * Should contains all the blocks located at those positions and manage their tick updates.
 * Be aware that implementations do not need to be thread-safe, all chunks are guarded by their own instance ('this').
 * <p>
 * You can create your own implementation of this class by extending it
 * and create the objects in {@link InstanceContainer#setChunkSupplier(ChunkSupplier)}.
 * <p>
 * You generally want to avoid storing references of this object as this could lead to a huge memory leak,
 * you should store the chunk coordinates instead.
 */
public abstract class Chunk implements BlockGetter, BlockSetter, Viewable, Tickable, TagHandler {
    public static final int CHUNK_SIZE_X = 16;
    public static final int CHUNK_SIZE_Z = 16;
    public static final int CHUNK_SECTION_SIZE = 16;

    private final UUID identifier;

    protected Instance instance;
    @NotNull
    protected final Biome[] biomes;
    protected final int chunkX, chunkZ;

    // Options
    private final boolean shouldGenerate;
    private boolean readOnly;

    protected volatile boolean loaded = true;
    protected final Set<Player> viewers = ConcurrentHashMap.newKeySet();
    private final Set<Player> unmodifiableViewers = Collections.unmodifiableSet(viewers);

    // Path finding
    protected PFColumnarSpace columnarSpace;

    // Data
    private final NBTCompound nbt = new NBTCompound();

    public Chunk(@NotNull Instance instance, @Nullable Biome[] biomes, int chunkX, int chunkZ, boolean shouldGenerate) {
        this.identifier = UUID.randomUUID();
        this.instance = instance;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.shouldGenerate = shouldGenerate;

        final int biomeCount = Biome.getBiomeCount(instance.getDimensionType());
        if (biomes != null && biomes.length == biomeCount) {
            this.biomes = biomes;
        } else {
            this.biomes = new Biome[biomeCount];
        }
    }

    /**
     * Sets a block at a position.
     * <p>
     * This is used when the previous block has to be destroyed/replaced, meaning that it clears the previous data and update method.
     * <p>
     * WARNING: this method is not thread-safe (in order to bring performance improvement with {@link Batch batches})
     * The thread-safe version is {@link Instance#setBlock(int, int, int, Block)} (or any similar instance methods)
     * Otherwise, you can simply do not forget to have this chunk synchronized when this is called.
     *
     * @param x     the block X
     * @param y     the block Y
     * @param z     the block Z
     * @param block the block to place
     */
    @Override
    public abstract void setBlock(int x, int y, int z, @NotNull Block block);

    public abstract @NotNull Map<Integer, Section> getSections();

    public abstract @NotNull Section getSection(int section);

    /**
     * Executes a chunk tick.
     * <p>
     * Should be used to update all the blocks in the chunk.
     * <p>
     * WARNING: this method doesn't necessary have to be thread-safe, proceed with caution.
     *
     * @param time the time of the update in milliseconds
     */
    @Override
    public abstract void tick(long time);

    /**
     * Gets the last time that this chunk changed.
     * <p>
     * "Change" means here data used in {@link ChunkDataPacket}.
     * It is necessary to see if the cached version of this chunk can be used
     * instead of re writing and compressing everything.
     *
     * @return the last change time in milliseconds
     */
    public abstract long getLastChangeTime();

    /**
     * Sends the chunk data to {@code player}.
     *
     * @param player the player
     */
    public abstract void sendChunk(@NotNull Player player);

    public abstract void sendChunk();

    /**
     * Creates a copy of this chunk, including blocks state id, custom block id, biomes, update data.
     * <p>
     * The chunk position (X/Z) can be modified using the given arguments.
     *
     * @param instance the chunk owner
     * @param chunkX   the chunk X of the copy
     * @param chunkZ   the chunk Z of the copy
     * @return a copy of this chunk with a potentially new instance and position
     */
    public abstract @NotNull Chunk copy(@NotNull Instance instance, int chunkX, int chunkZ);

    /**
     * Resets the chunk, this means clearing all the data making it empty.
     */
    public abstract void reset();

    /**
     * Gets the unique identifier of this chunk.
     * <p>
     * WARNING: this UUID is not persistent but randomized once the object is instantiated.
     *
     * @return the chunk identifier
     */
    public @NotNull UUID getIdentifier() {
        return identifier;
    }

    /**
     * Gets the instance where this chunk is stored
     *
     * @return the linked instance
     */
    public @NotNull Instance getInstance() {
        return instance;
    }

    public Biome[] getBiomes() {
        return biomes;
    }

    /**
     * Gets the chunk X.
     *
     * @return the chunk X
     */
    public int getChunkX() {
        return chunkX;
    }

    /**
     * Gets the chunk Z.
     *
     * @return the chunk Z
     */
    public int getChunkZ() {
        return chunkZ;
    }

    /**
     * Gets the world position of this chunk.
     *
     * @return the position of this chunk
     */
    public @NotNull Point toPosition() {
        return new Vec(CHUNK_SIZE_X * getChunkX(), 0, CHUNK_SIZE_Z * getChunkZ());
    }

    /**
     * Gets if this chunk will or had been loaded with a {@link ChunkGenerator}.
     * <p>
     * If false, the chunk will be entirely empty when loaded.
     *
     * @return true if this chunk is affected by a {@link ChunkGenerator}
     */
    public boolean shouldGenerate() {
        return shouldGenerate;
    }

    /**
     * Gets if this chunk is read-only.
     * <p>
     * Being read-only should prevent block placing/breaking and setting block from an {@link Instance}.
     * It does not affect {@link IChunkLoader} and {@link ChunkGenerator}.
     *
     * @return true if the chunk is read-only
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Changes the read state of the chunk.
     * <p>
     * Being read-only should prevent block placing/breaking and setting block from an {@link Instance}.
     * It does not affect {@link IChunkLoader} and {@link ChunkGenerator}.
     *
     * @param readOnly true to make the chunk read-only, false otherwise
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * Changes this chunk columnar space.
     *
     * @param columnarSpace the new columnar space
     */
    public void setColumnarSpace(PFColumnarSpace columnarSpace) {
        this.columnarSpace = columnarSpace;
    }

    /**
     * Used to verify if the chunk should still be kept in memory.
     *
     * @return true if the chunk is loaded
     */
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + chunkX + ":" + chunkZ + "]";
    }

    /**
     * Sends the chunk to {@code player} and add it to the player viewing chunks collection
     * and send a {@link PlayerChunkLoadEvent}.
     *
     * @param player the viewer to add
     * @return true if the player has just been added to the viewer collection
     */
    @Override
    public boolean addViewer(@NotNull Player player) {
        final boolean result = this.viewers.add(player);

        // Add to the viewable chunks set
        player.getViewableChunks().add(this);

        // Send the chunk data & light packets to the player
        sendChunk(player);

        if (result) {
            PlayerChunkLoadEvent playerChunkLoadEvent = new PlayerChunkLoadEvent(player, chunkX, chunkZ);
            GlobalHandles.PLAYER_CHUNK_LOAD.call(playerChunkLoadEvent);
        }

        return result;
    }

    /**
     * Removes the chunk to the player viewing chunks collection
     * and send a {@link PlayerChunkUnloadEvent}.
     *
     * @param player the viewer to remove
     * @return true if the player has just been removed to the viewer collection
     */
    @Override
    public boolean removeViewer(@NotNull Player player) {
        final boolean result = this.viewers.remove(player);

        // Remove from the viewable chunks set
        player.getViewableChunks().remove(this);

        if (result) {
            PlayerChunkUnloadEvent playerChunkUnloadEvent = new PlayerChunkUnloadEvent(player, chunkX, chunkZ);
            EventDispatcher.call(playerChunkUnloadEvent);
        }

        return result;
    }

    @NotNull
    @Override
    public Set<Player> getViewers() {
        return unmodifiableViewers;
    }

    @Override
    public <T> @Nullable T getTag(@NotNull Tag<T> tag) {
        return tag.read(nbt);
    }

    @Override
    public <T> void setTag(@NotNull Tag<T> tag, @Nullable T value) {
        tag.write(nbt, value);
    }

    /**
     * Sets the chunk as "unloaded".
     */
    protected void unload() {
        this.loaded = false;
    }
}