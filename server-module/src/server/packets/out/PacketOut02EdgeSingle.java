package server.packets.out;

import main.Edge;
import server.packets.Packet;
import server.packets.PacketOut;
import server.packets.PacketType;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Cas Eliens
 */
public class PacketOut02EdgeSingle extends PacketOut {

    private int level;
    private Edge edge;
    private boolean allowMode;

    public PacketOut02EdgeSingle(int level, Edge edge, boolean allowMode) {
        super(PacketType.EDGE_SINGLE);

        this.level = level;
        this.edge = edge;
        this.allowMode = allowMode;
    }

    @Override
    public void sendData(DataOutputStream out) throws IOException {
        String msg = "++" + String.format("%02d", type.getID()) + level + Packet.separator + edge.getX1() + Packet.separator + edge.getY1() + Packet.separator + edge.getX2() + Packet.separator + edge.getY2() + Packet.separator + edge.getRGB().getX() + Packet.separator + edge.getRGB().getY() + Packet.separator + edge.getRGB().getZ() + Packet.separator + (allowMode ? 1 : 0) + "==\n";
        out.writeBytes(msg);
        out.flush();
    }

}
