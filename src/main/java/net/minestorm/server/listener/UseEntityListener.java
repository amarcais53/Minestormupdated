package net.minestorm.server.listener;

import net.minestorm.server.entity.Entity;
import net.minestorm.server.entity.LivingEntity;
import net.minestorm.server.entity.Player;
import net.minestorm.server.event.EventDispatcher;
import net.minestorm.server.event.entity.EntityAttackEvent;
import net.minestorm.server.event.player.PlayerEntityInteractEvent;
import net.minestorm.server.network.packet.client.play.ClientInteractEntityPacket;

public class UseEntityListener {

    public static void useEntityListener(ClientInteractEntityPacket packet, Player player) {
        final Entity entity = Entity.getEntity(packet.targetId);
        if (entity == null)
            return;
        ClientInteractEntityPacket.Type type = packet.type;

        // Player cannot interact with entities he cannot see
        if (!entity.isViewer(player))
            return;

        if (type == ClientInteractEntityPacket.Type.ATTACK) {
            if (entity instanceof LivingEntity && ((LivingEntity) entity).isDead()) // Can't attack dead entities
                return;

            EntityAttackEvent entityAttackEvent = new EntityAttackEvent(player, entity);
            EventDispatcher.call(entityAttackEvent);
        } else if (type == ClientInteractEntityPacket.Type.INTERACT) {
            PlayerEntityInteractEvent playerEntityInteractEvent = new PlayerEntityInteractEvent(player, entity, packet.hand);
            EventDispatcher.call(playerEntityInteractEvent);
        } else {
            // TODO find difference with INTERACT
            //PlayerEntityInteractEvent playerEntityInteractEvent = new PlayerEntityInteractEvent(player, entity, packet.hand);
            //player.callEvent(PlayerEntityInteractEvent.class, playerEntityInteractEvent);
        }
    }

}
