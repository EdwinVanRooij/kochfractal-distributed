package client.client.packets.in;

import client.client.packets.Packet;
import client.client.packets.PacketIn;
import client.client.packets.PacketType;

/**
 * @author Cas Eliens
 */
public class PacketIn03FractalDone extends PacketIn {

    private int level = 0;
    private boolean allowMode;

    public PacketIn03FractalDone(String data) {
        super(PacketType.FRACTALDONE);

        String[] args = data.split(Packet.separator);

        if (args.length < 2) {
            throw new IllegalArgumentException("Invalid packet format");
        }

        try {
            this.level = Integer.parseInt(args[0]);
            allowMode = Integer.parseInt(args[1]) == 1;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid packet data");
        }
    }

    public int getLevel() {
        return this.level;
    }

    public boolean doAllowMode() {
        return this.allowMode;
    }
}
