/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.Serializable;

/**
 *
 * @author Edwin
 */
public class Vector3 implements Serializable {

    private double x, y, z;

    public Vector3() {
        x = 0;
        y = 0;
        z = 0;
    }

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    public double getZ() {
        return z;
    }

    public float getZF() {
        return (float) z;
    }

    public boolean equals(Vector3 other) {
        return other.getX() == this.x && other.getY() == this.y && other.getZ() == this.z;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
