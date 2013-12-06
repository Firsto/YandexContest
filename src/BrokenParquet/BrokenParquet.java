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
        }
        return matches;
    }

    public static void main(String[] args) throws Exception {

        BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
        int n=0,m=0,a=0,b=0;
//        try {
            String s = rdr.readLine();
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

        char[][] chars = new char[n][m];
        s = "";

            for (int i = 0; i < chars.length; i++) {
                if ((s = rdr.readLine()) != null) {
                s.trim();
                chars[i] = s.toCharArray();
                chars[i] = Arrays.copyOf(chars[i], m);// chars[i].length > m ? m : chars[i].length);
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

//        } catch (Exception e) {
////            System.out.println(0);
////            System.out.println(e);
//            System.exit(0);
//        }
    }
}
