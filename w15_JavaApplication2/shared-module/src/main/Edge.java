/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 *
 * @author Peter Boots
 * @author Cas Eliens
 */
public class Edge implements Serializable {

    public double X1, Y1, X2, Y2;
    public transient Color color;
    private Vector3 rgb;

    public Edge(double X1, double Y1, double X2, double Y2, Color color) {
        this.X1 = X1;
        this.Y1 = Y1;
        this.X2 = X2;
        this.Y2 = Y2;
        this.color = color;
        this.rgb = new Vector3(color.getRed(), color.getGreen(), color.getBlue());

    }

    public Edge(Vector2 side1, Vector2 side2, Vector3 color) {
        this.X1 = side1.getX();
        this.Y1 = side1.getY();
        this.X2 = side2.getX();
        this.Y2 = side2.getY();
        this.rgb = color;
        this.color = Color.color(color.getX(), color.getY(), color.getZ());
    }

    public Vector2 getSide1() {
        return new Vector2(X1, Y1);
    }

    public Vector2 getSide2() {
        return new Vector2(X2, Y2);
    }

    public Vector3 getRGB() {
        return this.rgb;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.color = Color.color(rgb.getX(), rgb.getY(), rgb.getZ());
    }
}
