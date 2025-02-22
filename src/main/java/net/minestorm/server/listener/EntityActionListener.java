package net.minestorm.server.listener;

import net.minestorm.server.entity.Player;
import net.minestorm.server.event.EventDispatcher;
import net.minestom.server.event.player.*;
import net.minestorm.server.network.packet.client.play.ClientEntityActionPacket;
import net.minestorm.server.event.player.*;

public class EntityActionListener {

    public static void listener(ClientEntityActionPacket packet, Player player) {
        ClientEntityActionPacket.Action action = packet.action;
        switch (action) {
            case START_SNEAKING:
                EntityActionListener.setSneaking(player, true);
                break;
            case STOP_SNEAKING:
                EntityActionListener.setSneaking(player, false);
                break;
            case START_SPRINTING:
                EntityActionListener.setSprinting(player, true);
                break;
            case STOP_SPRINTING:
                EntityActionListener.setSprinting(player, false);
                break;
            case START_FLYING_ELYTRA:
                EntityActionListener.startFlyingElytra(player);
                break;
            // TODO do remaining actions
        }
    }

    private static void setSneaking(Player player, boolean sneaking) {
        boolean oldState = player.isSneaking();

        player.setSneaking(sneaking);

        if (oldState != sneaking) {
            if (sneaking) {
                EventDispatcher.call(new PlayerStartSneakingEvent(player));
            } else {
                EventDispatcher.call(new PlayerStopSneakingEvent(player));
            }
        }
    }

    private static void setSprinting(Player player, boolean sprinting) {
        boolean oldState = player.isSprinting();

        player.setSprinting(sprinting);

        if (oldState != sprinting) {
            if (sprinting) {
                EventDispatcher.call(new PlayerStartSprintingEvent(player));
            } else {
                EventDispatcher.call(new PlayerStopSprintingEvent(player));
            }
        }
    }

    private static void startFlyingElytra(Player player) {
        player.setFlyingWithElytra(true);
        EventDispatcher.call(new PlayerStartFlyingWithElytraEvent(player));
    }
}
