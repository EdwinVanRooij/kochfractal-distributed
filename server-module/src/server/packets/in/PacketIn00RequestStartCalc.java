package server.packets.in;

import server.packets.Packet;
import server.packets.PacketIn;
import server.packets.PacketType;
import main.EdgeRequestMode;

/**
 * @author Edwin
 */

public class PacketIn00RequestStartCalc extends PacketIn {

    private int level;
    private EdgeRequestMode mode;

    public PacketIn00RequestStartCalc(String data) {
        super(PacketType.REQUEST_START_CALC);

        String[] args = data.split(Packet.separator);
        if (args.length < 2) {
            throw new IllegalArgumentException("Invalid packet format");
        }

        try {
            level = Integer.parseInt(args[0]);
            mode = EdgeRequestMode.fromName(args[1]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid packet data");
        }
    }

    public int getLevel() {
        return this.level;
    }

    public EdgeRequestMode getMode() {
        return this.mode;
    }
}
