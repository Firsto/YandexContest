package YCS14;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Razor
 * Date: 09.04.14
 * Time: 20:10
 */
public class FormatsParserConverter {

    public static void main(String[] args) {

        String inputFormat = "";
        String outputFormat = "";
        String[] fileTree = {""};
        int stringsCount = 0;

        BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
        try {
            inputFormat = rdr.readLine();
            outputFormat = rdr.readLine();
            stringsCount = Integer.parseInt(rdr.readLine());
            fileTree = new String[stringsCount];
            for (int i = 0; i < stringsCount; i++) {
                fileTree[i] = rdr.readLine();
            }
            rdr.close();
        } catch (IOException e) {
            System.out.println("Input Error");
            e.printStackTrace();
            System.exit(-1);
        }
        ArrayList<FileKeeper> fileList = new ArrayList<>();
        for (String s : fileTree) {
            fileList.add(new FileKeeper(s, inputFormat));
        }
        HashMap<Integer, FileKeeper> fileSystem = new HashMap<Integer, FileKeeper>();
        for (FileKeeper fileKeeper : fileList) {
//            System.out.println(fileKeeper.initialInfo);
            fileKeeper.initialize();
            fileSystem.put(fileKeeper.id, fileKeeper);
        }

    }

    static class FileKeeper {
        enum Format {FIND, PYTHON, ACM1, ACM2, ACM3, XML}
        enum Type {FILE, DIR}
        String initialInfo = "";
        Format initialFormat;
        Type type;
        String name;
        int id;
        int parentId;

        FileKeeper(){
        }

        FileKeeper(String info) {
            initialInfo = info;
        }
        FileKeeper(String info, String format) {
            initialInfo = info;
            this.initialFormat = Format.valueOf(format.toUpperCase());
        }

        void initialize() {
            if (initialFormat != null && initialFormat != Format.XML) {
                id = Integer.parseInt(initialInfo.split(" ")[1]);
            }

            if (initialFormat == Format.FIND) {
                parseFind();
            }
        }

        void parseFind() {
            String[] cuted = initialInfo.split(" ");
        }
    }

}
