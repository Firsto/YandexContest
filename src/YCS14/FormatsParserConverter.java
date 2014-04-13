package YCS14;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Razor
 * Date: 09.04.14
 * Time: 20:10
 */
public class FormatsParserConverter {
    static HashMap<Integer, FileKeeper> fileSystem = new HashMap<Integer, FileKeeper>();
    public static void main(String[] args) {

        String inputFormat = "";
        String outputFormat = "";
        String[] fileTree = {""};
        int stringsCount = 0;

        BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
        try {
            inputFormat = rdr.readLine();
            outputFormat = rdr.readLine();
            if (!inputFormat.equals("xml")) {
                stringsCount = Integer.parseInt(rdr.readLine());
                fileTree = new String[stringsCount];
                for (int i = 0; i < stringsCount; i++) {
                    fileTree[i] = rdr.readLine();
                }
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
//        HashMap<Integer, FileKeeper> fileSystem = new HashMap<Integer, FileKeeper>();
        for (FileKeeper fileKeeper : fileList) {
//            System.out.println(fileKeeper.initialInfo);
            fileKeeper.initialize();
            fileSystem.put(fileKeeper.id, fileKeeper);
        }

        // Parents detector
        for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
            FileKeeper fk = fileKeeperEntry.getValue();
            for (FileKeeper keeper : fileList) {
                if (fk.parentName != null && fk.parentName.equals(keeper.name) && (fk.loadLevel == keeper.loadLevel+1)) {
                    fk.parentId = keeper.id;
                    break;
                } else if (fk.parentName == null) {
                    fk.parentId = -1;
                }
            }
        }
        for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
            FileKeeper fk = fileKeeperEntry.getValue();
            for (FileKeeper keeper : fileList) {
                if (fk.id == keeper.parentId) {
                    fk.type = FileKeeper.Type.DIR;
                    break;
                } else {
                    fk.type = FileKeeper.Type.FILE;
                }
            }

//            System.out.println("NAME: " + fk.name + " / TYPE: " + fk.type.toString() + " / PARENT ID: " + fk.parentId);
        }

        // OUTPUT FIND
        if (outputFormat.equals("find")) {
            System.out.println(fileSystem.size());
            for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
                int id = fileKeeperEntry.getValue().id;
                String fullName = "";
                while (fileSystem.get(id).parentName != null) {
                    FileKeeper fk = fileSystem.get(id);
                    if (fk.parentName != null) {
                        fullName = fk.parentName + "/" + fullName;
                        id = fk.parentId;
                    }
                }
                System.out.println(fullName + fileKeeperEntry.getValue().name + " " + fileKeeperEntry.getKey());
            }
        }

        // OUTPUT PYTHON
        if (outputFormat.equals("python")) {
            System.out.println(fileSystem.size());
            for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
                for (int i = 0; i < fileKeeperEntry.getValue().loadLevel; i++) {
                    System.out.print("    ");
                }
                System.out.println(fileKeeperEntry.getValue().name + " " + fileKeeperEntry.getKey());
            }
        }

        // OUTPUT ACM1
        if (outputFormat.equals("acm1")) {
            System.out.println(fileSystem.size());
            for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
                System.out.println(fileKeeperEntry.getValue().name + " " + fileKeeperEntry.getKey());
            }
            for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
                int child = 0;
                String children = "";
                for (Map.Entry<Integer, FileKeeper> keeperEntry : fileSystem.entrySet()) {
                    if (fileKeeperEntry.getValue().id == keeperEntry.getValue().parentId) {
                        child++;
                        children += " " + keeperEntry.getValue().id;
                    }
                }
                System.out.println(child + children);
            }
        }

        // OUTPUT ACM2
        if (outputFormat.equals("acm2")) {
            System.out.println(fileSystem.size());
            for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
                System.out.println(fileKeeperEntry.getValue().name + " " + fileKeeperEntry.getKey());
            }
            for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
                System.out.println(fileKeeperEntry.getValue().parentId);
            }
        }

        // OUTPUT ACM3
        if (outputFormat.equals("acm3")) {
            System.out.println(fileSystem.size());
            for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
                System.out.println(fileKeeperEntry.getValue().name + " " + fileKeeperEntry.getKey());
            }
            for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
                if (fileKeeperEntry.getValue().parentId == -1) continue;
                System.out.println(fileKeeperEntry.getValue().parentId + " " + fileKeeperEntry.getValue().id);
            }
        }

        // OUTPUT XML
        if (outputFormat.equals("xml")) {
//            for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
//                String name = fileKeeperEntry.getValue().name;
//                int id = fileKeeperEntry.getValue().id;
//                for (int i = 0; i < fileKeeperEntry.getValue().loadLevel; i++) {
//                    System.out.print("  ");
//                }
//                if (fileKeeperEntry.getValue().type == FileKeeper.Type.DIR) {
//                    System.out.println("<dir name='" + name + "' id='" + id + "'>");
//                } else {
//                    System.out.println("<file name='" + name + "' id='" + id + "'/>");
//                }
//            }
            xmlOut(-1);
        }
    }

    static void xmlOut(int parentId) {
        for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
            if (fileKeeperEntry.getValue().parentId == parentId) {
                String name = fileKeeperEntry.getValue().name;
                int id = fileKeeperEntry.getValue().id;
                int level = fileKeeperEntry.getValue().loadLevel;
                for (int i = 0; i < level; i++) {
                    System.out.print("  ");
                }

                if (fileKeeperEntry.getValue().type == FileKeeper.Type.FILE) {
                    System.out.println("<file name='" + name + "' id='" + id + "'/>");
                }
                if (fileKeeperEntry.getValue().type == FileKeeper.Type.DIR) {
                    System.out.println("<dir name='" + name + "' id='" + id + "'>");
                    xmlOut(fileKeeperEntry.getKey());
                    for (int i = 0; i < level; i++) {
                        System.out.print("  ");
                    }
                    System.out.println("</dir>");
                }
            }
        }
    }

    static class FileKeeper {
        enum Format {FIND, PYTHON, ACM1, ACM2, ACM3, XML}
        enum Type {FILE, DIR}
        String initialInfo = "";
        Format initialFormat;
        Type type;
        String name;
        String parentName;
        String parentFullName;
        int id;
        int parentId;
        int loadLevel;
        boolean xmlDirTagClose = false;

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
            cuted = cuted[0].split("/");
            if (cuted.length > 0) {
                this.loadLevel = cuted.length-1;
                this.parentFullName = "";
                for (int i = 0; i < cuted.length-1; i++) {
                    this.parentFullName += cuted[i] + "/";
                }
            }
            this.name = cuted[cuted.length-1];
            if (cuted.length > 1) this.parentName = cuted[cuted.length - 2];
//            System.out.println(this.name + " " + this.id + "   " + this.parentName + "   " + this.parentFullName + "  " + this.loadLevel);
        }
    }

}
