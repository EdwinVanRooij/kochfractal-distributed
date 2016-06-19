package client.packets.out;

import client.packets.Packet;
import client.packets.PacketOut;
import client.packets.PacketType;
import main.Vector2;
import main.ZoomType;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Cas Eliens
 */
public class PacketOut04Zoom extends PacketOut {

    private ZoomType zoom;
    private Vector2 position;

    public PacketOut04Zoom(ZoomType zoom, double x, double y) {
        super(PacketType.ZOOM);

        this.zoom = zoom;
        this.position = new Vector2(x, y);
    }

    @Override
    public void sendData(DataOutputStream out) throws IOException {
        out.writeBytes("++" + String.format("%02d", type.getID()) + zoom.getID() + Packet.separator + position.getX() + Packet.separator + position.getY() + "==\n");
        out.flush();
    }

}
