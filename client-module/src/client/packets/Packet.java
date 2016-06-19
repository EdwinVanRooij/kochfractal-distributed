package client.packets;

/**
 * @author Edwin
 */
public abstract class Packet {

    protected static final String separator = "--";

    protected PacketType type;

    Packet(PacketType type) {
        this.type = type;
    }

    public PacketType getType() {
        return this.type;
    }
}
