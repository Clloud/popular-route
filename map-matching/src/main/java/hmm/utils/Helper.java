package hmm.utils;

import hmm.types.Point;

public class Helper {

    /*
     * Returns the great circle distance [m] between two GPS points.
     */
    public static double computeDistance(Point p1, Point p2) {
        // great circle distance
        double EARTH_RADIUS = 6378.137;
        double radLat1 = rad(p1.latitude);
        double radLat2 = rad(p2.latitude);
        double a = radLat1 - radLat2;
        double b = rad(p1.longitude) - rad(p2.longitude);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));

        s = s * EARTH_RADIUS * 1000;
        s = Math.round(s * 1000d) / 1000d;
        return s;

//        // line distance
//        double deltaX = p1.longitude - p2.longitude;
//        double deltaY = p1.latitude - p2.latitude;
//        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    public static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /* convert distance difference [m] to longitude difference [°] */
    public static double distanceToLongitude(Point point, double d) {
        // great circle distance
        return d / (Math.cos(rad(point.latitude)) * 111318.078);
//        return d;
    }

    /* convert distance difference [m] to latitude difference [°] */
    public static double distanceToLatitude(double d) {
        // great circle distance
        return d / 111319.5;
//        return d;
    }

    /*
     *  Returns the shortest great circle distance [m] from GPS point p1 to line segment
     *  determined by GPS point p2 and GPS point p3.
     */
    public static double computeDistance(Point p1, Point p2, Point p3) {
        double a = computeDistance(p2, p3);
        double b = computeDistance(p1, p2);
        double c = computeDistance(p1, p3);
        double cosAlpha = (a * a + b * b - c * c) / (2 * a * b);
        double cosBeta = (a * a + c * c - b * b) / (2 * a * c);
        if (cosAlpha <= 0) return b;
        if (cosBeta <= 0) return c;
        return (Math.sqrt((a + b + c) * (a + b - c) * (a + c - b) * (b + c - a)) / (2 * a));
    }

    /*
     * Returns the foot point p to point p1 on the line segment determined by point p2 and p3.
     */
    public static Point findFootPoint(Point p1, Point p2, Point p3) {
        double a = computeDistance(p2, p3);
        double b = computeDistance(p1, p2);
        double h = computeDistance(p1, p2, p3);
        double t = Math.sqrt(b * b - h * h);

        double x2 = p2.longitude, y2 = p2.latitude;
        double x3 = p3.longitude, y3 = p3.latitude;

        double x = x2 + (x3 - x2) / a * t;
        double y = y2 + (y3 - y2) / a * t;
        return new Point(x, y);
    }

    /*
     * Check if the foot point to point p1 is on the line segment determined by point p2 and p3.
     */
    public static Boolean hasFootPoint(Point p1, Point p2, Point p3) {
        double a = computeDistance(p2, p3);
        double b = computeDistance(p1, p2);
        double c = computeDistance(p1, p3);
        double cos_alpha = Math.round((a * a + b * b - c * c) / (2 * a * b) * 10e15d) / 10e15d;
        double cos_beta = Math.round((a * a + c * c - b * b) / (2 * a * c) * 10e15d) / 10e15d;
        if (cos_alpha >= 0 && cos_beta >= 0) return true;
        return false;
    }
}
