package server.packets.out;

import server.packets.Packet;
import server.packets.PacketOut;
import server.packets.PacketType;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Edwin
 */

public class FractalDonePacket extends PacketOut {

    private final int level;
    private final boolean allowMode;

    public FractalDonePacket(int level, boolean allowMode) {
        super(PacketType.FRACTALDONE);

        this.level = level;
        this.allowMode = allowMode;
    }

    public void sendData(DataOutputStream out) throws IOException {
        out.writeBytes("++" + String.format("%02d", type.getID()) + level + Packet.separator + (allowMode ? 1 : 0) + "==\n");
        out.flush();
    }

}
