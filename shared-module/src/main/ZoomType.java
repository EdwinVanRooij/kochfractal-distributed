package main;

/**
 * @author Cas Eliens
 */
public enum ZoomType {

    RESET(0), INCREASE(1), DECREASE(2);

    private int id;

    ZoomType(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public static ZoomType getFromId(int id) {
        for (ZoomType type : ZoomType.values()) {
            if (type.id == id) {
                return type;
            }
        }

        return null;
    }
}
