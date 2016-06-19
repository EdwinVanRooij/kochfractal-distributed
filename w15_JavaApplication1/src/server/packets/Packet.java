package server.packets;

/**
 * @author Cas Eliens
 */
public abstract class Packet {

    public static final String separator = "--";

    protected PacketType type;

    public Packet(PacketType type) {
        this.type = type;
    }

    public PacketType getType() {
        return this.type;
    }
}
