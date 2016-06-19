package calculate;

import client.ClientRunnable;
import client.packets.out.PacketOut00RequestStartCalc;
import javafx.application.Platform;
import main.Edge;
import main.EdgeRequestMode;
import client.Client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cas Eliens
 */
public class KochManager {

    private Client app;
    private List<Edge> edges, tempEdges;
    private int level, edgeCount;
    private EdgeRequestMode mode = EdgeRequestMode.Single;

    public KochManager(Client app) {
        this.app = app;
        this.edges = new ArrayList<>();
        this.tempEdges = new ArrayList<>();

        // Reset tempedges
        this.tempEdges.clear();
        this.drawEdges(true);
    }

    // Drawing
    private synchronized void drawEdges(boolean allowMode) {
        app.clearKochPanel();

        for (Edge e : this.edges) {
            app.drawEdge(e, false);
        }

        // Only draw temp edges if mode is eachedge
        if (mode == EdgeRequestMode.EachEdge && allowMode) {
            for (Edge e : this.tempEdges) {
                app.drawEdge(e, true);
            }
        }
    }

    private synchronized void drawTempEdge(Edge temp) {
        this.tempEdges.add(temp);

        app.drawEdge(temp, true);
    }

    public synchronized void doneReading(boolean allowMode) {
        this.edges.clear();
        this.edges.addAll(this.tempEdges);

        this.tempEdges.clear();

        Platform.runLater(() -> this.drawEdges(allowMode));
    }

    // Edges
    public synchronized List<Edge> getEdges() {
        return new ArrayList<>(this.edges);
    }

    public synchronized void setEdges(List<Edge> edges) {
        this.tempEdges = new ArrayList<>(edges);
    }

    public synchronized void addEdge(ClientRunnable client, Edge edge, boolean allowMode) {
        if (mode == EdgeRequestMode.Single || !allowMode) {
            this.tempEdges.add(edge);
        } else {
            this.drawTempEdge(edge);
        }
    }

    // Data
    public void setLevel(int level) {
        setLevel(level, true);
    }

    public void setLevel(int level, boolean sendPacket) {
        this.level = level;
        this.tempEdges.clear();
        Platform.runLater(() -> {
            this.drawEdges(sendPacket);
            app.setLevel(level);
        });

        if (sendPacket) {
            PacketOut00RequestStartCalc startCalc = new PacketOut00RequestStartCalc(level, mode);
            try {
                startCalc.sendData(app.getClient().getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(KochManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public int getLevel() {
        return this.level;
    }

    public void setEdgeCount(int edgeCount) {
        this.edgeCount = edgeCount;
        Platform.runLater(() -> app.setTextNrEdges(edgeCount + ""));
    }

    public int getEdgeCount() {
        return this.edgeCount;
    }

    public void setMode(EdgeRequestMode mode) {
        this.mode = mode;
    }

    public EdgeRequestMode getMode() {
        return this.mode;
    }

    public void exit() {
        //
    }
}
