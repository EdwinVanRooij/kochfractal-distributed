package client.client.packets.out;

import java.io.DataOutputStream;
import java.io.IOException;
import client.client.packets.Packet;
import client.client.packets.PacketOut;
import client.client.packets.PacketType;
import client.utils.Vector2;

/**
 * @author Cas Eliens
 */
public class PacketOut05Press extends PacketOut {

    private Vector2 position;

    public PacketOut05Press(double x, double y) {
        super(PacketType.PRESS);

        position = new Vector2(x, y);
    }

    @Override
    public void sendData(DataOutputStream out) throws IOException {
        out.writeBytes("++" + String.format("%02d", type.getID()) + position.getX() + Packet.separator + position.getY() + "==\n");
        out.flush();
    }

}
