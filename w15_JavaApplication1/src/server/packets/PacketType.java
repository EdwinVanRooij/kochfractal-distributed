package server.packets;

/**
 *
 * @author Cas Eliens
 */
public enum PacketType {

    INVALID(-1), REQUEST_START_CALC(0), FRACTALINFO(1), EDGE_SINGLE(2), FRACTALDONE(3), ZOOM(4), PRESS(5), DRAG(6);

    private int id;

    PacketType(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public static PacketType getByID(int id) {
        for (PacketType type : PacketType.values()) {
            if (type.id == id) {
                return type;
            }
        }

        return PacketType.INVALID;
    }

    @Override
    public String toString() {
        return String.format("%02d", this.id);
    }
}
