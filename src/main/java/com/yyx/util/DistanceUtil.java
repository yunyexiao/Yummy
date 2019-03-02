package com.yyx.util;

import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.acos;
import static java.lang.Math.toRadians;

public final class DistanceUtil {
    public static final double EARTH_R = 6371;

    private DistanceUtil() {

    }

    /**
     * @return the distance between two geography points in km.
     */
    public static double distance(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
        return EARTH_R * acos(
                cos(toRadians(latitudeA)) * cos(toRadians(latitudeB)) * cos(toRadians(longitudeA - longitudeB))
                + sin(toRadians(latitudeA)) * sin(toRadians(latitudeB))
        );
    }
}
