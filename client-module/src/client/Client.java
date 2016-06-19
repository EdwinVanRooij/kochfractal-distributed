package client;

import calculate.KochManager;
import client.packets.out.PacketOut04Zoom;
import client.packets.out.PacketOut05Press;
import client.packets.out.PacketOut06Drag;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.Edge;
import main.EdgeRequestMode;
import main.ZoomType;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cas Eliens
 */
public class Client extends Application {

    // Client
    private ClientRunnable client;

    // Zoom and drag
    /*
     private double zoomTranslateX = 0.0;
     private double zoomTranslateY = 0.0;
     private double zoom = 1.0;
     private double startPressedX = 0.0;
     private double startPressedY = 0.0;
     private double lastDragX = 0.0;
     private double lastDragY = 0.0;
     */
    // Kochmanager instance
    private KochManager kochManager;

    // Main stage
    private Stage primaryStage;

    // JavaFX items
    private Label lbNrEdgesText, lbNrEdges, lbLevel, lbMode;

    private Canvas cvPanel;
    private final int panelWidth = 500;
    private final int panelHeight = 500;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        // Define grid pane
        GridPane grid;
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Add panel
        cvPanel = new Canvas(panelWidth, panelHeight);
        grid.add(cvPanel, 0, 3, 25, 1);

        // Labels to present number of edges for Koch fractal
        lbNrEdgesText = new Label("Nr edges:");
        lbNrEdges = new Label(" 0");
        grid.add(lbNrEdgesText, 0, 0, 4, 1);
        grid.add(lbNrEdges, 1, 0);

        // Label to present current level of Koch fractal
        lbLevel = new Label("Level: X");
        grid.add(lbLevel, 0, 6);

        // Button to fit Koch fractal in Koch panel
        Button buttonFitFractal = new Button();
        buttonFitFractal.setText("Fit Fractal");
        buttonFitFractal.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fitFractalButtonActionPerformed(event);
            }
        });
        grid.add(buttonFitFractal, 1, 6);

        // Button to connect to server
        Button buttonConnect = new Button();
        buttonConnect.setText("Connect");
        buttonConnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                connectButtonActionPerformed(event);
            }
        });
        grid.add(buttonConnect, 2, 6);

        // Button to increase fractal level
        Button buttonIncreaseLevel = new Button();
        buttonIncreaseLevel.setText("Increase level");
        buttonIncreaseLevel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                increaseLevelButtonActionPerformed(event);
            }
        });
        grid.add(buttonIncreaseLevel, 3, 6);

        // Button to decrease fractal level
        Button buttonDecreaseLevel = new Button();
        buttonDecreaseLevel.setText("Decrease level");
        buttonDecreaseLevel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                decreaseLevelButtonActionPerformed(event);
            }
        });
        grid.add(buttonDecreaseLevel, 4, 6);

        // Button to switch between request modes
        Button buttonSwitchMode = new Button();
        buttonSwitchMode.setText("Switch mode");
        buttonSwitchMode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switchModeButtonActionPerformed(event);
            }
        });
        grid.add(buttonSwitchMode, 0, 7);

        lbMode = new Label("Mode: Single request");
        grid.add(lbMode, 1, 7, 2, 1);

        // Add mouse clicked event to Koch panel
        cvPanel.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        kochPanelMouseClicked(event);
                    }
                });

        // Add mouse pressed event to Koch panel
        cvPanel.addEventHandler(MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        kochPanelMousePressed(event);
                    }
                });

        // Add mouse released event to Koch panel
        cvPanel.addEventHandler(MouseEvent.MOUSE_RELEASED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        kochPanelMouseReleased(event);
                    }
                });

        // Add mouse dragged event to Koch panel
        cvPanel.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                kochPanelMouseDragged(event);
            }
        });

        // Create Koch manager
        clearKochPanel();
        kochManager = new KochManager(this);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                kochManager.exit();
                try {
                    client.close();
                } catch (IOException ex) {
                    // Ignore
                }
            }
        });

        // Create the scene and add the grid pane
        Group root = new Group();
        Scene scene = new Scene(root, panelWidth + 50, panelHeight + 200);
        root.getChildren().add(grid);

        // Define title and assign the scene for main window
        primaryStage.setTitle("Koch Fractal");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initiate client
        client = new ClientRunnable(kochManager);
    }

    public void clearKochPanel() {
        GraphicsContext gc = cvPanel.getGraphicsContext2D();
        gc.clearRect(0.0, 0.0, panelWidth, panelHeight);
        gc.setFill(Color.BLACK);
        gc.fillRect(0.0, 0.0, panelWidth, panelHeight);
    }

    public synchronized void drawEdge(Edge e, boolean temp) {
        // Graphics
        GraphicsContext gc = cvPanel.getGraphicsContext2D();

        // Set line color
        if (temp) {
            gc.setStroke(Color.WHITE);
        } else {
            gc.setStroke(e.getColor());
        }

        // Set line width depending on level
        if (kochManager.getLevel() <= 3) {
            gc.setLineWidth(2.0);
        } else if (kochManager.getLevel() <= 5) {
            gc.setLineWidth(1.5);
        } else {
            gc.setLineWidth(1.0);
        }

        // Draw line
        gc.strokeLine(e.getX1(), e.getY1(), e.getX2(), e.getY2());
    }

    // Labels
    public void setTextNrEdges(String text) {
        lbNrEdges.setText(text);
    }

    public void setLevel(int level) {
        lbLevel.setText("Level: " + kochManager.getLevel());
    }

    // Event handlers
    private void fitFractalButtonActionPerformed(ActionEvent event) {
        try {
            PacketOut04Zoom zoomPack = new PacketOut04Zoom(ZoomType.RESET, 0, 0);
            zoomPack.sendData(client.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void connectButtonActionPerformed(ActionEvent event) {
        if (!client.isRunning()) {
            client = new ClientRunnable(kochManager);
        }
    }

    private void increaseLevelButtonActionPerformed(ActionEvent event) {
        if (kochManager.getLevel() < 10) {
            kochManager.setLevel(kochManager.getLevel() + 1);
        }
    }

    private void decreaseLevelButtonActionPerformed(ActionEvent event) {
        if (kochManager.getLevel() > 1) {
            kochManager.setLevel(kochManager.getLevel() - 1);
        }
    }

    private void switchModeButtonActionPerformed(ActionEvent event) {
        if (!client.isCalculating()) {
            if (kochManager.getMode() == EdgeRequestMode.Single) {
                kochManager.setMode(EdgeRequestMode.EachEdge);
                lbMode.setText("Each edge");
            } else {
                kochManager.setMode(EdgeRequestMode.Single);
                lbMode.setText("Single request");
            }
        }
    }

    private void kochPanelMouseClicked(MouseEvent event) {
        try {
            ZoomType type = ZoomType.RESET;
            if (event.getButton() == MouseButton.PRIMARY) {
                type = ZoomType.INCREASE;
            } else if (event.getButton() == MouseButton.SECONDARY) {
                type = ZoomType.DECREASE;
            }

            PacketOut04Zoom zoomPack = new PacketOut04Zoom(type, event.getX(), event.getY());
            zoomPack.sendData(client.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void kochPanelMouseDragged(MouseEvent event) {
        /*
         try {
         PacketOut06Drag dragPack = new PacketOut06Drag(event.getX(), event.getY());
         dragPack.sendData(client.getOutputStream());
         } catch (IOException ex) {
         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
         }
         */
    }

    private void kochPanelMousePressed(MouseEvent event) {
        try {
            PacketOut05Press pressPack = new PacketOut05Press(event.getX(), event.getY());
            pressPack.sendData(client.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void kochPanelMouseReleased(MouseEvent event) {
        try {
            PacketOut06Drag dragPack = new PacketOut06Drag(event.getX(), event.getY());
            dragPack.sendData(client.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Other stuffs
    public ClientRunnable getClient() {
        return this.client;
    }
}
