package BaselinePredictors;

/*

10 3 3 5 4
0 0 9
0 1 8
1 1 4
1 2 6
2 2 7
0 2
1 0
2 0
2 1

*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.WeakHashMap;

public class SVD {

// thx to http://habrahabr.ru/company/surfingbird/blog/141959/

    double lambda1 = 0.0;
    double lambda2 = 0.015;
    double eta = 0.01;
    int features = 10;

    WeakHashMap<Integer,Integer> ratings = new WeakHashMap<>();
//    double[][] l;

    public static void main(String[] args) {
        SVD svd = new SVD();
        BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
        String str = "";
        int k=10, u=3, m=3, d=5, t=4, ui=0,mi=0;
        String[] rec = new String[0];
        try {
            str = rdr.readLine();
            k = Integer.parseInt(str.split(" ")[0]);
            u = Integer.parseInt(str.split(" ")[1]);
            m = Integer.parseInt(str.split(" ")[2]);
            d = Integer.parseInt(str.split(" ")[3]);
            t = Integer.parseInt(str.split(" ")[4]);
//            svd.l = new double[u][m];
            svd.b_u = new double[u];
            svd.b_v = new double[m];
            svd.u_f = new double[u][svd.features];
            for (int i=0; i<u; ++i) {
                for (int f=0; f < svd.features; ++f) {
                    svd.u_f[i][f] = 0.1;
                }
            }
            svd.v_f = new double[m][svd.features];
            for (int i=0; i<m; ++i) {
                for (int f=0; f < svd.features; ++f) {
                    svd.v_f[i][f] = 0.05 * f ;
                }
            }
            svd.total = d;
            rec = new String[t];
            int value;
            for (int i = 0; i < d; i++) {
                str = rdr.readLine();
                ui = Integer.parseInt(str.split(" ")[0]);
                mi = Integer.parseInt(str.split(" ")[1]);
                value = Integer.parseInt(str.split(" ")[2]);
//                svd.l[ui][mi] = value;
                svd.ratings.put(ui*10000+mi,value);
            }
            for (int i = 0; i < t; i++) {
                str = rdr.readLine();
                rec[i] = str;
//                ui = Integer.parseInt(str.split(" ")[0]);
//                mi = Integer.parseInt(str.split(" ")[1]);
            }
        } catch (IOException e) {
            System.out.print(-1);
            System.exit(0);
        }

        svd.learn();
//        svd.print_all();
        double result = 0;

        for (int i = 0; i < t; i++) {
            ui = Integer.parseInt(rec[i].split(" ")[0]);
            mi = Integer.parseInt(rec[i].split(" ")[1]);

//            result = svd.mu + svd.b_u[ui] + svd.b_v[mi] + svd.u_f[ui][0]*svd.u_f[ui][1]*svd.u_f[ui][2] + svd.v_f[mi][0]*svd.v_f[mi][1]*svd.v_f[mi][2];
//            result = svd.mu + svd.b_u[ui] + svd.b_v[mi] + svd.u_f[ui][0]*svd.u_f[ui][1]*svd.u_f[ui][2] + svd.v_f[mi][0]*svd.v_f[mi][1]*svd.v_f[mi][2];
            double uf,vf,fr=0;
            for (int j = 0; j < svd.features; j++) {
                fr += svd.u_f[ui][j]*svd.v_f[mi][j];
            }
//            System.out.println("fr = " + fr);
//            result = fr;
//            result = svd.mu + svd.b_u[ui] + svd.b_v[mi] ;
//            result = svd.mu + svd.b_u[ui] + svd.b_v[mi] + fr ;
            result = svd.mu + svd.b_u[ui] + svd.b_v[mi] - fr;
//            result=svd.mu-result;
            System.out.println(result>10?10:result);
        }
    }

    double dot(double[] v1, double[] v2) {
        double res = 0;
        for(int i=0; i < features; ++i) {
            res += v1[i] * v2[i];
        }
        return res;
    }


    double mu = 0;
    double[] b_u;
    double[] b_v;
    double[][] u_f;
    double[][] v_f;
    int total;

    int iter_no = 0;
    double err = 0;
    double rmse = 1;
    double old_rmse = 0;
    double threshold = 0.01;
    double ut,vt;
    void learn() {
//    learning
        int count = 0;           int rt=0;
        while (Math.abs(old_rmse - rmse) > 0.00001 ) {
            old_rmse = rmse;
            rmse = 0;
            for (int u = 0; u < b_u.length; ++u) {
                for (int v = 0; v < b_v.length; ++v) {

//                    err = l[u][v] - (mu + b_u[u] + b_v[v] + dot(u_f[u] , v_f[v]) );
//                    err = l[u][v] - ( b_u[u] + b_v[v] + dot(u_f[u] , v_f[v]) );
                    if (ratings.containsKey(u*10000+v)) rt = ratings.get(u*10000+v); else rt=0;
                    err = rt - ( dot(u_f[u] , v_f[v]) );
                    rmse += err * err;
    //  update predictors
                    mu += eta * err;
                    b_u[u] += eta * (err - lambda2 * b_u[u]);
                    b_v[v] += eta * (err - lambda2 * b_v[v]);

                    for (int i = 0; i < features; i++) {
                        ut = u_f[u][i]; vt = v_f[v][i];
                        u_f[u][i] += eta * (err * vt - lambda2 * ut);
                        v_f[v][i] += eta * (err * ut - lambda2 * vt);
                    }
                }
            }
            ++iter_no;

            rmse = Math.sqrt(rmse / total);
//            System.out.print("Iteration iter_no:"+(++count)+"\tRMSE=" + rmse + "\n");

            if (rmse > old_rmse - threshold) {
                eta = eta * 0.66;
                threshold = threshold * 0.5;
            }
        }
    }


    void print_array(double[] arr, String prefix) {

        System.out.print(prefix+"\t");
        for(double x : arr) {
            System.out.printf("%.4f\t", x);
        }
        System.out.print("\n");
    }


    void print_all() {
        System.out.print("       mu:\t" + mu + "\n");
        print_array(b_u, "User base:");
        print_array(b_v, "Item base:");
        System.out.print("User features:\n");
        for(int u=0; u < u_f.length; ++u) {
            print_array(u_f[u], "  user "+u+":");
        }
        System.out.print("Item features:\n");
        for(int v=0; v < v_f.length; ++v) {
            print_array(v_f[v], "  item "+v+":");
        }
    }

}
