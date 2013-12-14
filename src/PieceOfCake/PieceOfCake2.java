package PieceOfCake;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Razor
 * Date: 21.11.13
 * Time: 21:12
 */

public class PieceOfCake2 {
    final static double eps = 1e-14;
//    final static double eps = Double.MIN_VALUE;
    public static void main(String[] args) {
//        System.out.println((double) 20);
//        System.exit(0);
        ArrayList<Point> polygon = new ArrayList<Point>();
//        polygon.add(new Point(0, 0));
//        polygon.add(new Point(0, 2));
//        polygon.add(new Point(2, 2));
//        polygon.add(new Point(2, 0));
        BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
        int n = 0;
        String str = "";
        try {
            n = Integer.parseInt(rdr.readLine());
            for (int i = 0; i < n; i++) {
                str = rdr.readLine();
//                double x = Double.parseDouble(str.split(" ")[0]);
                int x = Integer.parseInt(str.split(" ")[0]);
//                double y = Double.parseDouble(str.split(" ")[1]);
                int y = Integer.parseInt(str.split(" ")[1]);
                polygon.add(new Point(x,y));
            }
        } catch (IOException e) {
            System.out.print(-1);
            System.exit(0);
        }
        if (!is_convex(polygon)) {
            System.out.print(-1);
            System.exit(0);
        }
//        System.out.println(polygon);
//        System.out.println("Base polygon: " + polygon);
        double ps = polygonSquare(polygon);
        double psq = ps/4;
        double p0 = 0, p1 = 0, ps0 = 0, ps1 = 0, ps2 = 0, ps3 = 0, qps = 0;
//        System.out.println("polygon Square = " + ps + " ; quart = " + psq);
//        Line line1 = new Line(new Point(6,1.56), new Point(6,-0.7));
        ArrayList<Point> halfpoly1 = new ArrayList<Point>();
        ArrayList<Point> halfpoly2 = new ArrayList<Point>();
//        Line line2 = perpendicularLine(line1, new Point(1,1));
        ArrayList<Point>[] quartpoly = new ArrayList[4];
        for (int l = 0; l < quartpoly.length; l++) {
            quartpoly[l] = new ArrayList<Point>();
        }
//        quartpoly[0].clear();quartpoly[1].clear();quartpoly[2].clear();quartpoly[3].clear();
//        dividePolygonByLine(halfpoly1, line2, quartpoly[0], quartpoly[1], intersectPoint1, intersectPoint2);
//        dividePolygonByLine(halfpoly2, line2, quartpoly[2], quartpoly[3], intersectPoint1, intersectPoint2);
//        for (int l = 0; l < quartpoly.length; l++) {
//            ArrayList<Point> points = quartpoly[l];
//            System.out.println("Polygon #" + l + " " + points + " with S = " + polygonSquare(points));
//        }

        ArrayList<Point> s = new ArrayList<>();
        ArrayList<Point> sv = new ArrayList<>();
        ArrayList<Point> qp1 = new ArrayList<>();
        ArrayList<Point> qp2 = new ArrayList<>();
        Point intersectPoint1 = new Point();
        Point intersectPoint2 = new Point();
        Point p = new Point();
        Point pt = new Point();
        Point pp = new Point();
        Point pp0 = new Point();
        Point pp1 = new Point();
        Point pp2 = new Point();
        Point pp3 = new Point();
        Point leftpoint = new Point();
        Point rightpoint = new Point();
        Line line1 = new Line(p,p);
        Line line2 = new Line(p,p);
        Line line3 = new Line(p,p);
        int m = 1000000;
        n = polygon.size();
        int n1 = 0; int n2 = 0;
        boolean isFound = false;
        searching:
        for (int j = 0; j < n; j++) {
            p.x = polygon.get(j).x;p.y = polygon.get(j).y;
            leftpoint.x = polygon.get(j).x; leftpoint.y = polygon.get(j).y;
            rightpoint.x = polygon.get(j==n-1?0:j+1).x; rightpoint.y = polygon.get(j==n-1?0:j+1).y;
//            intersectPoint1 = part_segment(polygon.get(j), polygon.get(j==n-1?0:j+1), 1, 1);
//            p = part_segment(intersectPoint1, intersectPoint2, 1, 1);
//            p = part_segment(polygon.get(j), polygon.get(j==n-1?0:j+1), 1, 1);
            for (int i = 0; i < 64; i++) {
                if (i!=0) p = part_segment(leftpoint, rightpoint, 1, 1);
//                System.out.println(p);
                intersectPoint1.x = p.x;intersectPoint1.y=p.y;
                pt.x=p.x;pt.y=p.y;
                s.clear(); sv.clear(); halfpoly1.clear(); halfpoly2.clear();
                s.addAll(polygon);
//                if (i!=0)
                    if (j == n-1) s.add(intersectPoint1); else s.add(j+1, intersectPoint1);
                for (int k = 0; k <= j; k++) {
                    s.add(s.get(0));
                    s.remove(0);
                }
                npart_convex(s, 2, sv);
                intersectPoint2.x = sv.get(0).x; intersectPoint2.y = sv.get(0).y;
//                System.out.println(intersectPoint2);
                line1 = new Line(intersectPoint1, intersectPoint2);
                dividePolygonByLine(polygon, line1, halfpoly1, halfpoly2, intersectPoint1, intersectPoint2);
//                System.out.println(s + " -- ip: " + intersectPoint2);
//                System.out.printf("Squares: base %f; left %f; right %f \n", polygonSquare(polygon), polygonSquare(halfpoly1), polygonSquare(halfpoly2));
//                System.out.println(line1);
                n1 = halfpoly1.size(); n2 = halfpoly2.size();
                p0 = polygonSquare(halfpoly1);
                p1 = polygonSquare(halfpoly2);

//                if (p0!=p1) continue ;
//                System.out.println("HALFPOLY 1 : " + halfpoly1 + " ; SQ : " + p0);
//                System.out.println("HALFPOLY 2 : " + halfpoly2 + " ; SQ : " + p1);

//                if (p0 < p1) {rightpoint.x = p.x;rightpoint.y=p.y;continue;}
//                if (p0 > p1) {leftpoint.x = p.x;leftpoint.y=p.y;continue;}

//                if (ps0 > ps1) {intersectPoint1.x = p.x;intersectPoint1.y=p.y;continue;}
//                if (ps0 < ps1) {intersectPoint2.x = p.x;intersectPoint2.y=p.y;continue;}
//                if (p0 == 0 || p1 == 0 || p0 != p1) continue;
//                System.out.println("p0: " + p0 + " ; p1: " + p1 + " ; j = " + j + " ; i = " + i);
                for (int k = 1; k < 64; k++) {
//                    p = part_segment(intersectPoint1, intersectPoint2, k, m - k);
/*
                    qp1.clear(); qp2.clear(); sv.clear();
                    qp2.addAll(halfpoly1); qp2.addAll(halfpoly2);
                    if (qp1.isEmpty() || qp2.isEmpty()) continue;
                    qp1.add(p); qp2.add(1, p);
                    qp1.add(qp1.get(0)); qp2.add(qp2.get(0));
                    qp1.remove(0); qp2.remove(0);

                    npart_convex(qp1, 2, sv);
                    line2 = new Line(p, sv.get(0));
                    npart_convex(qp2, 2, sv);
                    line3 = new Line(p, sv.get(1));

                    if (is_equal_line(line2, line3)) {
                        System.out.println("BINGO!");
                        isFound = true;
                        break searching;
                    }
                    System.out.println(p);
*/

                    p = part_segment(intersectPoint1, intersectPoint2, 1, 1);
//                    System.out.println("p = " + p + " ; ip1 = " + intersectPoint1 + " ; ip2 = " + intersectPoint2);
                    line2 = perpendicularLine(line1, p);
                    quartpoly[0].clear();quartpoly[1].clear();quartpoly[2].clear();quartpoly[3].clear();
//                    System.out.println("HALFPOLY 1 : " + halfpoly1 + " ; SQ : " + p0);
//                    System.out.println("HALFPOLY 2 : " + halfpoly2 + " ; SQ : " + p1);
                    dividePolygonByLine(halfpoly1, line2, quartpoly[0], quartpoly[1], pp0, pp1);
                    dividePolygonByLine(halfpoly2, line2, quartpoly[2], quartpoly[3], pp2, pp3);
//                    System.out.println("pp0 : " + pp0 + " ; pp1 : " + pp1 + " ; pp2 : " + pp2 + " ; pp3 : " + pp3);
                    ps0 = polygonSquare(quartpoly[0]);
                    ps1 = polygonSquare(quartpoly[1]);
                    if (k==1) qps = ps1;
                    ps2 = polygonSquare(quartpoly[2]);
                    ps3 = polygonSquare(quartpoly[3]);
//                    System.out.println("qp0sq: " + ps0 + " ; qp1sq: " + ps1 + " ; qp2sq: " + ps2 + " ; qp3sq: " + ps3);
//                    System.out.println("0#> " + quartpoly[0] + " -- 1#> " + quartpoly[1] + " -- 2#> " + quartpoly[2] + " -- 3#> " +quartpoly[3]);
//                    if (k==4) break ;
                    if (ps0==0) continue;
//                    System.exit(0);
                    if (Math.abs(psq-ps0)<eps)ps0=psq;
                    if (Math.abs(psq-ps1)<eps)ps1=psq;
                    if (Math.abs(psq-ps2)<eps)ps2=psq;
                    if (Math.abs(psq-ps3)<eps)ps3=psq;
                    if (ps0 > psq || ps1 < psq /*ps1 > ps2 && ps0 != ps2*/) {intersectPoint1.x = p.x;intersectPoint1.y=p.y;continue;}
                    if (ps0 < psq || ps1 > psq /*ps1 < ps2 && ps0 != ps2*/) {intersectPoint2.x = p.x;intersectPoint2.y=p.y;continue;}
                    if (ps0 == psq && ps1 == psq && (ps2 != psq || ps3 != psq)) break;
                    if (
                        polygonSquare(quartpoly[0])!=0 &&
                                Math.abs(psq - polygonSquare(quartpoly[0])) < eps &&
                                Math.abs(psq - polygonSquare(quartpoly[2])) < eps &&
                                Math.abs(psq - polygonSquare(quartpoly[3])) < eps
//                                ps0 == psq &&
//                                ps2 == psq &&
//                                ps3 == psq

                            ){
//                        System.out.println("BINGO!");
//                        System.out.println("halfpoly1 : " + halfpoly1);
//                        System.out.println("halfpoly1 square = " + polygonSquare(halfpoly1));
//                        System.out.println("halfpoly2 : " + halfpoly2);
//                        System.out.println("halfpoly2 square = " + polygonSquare(halfpoly2));
//                        System.out.println("k = " + k);
                        for (int l = 0; l < quartpoly.length; l++) {
                            ArrayList<Point> points = quartpoly[l];
//                            System.out.println("Polygon #" + l + " " + points + " with S = " + polygonSquare(points));
                         }
                        isFound = true;
                        break searching;
                    }

//                    System.out.println("halfpoly1 : " + halfpoly1 + " ; halfpoly1 square = " + polygonSquare(halfpoly1));
//                    System.out.println("halfpoly2 : " + halfpoly2 + " ; halfpoly2 square = " + polygonSquare(halfpoly2));
//                    System.out.println("k = " + k);
                }
//                if (i == 0) continue ;
                if (ps0!=0 && (ps0 < psq  )) {rightpoint.x = pt.x;rightpoint.y=pt.y;}
                else if (ps0!=0 && ps2>psq ) {rightpoint.x = pt.x;rightpoint.y=pt.y;}
                if (ps0!=0 && (ps0 > psq  )) {leftpoint.x = pt.x;leftpoint.y=pt.y;}
                else if (ps0!=0 && ps2<psq ) {leftpoint.x = pt.x;leftpoint.y=pt.y;}
//                else if (ps0!=0 && (ps0 == psq) && ) {rightpoint.x = pt.x;rightpoint.y=pt.y;}

            }
        }
        if (isFound) {
//            intersectPoint1.x = BigDecimal.valueOf(intersectPoint1.x).setScale(6,BigDecimal.ROUND_HALF_UP).doubleValue();
//            intersectPoint1.y = BigDecimal.valueOf(intersectPoint1.y).setScale(6,BigDecimal.ROUND_HALF_UP).doubleValue();

//            p.x = BigDecimal.valueOf(p.x).setScale(6,BigDecimal.ROUND_HALF_UP).doubleValue();
//            p.y = BigDecimal.valueOf(p.y).setScale(6,BigDecimal.ROUND_HALF_UP).doubleValue();

            double k = (intersectPoint2.y - pt.y)/(intersectPoint2.x - pt.x);
//            System.out.println("k = " + k + " ; degrees = " + Math.toDegrees(Math.atan(k)));
            if (k < 0) {
                dividePolygonByLine(polygon, line2, halfpoly1, halfpoly2, intersectPoint1, intersectPoint2);
                k = (intersectPoint1.y - p.y)/(intersectPoint1.x - p.x);
            }
//            System.out.println(k);
            k = Math.toDegrees(Math.atan(k));
//            k = BigDecimal.valueOf(k).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();

            if (Math.abs(Math.round(p.x)-p.x) < eps && Math.abs(Math.round(p.y)-p.y) < eps){
                System.out.println(Math.round(p.x) + " " + Math.round(p.y));
            }   else {
                System.out.println(p.x + " " + p.y);
            }
//            System.out.println(p.x + " " + p.y);
            if (Math.abs(Math.round(k)-k) < eps){
            System.out.print(Math.round(k));
            } else
                System.out.print(k);

        } else System.out.print(-1);
//        System.out.println(s);

//        System.out.println("First polygon : " + halfpoly1);
//        System.out.println("Second polygon : " + halfpoly2);
//        System.out.println("First intersect point : " + intersectPoint1);
//        System.out.println("Second intersect point : " + intersectPoint2);
//        System.out.printf("Squares: base %f; left %f; right %f \n", polygonSquare(polygon), polygonSquare(halfpoly1), polygonSquare(halfpoly2));
//        System.out.printf("Perimeters: base %f, left %f, right %f \n", perimeter_polygon(polygon), perimeter_polygon(halfpoly1), perimeter_polygon(halfpoly2));
//        System.out.println(lineIntersection(l1, new Line(polygon.get(0),polygon.get(1)), p3));

//        Line l2 = new Line(new Point(-12,-6), new Point(-8,2));
//        System.out.println(lineIntersection(l1,l2));

    }

//    static void
/*
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
*/

    static boolean point_in_box (Point t, Point p1, Point p2)
    {
        return  (Math.abs (t.x - Math.min(p1.x, p2.x)) <= eps || Math.min(p1.x, p2.x) <= t.x) &&
                (Math.abs (Math.max(p1.x, p2.x) - t.x) <= eps || Math.max(p1.x, p2.x) >= t.x) &&
                (Math.abs (t.y - Math.min(p1.y, p2.y)) <= eps || Math.min(p1.y, p2.y) <= t.y) &&
                (Math.abs (Math.max(p1.y, p2.y) - t.y) <= eps || Math.max(p1.y, p2.y) >= t.y);
    }

    static int cross_segment_line (Point p1, Point p2, Line l, Point p)
    {
        Line t = new Line(p1, p2);
        int flag = lineIntersection (l, t, p);
        if (flag == 0) return 0;
        else if (flag == 2) return 2;
        else if (point_in_box (p, p1, p2)) return 1;
        else return 0;
    }

    static double distance(Point p1, Point p2){
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    static double perimeter_polygon (ArrayList<Point> plist)
    {
        int i, j;
        double perimeter = 0;
        for (i = 0; i < plist.size(); ++ i)
        {
            j = (i + 1) % plist.size();
            perimeter += distance(plist.get(i), plist.get(j));
        }
        return perimeter;
    }


    static Point part_segment (Point p1, Point p2, double m, double n)
    {
        Point t = new Point();
        t.x = (p1.x * n + p2.x * m) / (m + n);
        t.y = (p1.y * n + p2.y * m) / (m + n);
        return t;
    }

    static double triangleSquare(Point p1, Point p2, Point p3){

        ArrayList<Point> plist = new ArrayList<>();
        plist.add(p1);
        plist.add(p2);
        plist.add(p3);
        return polygonSquare(plist);

        /*
        double
                a = distance(p1, p2),
                b = distance(p2, p3),
                c = distance(p3, p1);
        double p = (a + b + c) / 2;

        return Math.sqrt(p * (p - a) * (p - b) * (p - c));
        */
    }


    static boolean ccw (Point a, Point b, Point c)
    {
        return triangleSquare (a, b, c) > eps;
    }

    static boolean is_convex (ArrayList<Point> polygon)
    {
        int l, i, r;
        int n = polygon.size();
        boolean isccw = ccw (polygon.get(n-1), polygon.get(0), polygon.get(1));
        for (i = 1; i < n; ++ i)
        {
            l = (i - 1 + n) % n;
            r = (i + 1) % n;
            if (ccw (polygon.get(l), polygon.get(i), polygon.get(r)) != isccw)
                return false;
        }
        return true;
    }
    static double polygonSquare(ArrayList<Point> plist){
//        double s = 0;
//        for(int i = 0; i < plist.size() - 1; i++)
//            s += triangleSquare(plist.get(0), plist.get(i), plist.get(i + 1));
//
//        return s;

        int i, j;
        double s = 0;
        for (i = 0; i < plist.size(); ++ i)
        {
            j = (i + 1) % plist.size();
            s += plist.get(i).x * plist.get(j).y - plist.get(j).x * plist.get(i).y;
        }
        return Math.abs(0.5 * s);

//        return BigDecimal.valueOf(d).setScale(6,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    static int point_in_line (Line l, Point p)
    {
        double s = l.a * p.x + l.b * p.y + l.c;
        return s < - eps ? - 1 : s > eps ? 1 : 0;
    }

    static boolean is_parallel_line (Line l1, Line l2)
    {
        return Math.abs (l1.a * l2.b - l2.a * l1.b) <= eps;
    }

    static boolean is_equal_line (Line l1, Line l2)
    {
        return Math.abs (l1.a * l2.b - l2.a * l1.b) <= eps &&
        Math.abs (l1.a * l2.c - l2.a * l1.c) <= eps &&
        Math.abs (l1.b * l2.c - l2.b * l1.c) <= eps;
    }
    static int lineIntersection (Line l1, Line l2, Point p) {
        if (is_equal_line (l1, l2)) return 2;
        if (is_parallel_line (l1, l2)) return 0;

        p.x = (l2.b * l1.c - l1.b * l2.c) / (l2.a * l1.b - l1.a * l2.b);
        p.y = (l1.b != 0 ? (- l1.c - l1.a * p.x) / l1.b : (-l2.c - l2.a * p.x) / l2.b);

        return 1;
    }
    static Line perpendicularLine (Line l, Point p) {
        Line pl = new Line(new Point(), new Point());
        pl.a = l.b;
        pl.b = -l.a;
        pl.c = -l.b * p.x + l.a * p.y;

        return pl;
    }

    static int polygon_for_line (ArrayList<Point> polygon, Line l)
    {
        int i, j;
        int s = - 2;
        for (i = 0; i < polygon.size(); ++ i)
        {
            int t = point_in_line (l, polygon.get(i));
            if (t != 0)
                if (s != - 2)
                    if (t != s)
                        return 0;
                    else
                    {}
                else
                    s = t;
        }
        if (s == - 2) return 0;
        return s;
    }
    static void dividePolygon (ArrayList<Point> polygon, int i1, int i2, ArrayList<Point> p1, ArrayList<Point> p2) {
        int i;
        int n = polygon.size();

        for (i = i1; i != (i2 + 1) % n; i = (i + 1) % n)
            p1.add(polygon.get(i));

        for (i = i2; i != (i1 + 1) % n; i = (i + 1) % n)
            p2.add(polygon.get(i));
    }

    static void dividePolygonByLine (ArrayList<Point> polygon, Line l, ArrayList<Point> v1, ArrayList<Point> v2, Point p1, Point p2)
    {
        int n = polygon.size();
        int i, j;

/*//        ArrayList<Point> v1 = new ArrayList<Point>();
//        ArrayList<Point> v2 = new ArrayList<Point>();
//        Point p1 = new Point();
//        Point p2 = new Point();*/

        int c = 0;
        ArrayList<Point> s = new ArrayList<Point>();
        s.addAll(polygon);
//        Iterator<Point> it = s.iterator(), jt = s.iterator();
//        Iterator<Point> i1 = s.iterator(), i2 = s.iterator();
        Point i1 = new Point(), i2 = new Point();

        Point jtp;
        for ( i = 0; i < s.size(); i++) {
            Point itp = s.get(i);
//            jt.next();
            if (i == s.size()-1) jtp = s.get(0);
            else jtp = s.get(i+1);


            Point t = new Point();
            int flag = cross_segment_line (itp, jtp, l, t);

            if (flag == 2)
            {
                if (polygon_for_line (polygon, l) > 0)    v1 = polygon;
                else    v2 = polygon;
                return;
            }

            if (flag == 0) continue;


            if (Math.abs (t.x - (itp).x) <= eps && Math.abs (t.y - (itp).y) <= eps)
            {
                if (c == 0) i1 = s.get(i);
                else i2 = s.get(i);
                ++ c;
                continue;
            }
            if (Math.abs (t.x - (jtp).x) <= eps && Math.abs (t.y - (jtp).y) <= eps) continue;


            i++;
//            itp = s.get(i);
//            s.add(s.indexOf(itp), t);
            s.add(i, t);


            if (c == 0) i1 = s.get(i);
            else i2 = s.get(i);
            ++ c;
        }


        if (c != 2)
        {
            if (polygon_for_line (polygon, l) > 0)    v1 = polygon;
            else    v2 = polygon;
            return;
        }


        n = s.size ();
        ArrayList<Point> all = new ArrayList<Point>();
        all.addAll(s);
        int j1 = 0, j2 = 0;
        for (i = 0; i < s.size(); i++)
        {
            if (s.get(i).x == i1.x && s.get(i).y == i1.y) j1 = i;
            if (s.get(i).x == i2.x && s.get(i).y == i2.y) j2 = i;
        }


        p1.x = all.get(j1).x; p1.y = all.get(j1).y;
        p2.x = all.get(j2).x; p2.y = all.get(j2).y;
        dividePolygon(all, j1, j2, v1, v2);


        if (polygon_for_line (v1, l) < 0);  {
            ArrayList<Point> tv = new ArrayList<Point>();
            tv.addAll(v1);
            v1.clear(); v1.addAll(v2);
            v2.clear(); v2.addAll(tv);
        }
    }

    static Point part_convex (ArrayList<Point> v, double m, double n)
    {
        double area = Math.abs(polygonSquare(v)) / (m + n) * m;
        double a = 0;
        int i;
        for (i = 1; i < v.size () - 1; ++ i)
        {
            double s = Math.abs(triangleSquare(v.get(0), v.get(i), v.get(i + 1)));
            if (a + s <= area)
                a += s;
            else break;
        }
        if (Math.abs(a - area) <= eps) return v.get(i);
        return part_segment (v.get(i), v.get(i + 1), area - a,
                Math.abs (triangleSquare(v.get(0), v.get(i), v.get(i + 1)) - area + a));
    }

    static void npart_convex (ArrayList<Point> v, int k, ArrayList<Point> s)
    {
        double area = Math.abs(polygonSquare(v));
        double a = area / (double) k;
//        System.out.println(" a = " + a);
        int i;
        for (i = 1; i < k; ++ i)
            s.add(part_convex(v, a * i, area - a * i));
    }

    static Point triangleCenter(ArrayList<Point> plist) {
        Point p = new Point();
//        System.out.println("Triangle: " + plist);
        for (Point point : plist) {
            p.x += point.x/3;
            p.y += point.y/3;
        }
//        System.out.println("Triangle center: " + p);
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
//            System.out.println("Triangle square: " + st);
            Point p = triangleCenter(triangle);
            center.x += p.x*st;
            center.y += p.y*st;
        }
        center.x = center.x/sp;
        center.y = center.y/sp;
//        System.out.println("Triangulation center: " + center);
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
        public double x, y;

        Point() {
            this.x = 0;
            this.y = 0;
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
    protected static class Line {
        public double a, b, c;
        Line (Point p1, Point p2) {
            this.a = p2.y - p1.y;
            this.b = p1.x - p2.x;
            this.c = - a * p1.x - b * p1.y;
        }
        @Override
        public String toString() {
            return "line: " + this.a + "x " + (this.b>0?"+ ":"") + this.b + "y = " + this.c;
        }
    }
}
