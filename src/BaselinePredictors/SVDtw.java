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
import java.util.Arrays;
import java.util.WeakHashMap;

public class SVDtw {

// thx to http://habrahabr.ru/company/surfingbird/blog/141959/

    double lambda1 = 0.0;
    double lambda2 = 0.015;
    double eta = 0.01;

    //    int[][] l;
    WeakHashMap<Integer,Integer> ratings = new WeakHashMap<>();
//    int[] cust;
//    int[] rate;

    public static void main(String[] args) {
        SVDtw svd = new SVDtw();
        BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
        String str = "";
        int k=10, u=3, m=3, d=5, t=4, ui=0,mi=0;
//        String[] rec = new String[0];
        int[] ut=new int[0],mt=new int[0];
        try {
            str = rdr.readLine();
            k = Integer.parseInt(str.split(" ")[0]);
            u = Integer.parseInt(str.split(" ")[1]);
            m = Integer.parseInt(str.split(" ")[2]);
            d = Integer.parseInt(str.split(" ")[3]);
            t = Integer.parseInt(str.split(" ")[4]);
//            svd.l = new int[u][m];
            svd.b_u = new double[u];
            svd.b_v = new double[m];
            svd.u_f = new double[u];
            svd.v_f = new double[m];
            Arrays.fill(svd.u_f, 0.1);
            Arrays.fill(svd.v_f, 0.1);
//            svd.cust = new int[d];
//            svd.rate = new int[d];
            svd.total = d;
//            rec = new String[t];
            ut = new int[t];
            mt = new int[t];
            int value;
            for (int i = 0; i < d; i++) {
                str = rdr.readLine();
                ui = Integer.parseInt(str.split(" ")[0]);
                mi = Integer.parseInt(str.split(" ")[1]);
                value = Integer.parseInt(str.split(" ")[2]);
//                svd.l[ui][mi] = value;
                svd.ratings.put(ui*10000+mi,value);
//                svd.cust[i]=ui*10000+mi;
//                svd.rate[i]=value;
            }
            for (int i = 0; i < t; i++) {
                str = rdr.readLine();
//                rec[i] = str;
                ut[i] = Integer.parseInt(str.split(" ")[0]);
                mt[i] = Integer.parseInt(str.split(" ")[1]);
            }
            rdr.close();
        } catch (IOException e) {
            System.out.print(-1);
            System.exit(0);
        }

        svd.learn();
//        svd.print_all();
        double result = 0;

        for (int i = 0; i < t; i++) {
            ui = ut[i];
            mi = mt[i];

//            result = svd.mu + svd.b_u[ui] + svd.b_v[mi] + svd.u_f[ui][0]*svd.u_f[ui][1]*svd.u_f[ui][2] + svd.v_f[mi][0]*svd.v_f[mi][1]*svd.v_f[mi][2];
//            result = svd.mu + svd.b_u[ui] + svd.b_v[mi] + svd.u_f[ui][0]*svd.u_f[ui][1]*svd.u_f[ui][2] + svd.v_f[mi][0]*svd.v_f[mi][1]*svd.v_f[mi][2];
            double uf,vf,fr=0;

                fr += svd.u_f[ui]*svd.v_f[mi];


            result = fr;
//            result = svd.mu + svd.b_u[ui] + svd.b_v[mi] + fr;
//            double result = svd.mu + svd.b_u[ui] + svd.b_v[mi];
            result=svd.mu-result;
            System.out.println(result>10?10:result);
        }
    }



    double mu = 0;
    double[] b_u;
    double[] b_v;
    double[] u_f;
    double[] v_f;
    double uf=0.1,mf=0.1;
    int total;

    int iter_no = 0;
    double err = 0;
    double rmse = 1;
    double old_rmse = 0;
    double threshold = 0.01;

    void learn() {
//    learning
        int count = 0; int rt=0;
        while (Math.abs(old_rmse - rmse) > 0.00001 ) {
//            if (iter_no>300)break;
            old_rmse = rmse;
            rmse = 0;
            for (int u = 0; u < b_u.length; ++u) {
                for (int v = 0; v < b_v.length; ++v) {
//                    if(u_f[u]==0)u_f[u]=0.1;
//                    if(v_f[v]==0)v_f[v]=0.1;
                    if (ratings.containsKey(u*10000+v)) rt = ratings.get(u*10000+v); else rt=0;
                    err = rt - (mu + b_u[u] + b_v[v] + u_f[u] * v_f[v]);
//                    err = rt - ( b_u[u] + b_v[v] + u_f[u] * v_f[v] );
                    rmse += err * err;
                    //  update predictors
                    mu += eta * err;
                    b_u[u] += eta * (err - lambda2 * b_u[u]);
                    b_v[v] += eta * (err - lambda2 * b_v[v]);

                        u_f[u] += eta * (err * v_f[v] - lambda2 * u_f[u]);
                        v_f[v] += eta * (err * u_f[u] - lambda2 * v_f[v]);
                }
            }
            ++iter_no;
//            System.out.println(iter_no);
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

            print_array(u_f, "  user :");

        System.out.print("Item features:\n");

            print_array(v_f, "  item :");

    }

}
