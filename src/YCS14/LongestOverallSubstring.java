package YCS14;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Razor
 * Date: 14.04.14
 * Time: 0:00
 */
public class LongestOverallSubstring {

    public static void main(String[] args) {
        int count = 0;
        ArrayList<String> strings = new ArrayList<>();
        BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
        try {
            count = Integer.parseInt(rdr.readLine());
            for (int i = 0; i < count; i++) {
                strings.add(rdr.readLine());
            }
            rdr.close();
        } catch (IOException e) {
            System.out.println("Input Error");
            e.printStackTrace();
            System.exit(-1);
        }
        String s = "";
        String str[] = new String[count];
        String stt[] = new String[count];
        int o = 0;
        for (String string : strings) {
            str[o] = string;
            stt[o] = string;
            o++;
        }
        for (int i =1; i<=count-1;i++){

            if(str[i-1].length() <= str[i].length()){
                s=str[i-1];
                str[i]=str[i-1];
                str[i-1]=s;
            }
        }
        String shortest = str[count-1];
        for(int i=shortest.length()-1;i>=0;i--){
            for(int j=i; j<=shortest.length()-1;j++){
                String st=shortest.substring(j-i,j+1);
                int findcount=0;
                for(int k=0; k<=str.length-1;k++){
                    if(stt[k].contains(st)){
                        findcount++;
                    }
                }
                if(findcount==count){
                    System.out.println(st);
                    System.exit(0);
                }
            }
        }
    }
}
