package PieceOfCake;

/**
 * Created with IntelliJ IDEA.
 * User: Razor
 * Date: 21.11.13
 * Time: 21:12
 */

/*
//        Var p,s,min: real;
//            n: integer;
//            x,y: array[1..21] of real;
//
//        Procedure Vvod;
//        Var i: integer;
//        Begin
//        write('Vvedite kolichestvo yglov mnogougolnika n=');
//        readln(n);
//        for i:= 1 to n do
//          begin
//           writeln('vvedite kkordinatu N ',i);
//           write('x[',i,']=');
//           readln(x[i]);
//           write('y[',i,']=');
//           readln(y[i]);
//          end;
//          x[n+1]:=x[1];
//          y[n+1]:=y[1];
//        End;
//
//        Procedure Perenos;
//        Var i: integer;
//        Begin
//        min:=y[1];
//        for i:= 2 to n do
//          if min >y[i] then
//           min:=y[i];
//          for i:=1 to n+1 do
//           y[i]:=y[i]-min;
//        End;
//
//        Procedure Ploshad;
//        Var i: integer;
//        Begin
//        s:=0;
//        for i:= 1 to n do
//          s:= s+((y[i+1]+y[i])*(x[i+1]-x[i])/2);
//        end;

#include <stdio.h>
#include <stdlib.h>
#include <math.h>


typedef struct{
    float x, y;
} Point2f;

float distance2f(Point2f*, Point2f*);
float triangleSqare2f(Point2f*, Point2f*, Point2f*);
float polygonSquare2fv(Point2f*, int);

int main(int argc, char** argv) {
    srand(time(0));

    int i, n;
    printf("Enter n: "); scanf("%d", &n);

    Point2f* polygon = (Point2f*)calloc(n, sizeof(Point2f));

    for(i = 0; i < n; i++){
        printf("Point %d.x: ", i + 1); scanf("%f", &polygon[i].x);
        printf("Point %d.y: ", i + 1); scanf("%f", &polygon[i].y);
    }

    printf("Polygon square: %f\n", polygonSquare2fv(polygon, n));

    free(polygon);
    return 0;
}

float distance2f(Point2f* p1, Point2f* p2){
    return sqrtf(pow(p1->x - p2->x, 2) + pow(p1->y - p2->y, 2));
}

float triangleSqare2f(Point2f* p1, Point2f* p2, Point2f* p3){
    float
        a = distance2f(p1, p2),
        b = distance2f(p2, p3),
        c = distance2f(p3, p1);
    float p = (a + b + c) / 2.0;

    return sqrtf(p*(p - a) * (p - b) * (p - c));
}

float polygonSquare2fv(Point2f* points, int n){
    float square = 0.0;
    int i;
    for(i = 1; i < n - 1; i++)
        square += triangleSqare2f(points + 0, points + i, points + i + 1);

    return square;
}


*/

public class PieceOfCake {
    public static void main(String[] args) {
//        System.out.println(20 % 3);
//        System.exit(0);
//        int n = 6;
//        int[] x = {2, 3, 2, -1, -2, -1, 0};
//        int[] y = {5, -1, -4, -4, -2, 3, 0};
        int n = 5;
//        int[] x = {-1, 1, 2, 1, -1, -2, 0};
//        int[] y = {-3, -3, 0, 3, 3, 0,  0};
//        int[] x = {2, -2, -3, 4, 11, 9,  0};
//        int[] y = {5, 4, -1, -4, -2, 3,  0};
        int[] x = {-2, -3, -2, 3 , 3 , 0};
        int[] y = {-3, 2, 3, 2, -2, 0};
        x[n]=x[0];
        y[n]=y[0];

        double s = S(x, y, n);
        System.out.println(s);

        double xc = 0;
        for (int i = 0; i < n-1; i++) {
            xc += x[i];
        }
        xc = xc/n;
        double yc = 0;
        for (int i = 0; i < n-1; i++) {
            yc += y[i];
        }
        yc = yc/n;
        System.out.println("center (" + xc + ", " + yc + ")");
    }

    public static double S(int[] x, int[] y, int n) {
        int min = y[0];
       /* for (int i = 1; i < n; i++) {
            if (min > y[i]) min = y[i];
        }
        for (int i = 0; i < n+1; i++) {
            y[i] = y[i]-min;
        }*/

        double s = 0;
        for (int i = 0; i < n; i++) {
//            s += ((y[i+1]+y[i])*(x[i+1]-x[i])/2);
            s += (x[i]*y[(i+1)%n] - y[i]*x[(i+1)%n])/2;
        }

        return s;
    }
}
