package server.packets.out;

import server.packets.Packet;
import server.packets.PacketOut;
import server.packets.PacketType;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Edwin
 */

public class FractalInfoPacket extends PacketOut {

    private final int level;
    private final int edgeCount;

    public FractalInfoPacket(int level, int edgeCount) {
        super(PacketType.FRACTALINFO);

        this.level = level;
        this.edgeCount = edgeCount;
    }

    public void sendData(DataOutputStream out) throws IOException {
        out.writeBytes("++" + String.format("%02d", type.getID()) + level + Packet.separator + edgeCount + "==\n");
        out.flush();
    }

}
