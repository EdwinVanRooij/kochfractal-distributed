package client.packets;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Edwin
 */
public abstract class PacketOut extends Packet {

    protected PacketOut(PacketType type) {
        super(type);
    }
    
    public abstract void sendData(DataOutputStream out) throws IOException;
}
