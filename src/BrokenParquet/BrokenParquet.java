package BrokenParquet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Razor
 * Date: 28.11.13
 * Time: 13:10
 */
public class BrokenParquet {
    static boolean findPath(List<Integer>[] g, int u1, int[] matching, boolean[] vis) {
        if (vis[u1]) return false;
        vis[u1] = true;
        for (int v : g[u1]) {
            int u2 = matching[v];
            if (u2 == -1 || !vis[u2] && findPath(g, u2, matching, vis)) {
                matching[v] = u1;
                return true;
            }
        }
        return false;
    }

    public static int maxMatching(List<Integer>[] g, int n2) {
        int n1 = g.length;
        int[] matching = new int[n2];
        Arrays.fill(matching, -1);
        int matches = 0;
        for (int u = 0; u < n1; u++) {
            if (findPath(g, u, matching, new boolean[n1]))
                ++matches;
//            if (matches>36300)
//                System.out.println(matches);
        }
        return matches;
    }

    public static void main(String[] args) throws Exception {
        try {
        BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
        int n=0,m=0,a=0,b=0;

            String s = "";

        try {
            int c;

                StringBuilder sb = new StringBuilder();

                while ((c = rdr.read()) != '\n') {
                    sb.append((char) c);
                    s = sb.toString();
                    s.trim();
                }
        } catch (IOException e) {
            System.exit(0);
            e.printStackTrace();
        }

        if (s == null) System.exit(0);
            String[] num = s.split("[^\\p{Digit}*]");
//        System.out.println(Arrays.toString(num));
        if (num.length != 4) System.exit(0);

        try {
             n = Integer.parseInt(num[0]);
             m = Integer.parseInt(num[1]);
             a = Integer.parseInt(num[2]);
             b = Integer.parseInt(num[3]);
        } catch(Exception e) {System.exit(0);}
        if (n<1 || m<1 || n>300 || m>300 || a>1000 || b>1000 || a<0 || b<0) System.exit(0);
        char[][] chars = new char[n][m];
        s = "";


                try {

                        int c;
                    for (int i = 0; i < chars.length; i++) {
                        StringBuilder sb = new StringBuilder();
                        int l = 0;
                        while ((c = rdr.read()) != -1) {
                        if ((char) c == '\n') break;
                        if (((char) c == '*' || (char) c == '.') && l<m) {
                            sb.append((char) c);
                            l++;
                        }
                    }
                        if (l < m) for (int j = 0; j < m - l; j++) sb.append('.');
                        s = sb.toString();
                        s.trim();
//                        s = s.substring(0,m);
                        chars[i] = s.toCharArray();
                    }
                    rdr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (rdr != null) {
                        try {
                            rdr.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            rdr.close();
        int n1 = m*n;
        int n2 = n1;
        List<Integer>[] g = new List[n1];
        for (int i = 0; i < n1; i++) {
            g[i] = new ArrayList<Integer>();
        }
        int o = 0, w = 0;
        for (int i = 0; i < chars.length; i++) {
            for (int j = 0; j < chars[i].length; j++) {
                char c = chars[i][j];
                if (c == '*') {
                    w++;
                    if (j > 0 && chars[i][j-1] == '*') g[o].add(o-1);
                    if (j < chars[i].length-1 && chars[i][j+1] == '*') g[o].add(o+1);
                    if (i > 0 && chars[i-1][j] == '*') g[o].add(o-n);
                    if (i < chars.length-2 && chars[i+1][j] == '*') g[o].add(o+n);
                }
                o++;
            }
        }
        int d = maxMatching(g, n2)/2;
        if (2*b>a) System.out.print(d*a+(w-d*2)*b);
        else System.out.print(w * b);
    } catch (StackOverflowError e) {
        System.err.println("reported recursion level was "+e.getStackTrace().length);
            System.exit(0);
        } catch (IOException e) {
            System.exit(0);
        }
//        } catch (Exception e) {
////            System.out.println(0);
////            System.out.println(e);
//            System.exit(0);
//        }
    }
}
