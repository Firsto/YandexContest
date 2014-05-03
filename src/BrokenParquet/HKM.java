package BrokenParquet;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class HKM {

    final int MAXN1 = 1000000;
    final int MAXN2 = 1000000;
    final int MAXM = 3000000;

    int n1, n2, edges;
    int[] last = new int[MAXN1], prev = new int[MAXM], head = new int[MAXM];
    int[] matching = new int[MAXN2], dist = new int[MAXN1], Q = new int[MAXN1];
    boolean[] used = new boolean[MAXN1], vis = new boolean[MAXN1];

    void init(int _n1, int _n2) {
        n1 = _n1;
        n2 = _n2;
        edges = 0;
        Arrays.fill(last, -1);
    }

    void addEdge(int u, int v) {
        head[edges] = v;
        prev[edges] = last[u];
        last[u] = edges++;
    }

    void bfs() {

        Arrays.fill(dist, -1);
        int sizeQ = 0;
        for (int u = 0; u < n1; ++u) {
            if (!used[u]) {
                Q[sizeQ++] = u;
                dist[u] = 0;
            }
        }
        for (int i = 0; i < sizeQ; i++) {
            int u1 = Q[i];
            for (int e = last[u1]; e >= 0; e = prev[e]) {
                int u2 = matching[head[e]];
                if (u2 >= 0 && dist[u2] < 0) {
                    dist[u2] = dist[u1] + 1;
                    Q[sizeQ++] = u2;
                }
            }
        }
    }

    boolean dfs(int u1) {
        vis[u1] = true;
        for (int e = last[u1]; e >= 0; e = prev[e]) {
            int v = head[e];
            int u2 = matching[v];
            if (u2 < 0 || !vis[u2] && dist[u2] == dist[u1] + 1 && dfs(u2)) {
                matching[v] = u1;
                used[u1] = true;
                return true;
            }
        }
        return false;
    }

    int maxMatching() {
        Arrays.fill(used, false);
        Arrays.fill(matching, -1);
        for (int res = 0;;) {
            bfs();
            Arrays.fill(vis, false);
            int f = 0;
            for (int u = 0; u < n1; ++u)
                if (!used[u] && dfs(u))
                    ++f;
            if (f==0)
                return res;
            res += f;
        }
    }

    public static void main(String[] args) {
        HKM h = new HKM();

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
                for (int i = 0; i < chars.length; ++i) {
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
//            int n2 = n1;
//            int[][] g = new int[n1][4];
            h.init(n1, 0);
//        for (int i = 0; i < n1; i++) {
//            g[i] = new ArrayList<Integer>();
//        }
            int o = 0, w = 0, t = 0;
            for (int i = 0; i < chars.length; ++i) {
                for (int j = 0; j < chars[i].length; ++j) {
                    char c = chars[i][j];
                    if (c == '*') {
                        ++w;
//                        t = 0;
                        if (j > 0 && chars[i][j-1] == '*') h.addEdge(o, (o-1));
                        if (j <= chars[i].length-2 && chars[i][j+1] == '*') h.addEdge(o, (o+1));
                        if (i > 0 && chars[i-1][j] == '*') h.addEdge(o, (o-m));
                        if (i <= chars.length-2 && chars[i+1][j] == '*') h.addEdge(o, (o+m));
                    }
                    ++o;
                }
            }
            int d = h.maxMatching()/2;
            if (2*b>a) System.out.print(d*a+(w-d*2)*b);
            else System.out.print(w * b);
        } catch (StackOverflowError e) {
            System.err.println("reported recursion level was "+e.getStackTrace().length);
            System.exit(0);
        }
        catch (IOException e) {
            System.exit(0);
        }

//
//        h.init(12, 3);
//
//        h.addEdge(0, 1);
//        h.addEdge(0, 4);
//        h.addEdge(1, 0);
//        h.addEdge(1, 2);
//        h.addEdge(2, 1);
//        h.addEdge(2, 3);
//        h.addEdge(3, 2);
//        h.addEdge(3, 7);
//        h.addEdge(4, 0);
//        h.addEdge(4, 8);
//        h.addEdge(7, 3);
//        h.addEdge(7, 11);
//        h.addEdge(8, 9);
//        h.addEdge(9, 8);
//        h.addEdge(9, 10);
//        h.addEdge(10, 11);
//        h.addEdge(11, 10);
//        h.addEdge(11, 7);
//
//        System.out.println(h.maxMatching()/2);
    }

}
