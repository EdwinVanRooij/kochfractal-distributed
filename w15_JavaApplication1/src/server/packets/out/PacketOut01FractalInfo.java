package server.packets.out;

import java.io.DataOutputStream;
import java.io.IOException;
import server.packets.Packet;
import server.packets.PacketOut;
import server.packets.PacketType;

/**
 *
 * @author Cas Eliens
 */
public class PacketOut01FractalInfo extends PacketOut {

    private int level, edgeCount;

    public PacketOut01FractalInfo(int level, int edgeCount) {
        super(PacketType.FRACTALINFO);

        this.level = level;
        this.edgeCount = edgeCount;
    }

    @Override
    public void sendData(DataOutputStream out) throws IOException {
        out.writeBytes("++" + String.format("%02d", type.getID()) + level + Packet.separator + edgeCount + "==\n");
        out.flush();
    }

}
