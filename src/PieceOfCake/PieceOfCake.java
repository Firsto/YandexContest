package PieceOfCake;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Razor
 * Date: 21.11.13
 * Time: 21:12
 */

public class PieceOfCake {
    public static void main(String[] args) {
//        System.out.println((double) 20);
//        System.exit(0);
        ArrayList<Point> polygon = new ArrayList<Point>();
//        polygon.add(new Point(-4,2));
//        polygon.add(new Point(-4,4));
//        polygon.add(new Point(5,4));
//        polygon.add(new Point(4,0));
//        polygon.add(new Point(0,-2));
        polygon.add(new Point(-2.56, 7.75));
        polygon.add(new Point(-1.51, 8.58));
        polygon.add(new Point(1.18, 8.2));
        polygon.add(new Point(1.35, 7.03));
        polygon.add(new Point(0.68, 5.73));
        polygon.add(new Point(-1.12, 6.15));
        polygon.add(new Point(-2.2, 6.7));
//        System.out.println(triangleCenter(polygon));
//        System.out.println("S = " + S(polygon));
//        System.out.println("S = " + triangleSquare(new Point(1,-2), new Point(3,4),new Point(5,-1)));
        System.out.println("poly S = " + polygonSquare(polygon));
        System.out.println("Polycenter: " + polygonCenter(polygon));
    }
    public static double polyS(ArrayList<Point> plist) {
        double s = 0;

        for (int i = 0; i < plist.size() - 1; i++) {
            s += s;
        }

        return s;
    }
    static double S(ArrayList<Point> plist) {

        plist.add(new Point(plist.get(0).x, plist.get(0).y));
        System.out.println(plist);
        double s = 0;
        for (int i = 0; i < plist.size()-1; i++) {
            s += (plist.get(i+1).y + plist.get(i).y) * (plist.get(i+1).x - plist.get(i).x) / 2;
//              s += (plist.get(i).x * plist.get((i+1)%plist.size()).y - plist.get(i).y * plist.get((i+1)%plist.size()).x)/2;
        }

//        for (int i = 0; i < n; i++) {
//            s += ((y[i+1]+y[i])*(x[i+1]-x[i])/2);
//            s += ((x[i+1]+x[i])*(y[i+1]-y[i])/2);
//            s += (x[i]*y[(i+1)%n] - y[i]*x[(i+1)%n])/2;
//          }

        return s;
    }

    static double distance(Point p1, Point p2){
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    static double triangleSquare(Point p1, Point p2, Point p3){
        double
                a = distance(p1, p2),
                b = distance(p2, p3),
                c = distance(p3, p1);
        double p = (a + b + c) / 2;

        return Math.sqrt(p * (p - a) * (p - b) * (p - c));
    }

    static double polygonSquare(ArrayList<Point> plist){
        double s = 0;
        for(int i = 0; i < plist.size() - 1; i++)
            s += triangleSquare(plist.get(0), plist.get(i), plist.get(i + 1));

        return s;
    }

    static Point triangleCenter(ArrayList<Point> plist) {
        Point p = new Point();
//        System.out.println("Triangle: " + plist);
        for (Point point : plist) {
            p.x += point.x/3;
            p.y += point.y/3;
        }
        System.out.println("Triangle center: " + p);
        return p;
    }

    static Point triangulation(ArrayList<Point> plist) {
        Point center = new Point();
        double sp = polygonSquare(plist);
        System.out.println("Base polygon: " + plist);
        for(int i = 1; i < plist.size() - 1; i++) {
            ArrayList<Point> triangle = new ArrayList<Point>();
            triangle.add(plist.get(0));
            triangle.add(plist.get(i));
            triangle.add(plist.get(i + 1));
            System.out.println("Triangle: " + triangle);
            double st = polygonSquare(triangle);
            System.out.println("Triangle square: " + st);
            Point p = triangleCenter(triangle);
            center.x += p.x*st;
            center.y += p.y*st;
        }
        center.x = center.x/sp;
        center.y = center.y/sp;
        System.out.println("Triangulation center: " + center);
        return center;
    }

    static Point polygonCenter(ArrayList<Point> plist) {
        Point p = new Point();
//        double s = polygonSquare(plist);
        if (plist.size() > 3) {
            p = triangulation(plist);
        }
        else {
            p = triangleCenter(plist);
        }
        return p;
    }

    protected static class Point {
        public double x;
        public double y;

        Point() {
            this.x = 0;
            this.x = 0;
        }

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + this.x + ", " + this.y + ")";
        }
    }
}
