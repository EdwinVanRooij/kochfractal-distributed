package client.packets.out;

import client.packets.Packet;
import client.packets.PacketOut;
import client.packets.PacketType;
import main.Vector2;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Edwin
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
