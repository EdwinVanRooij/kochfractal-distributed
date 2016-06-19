package client.packets.in;

import main.Edge;
import client.packets.Packet;
import client.packets.PacketIn;
import client.packets.PacketType;
import main.Vector2;
import main.Vector3;

/**
 * @author Edwin
 */
public class EdgePacket extends PacketIn {

    private int level;
    private Edge edge;
    private boolean allowMode;

    public EdgePacket(String data) {
        super(PacketType.EDGE_SINGLE);

        String[] args = data.split(Packet.separator);
        if (args.length < 9) {
            throw new IllegalArgumentException("Invalid packet format");
        }

        try {
            level = Integer.parseInt(args[0]);

            double x1 = Double.parseDouble(args[1]);
            double y1 = Double.parseDouble(args[2]);
            double x2 = Double.parseDouble(args[3]);
            double y2 = Double.parseDouble(args[4]);

            Vector2 side1 = new Vector2(x1, y1);
            Vector2 side2 = new Vector2(x2, y2);

            double r = Double.parseDouble(args[5]);
            double g = Double.parseDouble(args[6]);
            double b = Double.parseDouble(args[7]);

            Vector3 rgb = new Vector3(r, g, b);

            this.edge = new Edge(side1, side2, rgb);

            this.allowMode = Integer.parseInt(args[8]) == 1;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid packet data");
        }
    }

    public Edge getEdge() {
        return this.edge;
    }

    public int getLevel() {
        return this.level;
    }

    public boolean doAllowMode() {
        return this.allowMode;
    }
}
