package com.github.incognitojam.redengine.maths;

import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public class VectorUtils {
    public static boolean isOutOfBounds(Vector3ic a, int min, int max) {
        return a.x() < min || a.x() >= max
            || a.y() < min || a.y() >= max
            || a.z() < min || a.z() >= max;
    }

    public static Vector3ic floorDiv(Vector3ic a, int b) {
        return new Vector3i(
            Math.floorDiv(a.x(), b),
            Math.floorDiv(a.y(), b),
            Math.floorDiv(a.z(), b)
        );
    }

    public static Vector3ic round(Vector3fc a) {
        return new Vector3i(
            Math.round(a.x()),
            Math.round(a.y()),
            Math.round(a.z())
        );
    }

    public static Vector3ic floorMod(Vector3ic a, int b) {
        return new Vector3i(
            Math.floorMod(a.x(), b),
            Math.floorMod(a.y(), b),
            Math.floorMod(a.z(), b)
        );
    }

    public static Vector3ic[] getNeighbours(Vector3ic a) {
        return new Vector3ic[] {
            a.add(-1, 0, 0, new Vector3i()),
            a.add(1, 0, 0, new Vector3i()),
            a.add(0, -1, 0, new Vector3i()),
            a.add(0, 1, 0, new Vector3i()),
            a.add(0, 0, -1, new Vector3i()),
            a.add(0, 0, 1, new Vector3i())
        };
    }
}
