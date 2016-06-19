package client.client.packets.in;

import client.client.packets.Packet;
import client.client.packets.PacketIn;
import client.client.packets.PacketType;

/**
 *
 * @author Cas Eliens
 */
public class PacketIn01FractalInfo extends PacketIn {

    private int level, edgeCount;

    public PacketIn01FractalInfo(String data) {
        super(PacketType.FRACTALINFO);

        String[] args = data.split(Packet.separator);
        if (args.length < 2) {
            throw new IllegalArgumentException("Invalid packet format");
        }

        try {
            level = Integer.parseInt(args[0]);
            edgeCount = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid packet data");
        }
    }

    public int getLevel() {
        return this.level;
    }

    public int getEdgeCount() {
        return this.edgeCount;
    }
}
