package BrokenParquet;

/**
 * Created with IntelliJ IDEA.
 * User: Razor
 * Date: 29.11.13
 * Time: 20:42
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class MaxFlowEdmondKarp {

    static class Edge {
        int s, t, rev, cap, f;

        public Edge(int s, int t, int rev, int cap) {
            this.s = s;
            this.t = t;
            this.rev = rev;
            this.cap = cap;
        }
    }

    public static List<Edge>[] createGraph(int nodes) {
        List<Edge>[] graph = new List[nodes];
        for (int i = 0; i < nodes; i++)
            graph[i] = new ArrayList<Edge>();
        return graph;
    }

    public static void addEdge(List<Edge>[] graph, int s, int t, int cap) {
        graph[s].add(new Edge(s, t, graph[t].size(), cap));
        graph[t].add(new Edge(t, s, graph[s].size() - 1, 0));
    }

    public static int maxFlow(List<Edge>[] graph, int s, int t) {
        int flow = 0;
        int[] q = new int[graph.length];
        while (true) {
            int qt = 0;
            q[qt++] = s;
            Edge[] pred = new Edge[graph.length];
            for (int qh = 0; qh < qt && pred[t] == null; qh++) {
                int cur = q[qh];
                for (Edge e : graph[cur]) {
                    if (pred[e.t] == null && e.cap > e.f) {
                        pred[e.t] = e;
                        q[qt++] = e.t;
                    }
                }
            }
            if (pred[t] == null)
                break;
            int df = Integer.MAX_VALUE;
            for (int u = t; u != s; u = pred[u].s)
                df = Math.min(df, pred[u].cap - pred[u].f);
            for (int u = t; u != s; u = pred[u].s) {
                pred[u].f += df;
                graph[pred[u].t].get(pred[u].rev).f -= df;
            }
            flow += df;
        }
        return flow;
    }

    // Usage example
    public static void main(String[] args) {

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
            int n2 = n1;
            int[][] g = new int[n1][4];
            List<Edge>[] graph = createGraph(n2);
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
                        if (j > 0 && chars[i][j-1] == '*') addEdge(graph, o, o-1, 1); //g[o][t++] = (o-1);
                        if (j <= chars[i].length-2 && chars[i][j+1] == '*') addEdge(graph, o, o+1, 1); //g[o][t++] = (o+1);
                        if (i > 0 && chars[i-1][j] == '*') addEdge(graph, o, o-m, 1); //g[o][t++] = (o-m);
                        if (i <= chars.length-2 && chars[i+1][j] == '*') addEdge(graph, o, o+m, 1);//g[o][t] = (o+m);
                    }
                    ++o;
                }
            }
            int flow = 0;
            for (int i = 0; i < graph.length-1; i++) {
                t = maxFlow(graph, i, i+1);
                flow += t;
                System.out.println(t);
            }
            System.out.println(flow);
            System.out.println("mf: " + maxFlow(graph, 0, graph.length-1));
            int d = flow/2;
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


//        addEdge(graph, 0, 1, 1);
//        addEdge(graph, 1, 0, 1);
//        addEdge(graph, 1, 2, 1);
//        addEdge(graph, 2, 1, 1);
//        addEdge(graph, 2, 3, 1);
//        addEdge(graph, 3, 2, 1);
//        addEdge(graph, 3, 4, 1);
//        addEdge(graph, 4, 3, 1);
//        int flow = 0;
//        for (int i = 0; i < graph.length-1; i++) {
//            flow += maxFlow(graph, i, i+1);
//        }
//
//        System.out.println(flow);
//    }
}