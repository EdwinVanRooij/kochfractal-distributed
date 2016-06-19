package client.client.packets.out;

import java.io.DataOutputStream;
import java.io.IOException;
import client.client.packets.Packet;
import client.client.packets.PacketOut;
import client.client.packets.PacketType;
import client.utils.Vector2;
import client.w15_javaapplication2.ZoomType;

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
