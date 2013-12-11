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
public class BrokenParquetEdmond {
    static int lca(int[] match, int[] base, int[] p, int a, int b) {
        boolean[] used = new boolean[match.length];
        while (true) {
            a = base[a];
            used[a] = true;
            if (match[a] == -1) break;
            a = p[match[a]];
        }
        while (true) {
            b = base[b];
            if (used[b]) return b;
            b = p[match[b]];
        }
    }

    static void markPath(int[] match, int[] base, boolean[] blossom, int[] p, int v, int b, int children) {
        for (; base[v] != b; v = p[match[v]]) {
            blossom[base[v]] = blossom[base[match[v]]] = true;
            p[v] = children;
            children = match[v];
        }
    }

    static int findPath(List<Integer>[] graph, int[] match, int[] p, int root) {
        int n = graph.length;
        boolean[] used = new boolean[n];
        Arrays.fill(p, -1);
        int[] base = new int[n];
        for (int i = 0; i < n; ++i)
            base[i] = i;

        used[root] = true;
        int qh = 0;
        int qt = 0;
        int[] q = new int[n];
        q[qt++] = root;
        while (qh < qt) {
            int v = q[qh++];

            for (int to : graph[v]) {
                if (base[v] == base[to] || match[v] == to) continue;
                if (to == root || match[to] != -1 && p[match[to]] != -1) {
                    int curbase = lca(match, base, p, v, to);
                    boolean[] blossom = new boolean[n];
                    markPath(match, base, blossom, p, v, curbase, to);
                    markPath(match, base, blossom, p, to, curbase, v);
                    for (int i = 0; i < n; ++i)
                        if (blossom[base[i]]) {
                            base[i] = curbase;
                            if (!used[i]) {
                                used[i] = true;
                                q[qt++] = i;
                            }
                        }
                } else if (p[to] == -1) {
                    p[to] = v;
                    if (match[to] == -1)
                        return to;
                    to = match[to];
                    used[to] = true;
                    q[qt++] = to;
                }
            }
        }
        return -1;
    }

    public static int maxMatching(List<Integer>[] graph) {
        int n = graph.length;
        int[] match = new int[n];
        Arrays.fill(match, -1);
        int[] p = new int[n];
        for (int i = 0; i < n; ++i) {
            if (match[i] == -1) {
                int v = findPath(graph, match, p, i);
                while (v != -1) {
                    int pv = p[v];
                    int ppv = match[pv];
                    match[v] = pv;
                    match[pv] = v;
                    v = ppv;
                }
            }
        }

        int matches = 0;
        for (int i = 0; i < n; ++i)
            if (match[i] != -1)
                ++matches;
        return matches / 2;
    }

    public static void main(String[] args) throws Exception {
        try {
            BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
            int n=0,m=0,a=0,b=0;
            StringBuilder sb = new StringBuilder();
            String s = "";

            try {
                int c;



                while ((c = rdr.read()) != '\n') {
                    sb.append((char) c);
                    s = sb.toString();
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
                    sb.delete(0, sb.length());
                    int l = 0;
                    while ((c = rdr.read()) != -1) {
                        if ((char) c == '\n') break;
                        if (((char) c == '*' || (char) c == '.') && l<m) {
                            sb.append((char) c);
                            l++;
                        }
                    }
                    if (l < m) for (int j = 0; j < m-l; j++) sb.append('.');
                    s = sb.toString();
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
                        if (j <= chars[i].length-2 && chars[i][j+1] == '*') g[o].add(o+1);
                        if (i > 0 && chars[i-1][j] == '*') g[o].add(o-m);
                        if (i <= chars.length-2 && chars[i+1][j] == '*') g[o].add(o+m);
                    }
                    o++;
                }
            }
            int d = maxMatching(g)/2;
            if (2*b>a) System.out.print(d*a+(w-d*2)*b);
            else System.out.print(w * b);
        } catch (StackOverflowError e) {
            System.err.println("reported recursion level was "+e.getStackTrace().length);
            System.exit(0);
        }
        catch (IOException e) {
            System.exit(0);
        }
//        } catch (Exception e) {
////            System.out.println(0);
////            System.out.println(e);
//            System.exit(0);
//        }
    }
}