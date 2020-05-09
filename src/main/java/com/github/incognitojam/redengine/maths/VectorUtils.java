package com.github.incognitojam.redengine.maths;

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

    public static Vector3ic floorMod(Vector3ic a, int b) {
        return new Vector3i(
            Math.floorMod(a.x(), b),
            Math.floorMod(a.y(), b),
            Math.floorMod(a.z(), b)
        );
    }
}
