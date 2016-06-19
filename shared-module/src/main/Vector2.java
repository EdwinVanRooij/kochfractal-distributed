package main;

import java.io.Serializable;

/**
 *
 * @author Cas Eliens
 */
public class Vector2 implements Serializable {

    private double x;
    private double y;

    public Vector2() {
        x = 0;
        y = 0;
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public float getXF() {
        return (float) x;
    }

    public double getY() {
        return y;
    }

    public float getYF() {
        return (float) y;
    }

    public boolean equals(Vector2 other) {
        return other.getX() == this.x && other.getY() == this.y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
