package client.packets;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Cas Eliens
 */
public abstract class PacketOut extends Packet {

    protected PacketOut(PacketType type) {
        super(type);
    }
    
    public abstract void sendData(DataOutputStream out) throws IOException;
}
