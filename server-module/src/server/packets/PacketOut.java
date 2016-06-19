package server.packets;

/**
 * @author Edwin
 */


public abstract class PacketOut extends Packet {

    protected PacketOut(PacketType type) {
        super(type);
    }

}
