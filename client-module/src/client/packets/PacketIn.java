package client.packets;


import client.packets.in.FractalInfoPacket;
import client.packets.in.EdgePacket;
import client.packets.in.FractalDonePacket;

/**
 * @author Edwin
 */
public abstract class PacketIn extends Packet {

    private static String lastpack = "";
    
    protected PacketIn(PacketType type) {
        super(type);
    }

    public static PacketIn parse(String data) {
        PacketType type = null;

        // Trim unnecessary characters off of data string
        if (!data.startsWith("++") && data.contains("++")) {
            data = data.substring(data.indexOf("++"));
        }

        // Trim unnecessary characters off of data string
        if (!data.endsWith("==") && data.contains("==")) {
            data = data.substring(0, data.indexOf("=="));
        }

        // String should at least have 2 characters (= packet type id)
        if (data.length() < 2) {
            return null;
        }

        // Partial packetstring
        if (data.startsWith("++") && !data.endsWith("==")) {
            lastpack = data;
        }

        if (!data.startsWith("++") && !lastpack.equals("")) {
            lastpack += data;
        }

        if (data.startsWith("++") && data.endsWith("==")) {
            // Reset if data string is correct
            lastpack = "";
        } else if (lastpack.startsWith("++") && lastpack.endsWith("==")) {
            // Last pack is correct data string
            data = lastpack;
            lastpack = "";
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

        data = data.substring(2);

        switch (type) {
            case FRACTALINFO:
                return new FractalInfoPacket(data);
            case EDGE_SINGLE:
                return new EdgePacket(data);
            case FRACTALDONE:
                return new FractalDonePacket(data);
        }

        return null;
    }
}
