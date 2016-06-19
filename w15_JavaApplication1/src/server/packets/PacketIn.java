package server.packets;

import java.util.HashMap;
import server.Client;
import server.packets.in.*;

/**
 * @author Cas Eliens
 */
public abstract class PacketIn extends Packet {

    private static HashMap<Integer, String> lastpack = new HashMap<Integer, String>();

    public PacketIn(PacketType type) {
        super(type);
    }

    public static PacketIn parse(Client client, String data) {
        PacketType type = null;

        // Trim unnecessary characters off of data string
        if (!data.startsWith("++") && data.contains("++")) {
            data = data.substring(data.indexOf("++"));
        }

        // Trim unnecessary characters off of data string
        if (!data.endsWith("==") && data.contains("==")) {
            data = data.substring(0, data.indexOf("=="));
        }

        // Partial packetstring
        if (data.startsWith("++") && !data.endsWith("==")) {
            lastpack.put(client.getID(), data);
        }

        if (!data.startsWith("++") && !lastpack.containsKey(client.getID())) {
            lastpack.put(client.getID(), lastpack.get(client.getID()) + data);
        }

        if (data.startsWith("++") && data.endsWith("==")) {
            // Reset if data string is correct
            lastpack.remove(client.getID());
        } else if (lastpack.containsKey(client.getID()) && lastpack.get(client.getID()).startsWith("++") && lastpack.get(client.getID()).endsWith("==")) {
            // Last pack is correct data string
            data = lastpack.get(client.getID());
            lastpack.remove(client.getID());
        } else {
            // Incorrect data string
            return null;
        }

        // Remove '++' and '=='
        data = data.substring(2, data.length() - 2);

        // String should at least have 2 characters (= packet type id)
        if (data.length() < 2) {
            return null;
        }

        // Get packet type
        try {
            type = PacketType.getByID(Integer.parseInt(data.substring(0, 2)));
        } catch (NumberFormatException ex) {
            return null;
        }

        // Remove type id
        data = data.substring(2);

        switch (type) {
            case REQUEST_START_CALC:
                return new PacketIn00RequestStartCalc(data);
            case ZOOM:
                return new PacketIn04Zoom(data);
            case PRESS:
                return new PacketIn05Press(data);
            case DRAG:
                return new PacketIn06Drag(data);
        }

        return null;
    }
}
