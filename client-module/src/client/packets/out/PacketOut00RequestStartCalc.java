package client.packets.out;

import client.packets.Packet;
import client.packets.PacketOut;
import client.packets.PacketType;
import main.EdgeRequestMode;

import java.io.DataOutputStream;
import java.io.IOException;


/**
 *
 * @author Edwin
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
