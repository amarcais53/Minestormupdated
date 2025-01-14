package net.minestorm.server.message;

import net.kyori.adventure.audience.MessageType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The different positions for chat messages.
 */
public enum ChatPosition {
    /**
     * A player-initiated chat message.
     */
    CHAT(MessageType.CHAT),

    /**
     * Feedback from running a command or other system messages.
     */
    SYSTEM_MESSAGE(MessageType.SYSTEM),

    /**
     * Game state information displayed above the hot bar.
     */
    GAME_INFO(null);

    private final MessageType messageType;

    ChatPosition(@Nullable MessageType messageType) {
        this.messageType = messageType;
    }

    /**
     * Gets the Adventure message type from this position. Note that there is no
     * message type for {@link #GAME_INFO}, as Adventure uses the title methods for this.
     *
     * @return the message type, if any
     */
    public @Nullable MessageType getMessageType() {
        return this.messageType;
    }

    /**
     * Gets the packet ID of this chat position.
     *
     * @return the ID
     */
    public byte getID() {
        return (byte) this.ordinal();
    }

    /**
     * Gets a position from an Adventure message type.
     *
     * @param messageType the message type
     * @return the position
     */
    public static @NotNull ChatPosition fromMessageType(@NotNull MessageType messageType) {
        switch (messageType) {
            case CHAT: return CHAT;
            case SYSTEM: return SYSTEM_MESSAGE;
        }
        throw new IllegalArgumentException("Cannot get position from message type!");
    }

    /**
     * Gets a position from a packet ID.
     *
     * @param id the id
     * @return the chat position
     */
    public static @NotNull ChatPosition fromPacketID(byte id) {
        switch (id) {
            case 0: return CHAT;
            case 1: return SYSTEM_MESSAGE;
            case 2: return GAME_INFO;
            default: throw new IllegalArgumentException("id must be between 0-2 (inclusive)");
        }
    }
}
