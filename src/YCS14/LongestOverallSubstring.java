package YCS14;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: Razor
 * Date: 14.04.14
 * Time: 0:00
 */
public class LongestOverallSubstring {

    public static void main(String[] args) {

        /*

7
aslkdsvxcvicparampamibyzxycbiouuidksdafdfasdfasddfsjhsdgsdgui
sdhakjdliidsuboidasuobuparampamiouboifuoibfodibdubias
adfoianpdozyucviparampamiovpopozoudvsiopofid
zjxclbyzparampamipbuyeiorupoiugrogpewoififeuoi
ipabdofnpouioaupoigaopigupoidaupoparampamidugaogupio
adfnadpufaparampamiduniadfiojzlxcjvlkzjxcv
fdiuosdoiiodfhiuodhuiodfuhodfparampamiuhio

         */

        int count = 0;
        String strings[] = {""};

        BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
        try {
            count = Integer.parseInt(rdr.readLine());
            strings = new String[count];
            for (int i = 0; i < count; i++) {
                strings[i] = rdr.readLine();
            }
            rdr.close();
        } catch (IOException e) {
            System.out.println("Input Error");
            e.printStackTrace();
            System.exit(-1);
        }
//        strings_temp = strings.clone();
        String s = "";

        for (int i = 0; i < count - 1; i++) {
            if (strings[i].length() <= strings[i+1].length()) {
                s = strings[i];
            }
        }

//        System.out.println(Arrays.toString(strings));
//        System.out.println(s);
        for (int i = s.length() - 1; i >= 0; i--) {
            for (int j = i; j <= s.length() - 1; j++) {
                String substring = s.substring(j - i, j + 1);
                int findcount = 0;
                for (int k = 0; k <= strings.length - 1; k++) {
                    if (strings[k].contains(substring)) {
                        findcount++;
                    }
                }
                if (findcount == count) {
                    System.out.println(substring);
                    System.exit(0);
                }
            }
        }
    }
}
