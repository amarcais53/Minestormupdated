package net.minestorm.server.listener;

import net.minestorm.server.entity.Entity;
import net.minestorm.server.entity.Player;
import net.minestorm.server.entity.metadata.other.BoatMeta;
import net.minestorm.server.network.packet.client.play.ClientSteerBoatPacket;
import net.minestorm.server.network.packet.client.play.ClientSteerVehiclePacket;
import net.minestorm.server.network.packet.client.play.ClientVehicleMovePacket;
import net.minestorm.server.coordinate.Pos;

public class PlayerVehicleListener {

    public static void steerVehicleListener(ClientSteerVehiclePacket packet, Player player) {
        final byte flags = packet.flags;
        final boolean jump = (flags & 0x1) != 0;
        final boolean unmount = (flags & 0x2) != 0;
        player.refreshVehicleSteer(packet.sideways, packet.forward, jump, unmount);
    }

    public static void vehicleMoveListener(ClientVehicleMovePacket packet, Player player) {
        final Entity vehicle = player.getVehicle();
        if (vehicle == null)
            return;

        final var newPosition = new Pos(packet.x, packet.y, packet.z, packet.yaw, packet.pitch);
        vehicle.refreshPosition(newPosition);

        // This packet causes weird screen distortion
        /*VehicleMovePacket vehicleMovePacket = new VehicleMovePacket();
        vehicleMovePacket.x = packet.x;
        vehicleMovePacket.y = packet.y;
        vehicleMovePacket.z = packet.z;
        vehicleMovePacket.yaw = packet.yaw;
        vehicleMovePacket.pitch = packet.pitch;
        player.getPlayerConnection().sendPacket(vehicleMovePacket);*/

    }

    public static void boatSteerListener(ClientSteerBoatPacket packet, Player player) {
        final Entity vehicle = player.getVehicle();
        if (!(vehicle.getEntityMeta() instanceof BoatMeta))
            return;

        BoatMeta boat = (BoatMeta) vehicle.getEntityMeta();
        boat.setLeftPaddleTurning(packet.leftPaddleTurning);
        boat.setRightPaddleTurning(packet.rightPaddleTurning);
    }
}
