package client.client.packets.out;

import java.io.DataOutputStream;
import java.io.IOException;
import client.client.packets.Packet;
import client.client.packets.PacketOut;
import client.client.packets.PacketType;
import client.w15_javaapplication2.EdgeRequestMode;

/**
 *
 * @author Cas Eliens
 */
public class PacketOut00RequestStartCalc extends PacketOut {

    private int level;
    private EdgeRequestMode mode;

    public PacketOut00RequestStartCalc(int level, EdgeRequestMode mode) {
        super(PacketType.REQUEST_START_CALC);

        this.level = level;
        this.mode = mode;
    }

    @Override
    public void sendData(DataOutputStream out) throws IOException {
        out.writeBytes("++" + String.format("%02d", type.getID()) + "" + level + Packet.separator + mode + "==\n");
        out.flush();
    }

}
