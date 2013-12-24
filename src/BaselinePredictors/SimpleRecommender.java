package BaselinePredictors;

import java.util.Arrays;

/**
 * User: Razor
 * Date: 08.12.13
 * Time: 16:28
 */
public class SimpleRecommender {

    public static void main(String[] args) {

        int k=10, u=3, m=3, d=5, t=4;
        double[][] model = new double[u][m];
//        for (int i = 0; i < u; i++) {
//            for (int j = 0; j < m; j++) {
//
//            }
//        }
        model[0][0] = 9;
        model[0][1] = 8;
        model[1][1] = 4;
        model[1][2] = 6;
        model[2][2] = 7;
        double bu, bm;
        double[] am = new double[m];
        double[] au = new double[u];
//        System.out.println(model[0][2]);
        int[] ur = new int[m];
        int[] mr = new int[u];
        double rt;
        for (int i = 0; i < m; i++) {
            ur[i] = 0;
            for (int j = 0; j < u; j++) {
                rt = model[j][i];
                if (rt != 0) {
                    am[i] += rt;
                    ur[i]++;
                }
            }
            am[i] = am[i]/ur[i];
            System.out.print(am[i] + " ");
        }
        System.out.println();
        for (int i = 0; i < u; i++) {
            mr[i] = 0;
            for (int j = 0; j < m; j++) {
                rt = model[i][j];
                if (rt != 0) {
                    au[i] += rt;
                    mr[i]++;
                }
            }
            au[i] = au[i]/mr[i];
            System.out.print(au[i] + " ");
        }
        System.out.println();
        System.out.println(Arrays.toString(ur));
        System.out.println(Arrays.toString(mr));
        double[][] uuw = new double[u][u];
        double[] uw = new double[u];
        double[] mw = new double[m];
        for (int i = 0; i < u; i++) {
            for (int j = 0; j < u; j++) {
                uuw[i][j] = getPearsonCorrelation(model[i], model[j]);
                System.out.print(uuw[i][j] + " ");
            }
            System.out.println();
        }
        for (int i = 0; i < m; i++) {
            mw[i] = getPearsonCorrelation(model[i], am);
            System.out.print(mw[i] + " ");
        }
        System.out.println();
        for (int i = 0; i < u; i++) {
            uw[i] = getPearsonCorrelation(model[i], au);
            System.out.print(uw[i] + " ");
        }
        System.out.println();
        System.out.println(uw[1]*mw[0]);
        System.out.println(uw[2]*mw[1]);
    }

    public static double getPearsonCorrelation(double[] scores1,double[] scores2){
        double result = 0;
        double sum_sq_x = 0;
        double sum_sq_y = 0;
        double sum_coproduct = 0;
        double mean_x = scores1[0];
        double mean_y = scores2[0];
        for(int i=2;i<scores1.length+1;i+=1){
            double sweep =Double.valueOf(i-1)/i;
            double delta_x = scores1[i-1]-mean_x;
            double delta_y = scores2[i-1]-mean_y;
            sum_sq_x += delta_x * delta_x * sweep;
            sum_sq_y += delta_y * delta_y * sweep;
            sum_coproduct += delta_x * delta_y * sweep;
            mean_x += delta_x / i;
            mean_y += delta_y / i;
        }
        double pop_sd_x = (double) Math.sqrt(sum_sq_x/scores1.length);
        double pop_sd_y = (double) Math.sqrt(sum_sq_y/scores1.length);
        double cov_x_y = sum_coproduct / scores1.length;
        result = cov_x_y / (pop_sd_x*pop_sd_y);
        return result;
    }

    static double userW (int ia, int ja, double ri, double rj ) {

        return 0;
    }

    static class User {

    }

    static class Item {

    }
}
