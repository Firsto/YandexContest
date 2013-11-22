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
*/

public class PieceOfCake {
    public static void main(String[] args) {
        int n = 4;
        int[] xa = {0, 2, 2, 0};
        int[] ya = {0, 0, 2, 2};

        //  s:= s+((y[i+1]+y[i])*(x[i+1]-x[i])/2);

        double s = 0;
        for (int i = 0; i < n-1; i++) {
            s += ((ya[i+1]+ya[i])*(xa[i+1]-xa[i])/2);
        }
        System.out.println(s);
    }
}
