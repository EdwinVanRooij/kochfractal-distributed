package server.packets.in;

import server.packets.Packet;
import server.packets.PacketIn;
import server.packets.PacketType;
import utils.Vector2;

/**
 * @author Cas Eliens
 */
public class PacketIn06Drag extends PacketIn {

    private Vector2 position;

    public PacketIn06Drag(String data) {
        super(PacketType.DRAG);

        String[] args = data.split(Packet.separator);

        if (args.length < 2) {
            throw new IllegalArgumentException("Invalid packet format");
        }

        try {
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            position = new Vector2(x, y);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid packet data");
        }
    }

    public Vector2 getPosition() {
        return this.position;
    }
}
