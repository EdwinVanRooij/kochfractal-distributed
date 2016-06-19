package w15_javaapplication1;

/**
 *
 * @author Cas Eliens
 */
public enum EdgeRequestMode {

    Single,
    EachEdge;

    public static EdgeRequestMode fromName(String name) {
        for (EdgeRequestMode mode : EdgeRequestMode.values()) {
            if (mode.toString().equalsIgnoreCase(name)) {
                return mode;
            }
        }

        return Single;
    }
}
