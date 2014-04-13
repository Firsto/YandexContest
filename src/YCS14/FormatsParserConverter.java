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
        String[] parentsTree = {""};
        ArrayList<String> xmlList = new ArrayList<>();

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
            if (inputFormat.equals("acm1") || inputFormat.equals("acm2")) {
                parentsTree = new String[stringsCount];
                for (int i = 0; i < stringsCount; i++) {
                    parentsTree[i] = rdr.readLine();
                }
            }

            if (inputFormat.equals("acm3")) {
                parentsTree = new String[stringsCount-1];
                for (int i = 0; i < stringsCount - 1; i++) {
                    parentsTree[i] = rdr.readLine();
                }
            }

            if (inputFormat.equals("xml")) {
                String s = "";
                while ((s = rdr.readLine()) != null ) {
                    xmlList.add(s);
                    if (s.equals("</dir>")) break;
                }
            }

            rdr.close();
        } catch (IOException e) {
            System.out.println("Input Error");
            e.printStackTrace();
            System.exit(-1);
        }
        ArrayList<FileKeeper> fileList = new ArrayList<>();

        if (!inputFormat.equals("xml")) {
            for (String s : fileTree) {
                fileList.add(new FileKeeper(s, inputFormat));
            }
//        HashMap<Integer, FileKeeper> fileSystem = new HashMap<Integer, FileKeeper>();
            for (FileKeeper fileKeeper : fileList) {
//            System.out.println(fileKeeper.initialInfo);
                fileKeeper.initialize();
                fileSystem.put(fileKeeper.id, fileKeeper);
            }
        }

        if (inputFormat.equals("xml")) {
//            StringBuilder sb = new StringBuilder();
//            for (String s : xmlList) {
//                sb.append(s);
//            }
//            parseTag(sb.toString(),"dir");
//            System.exit(0);

//            if (initialInfo.startsWith("    ")) {
//                int i = 0;
//                char[] chars = initialInfo.toCharArray();
//                while (chars[i] == ' ') {
//                    i++;
//                }
//                this.loadLevel = i/4;
//            }
//            String[] cuted = initialInfo.trim().split(" ");
//            this.name = cuted[0];

            for (String s : xmlList) {
                if (s.contains("<dir") || s.contains("<file")){
                    int level = 0;
                    if (s.startsWith("  ")) {
                        int i = 0;
                        char[] chars = s.toCharArray();
                        while (chars[i] == ' ') {
                            i++;
                        }
                        level = i/2;
                    }
                    s = s.trim();

                    FileKeeper fk = new FileKeeper();
                    fk.loadLevel = level;

                    if (s.contains("<dir")) {
                        fk.type = FileKeeper.Type.DIR;
//                        s = s.split(" ")[1] + s.split(" ")[2];
//                        System.out.println(s.split("'")[1] + " " + s.split("'")[3]);
                    }
                    if (s.contains("<file")) {
                        fk.type = FileKeeper.Type.FILE;
//                        s = s.split(" ")[1] + s.split(" ")[2];
//                        System.out.println(s.split("'")[1] + " " + s.split("'")[3]);
                    }
                    s = s.split(" ")[1] + s.split(" ")[2];
                    fk.name = s.split("'")[1];
                    fk.id = Integer.parseInt(s.split("'")[3]);
//                    System.out.println(s + " / level " + level);
                    fileSystem.put(fk.id, fk);
                }
            }
//            System.exit(0);
            inputFormat = "python";
        }

        // Parents detector

        if (inputFormat.equals("python")) {
            for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
                String name = fileKeeperEntry.getValue().name;
                int id = fileKeeperEntry.getValue().id;
                int level = fileKeeperEntry.getValue().loadLevel;

                for (Map.Entry<Integer, FileKeeper> keeperEntry : fileSystem.entrySet()) {
//                    if (keeperEntry.getValue().parentName != null) continue;
                    if (keeperEntry.getValue().loadLevel == level+1 && keeperEntry.getValue().id > id) {
                        keeperEntry.getValue().parentName = name;
                        keeperEntry.getValue().parentId = id;
                    }
                }
            }
        }

        if (inputFormat.equals("acm1")) {
            String[] childIds = {""};
            int i = 0;
            for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
                String name = fileKeeperEntry.getValue().name;
                int id = fileKeeperEntry.getValue().id;
                int level = fileKeeperEntry.getValue().loadLevel;

                String parents = parentsTree[i];
                if (parents.length() > 1) {
                    childIds = parents.split(" ");
                    for (int j = 1; j <= Integer.parseInt(childIds[0]); j++) {
                        int childId = Integer.parseInt(childIds[j]);
                        fileSystem.get(childId).parentId = id;
                        fileSystem.get(childId).parentName = name;
//                        fileSystem.get(childId).loadLevel = level + 1;
                    }
                }
//                System.out.println("id " + id + " / level " + fileKeeperEntry.getValue().loadLevel);
                i++;
            }
            for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
                if (fileKeeperEntry.getValue().parentName != null) {
                    int parentId = fileKeeperEntry.getValue().parentId;
                    fileKeeperEntry.getValue().loadLevel += fileSystem.get(parentId).loadLevel + 1;
                }
            }
        }

        if (inputFormat.equals("acm2")) {
            int i = 0;
            for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
                FileKeeper fk = fileKeeperEntry.getValue();
                int parentId = Integer.parseInt(parentsTree[i]);
                fk.parentId = parentId;
                if (parentId != -1) fk.parentName = fileSystem.get(parentId).name;
                i++;
            }
            for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
                if (fileKeeperEntry.getValue().parentName != null) {
                    int parentId = fileKeeperEntry.getValue().parentId;
                    fileKeeperEntry.getValue().loadLevel += fileSystem.get(parentId).loadLevel + 1;
                }
            }
        }

        if (inputFormat.equals("acm3")) {
            for (int i = 0; i < parentsTree.length; i++) {
                int parentId = Integer.parseInt(parentsTree[i].split(" ")[0]);
                int childId = Integer.parseInt(parentsTree[i].split(" ")[1]);
                FileKeeper fk = fileSystem.get(childId);
                fk.parentId = parentId;
                fk.parentName = fileSystem.get(parentId).name;
            }
            for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
                if (fileKeeperEntry.getValue().parentName != null) {
                    int parentId = fileKeeperEntry.getValue().parentId;
                    fileKeeperEntry.getValue().loadLevel += fileSystem.get(parentId).loadLevel + 1;
                }
            }
        }

        for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
            FileKeeper fk = fileKeeperEntry.getValue();

            for (Map.Entry<Integer, FileKeeper> keeperEntry : fileSystem.entrySet()) {
                if (fk.parentName != null && fk.parentName.equals(keeperEntry.getValue().name) && (fk.loadLevel == keeperEntry.getValue().loadLevel+1)) {
                    if (fk.id < keeperEntry.getValue().id) break;
                    fk.parentId = keeperEntry.getValue().id;
                    break;
                } else if (fk.parentName == null) {
                    fk.parentId = -1;
                }
            }
        }
        for (Map.Entry<Integer, FileKeeper> fileKeeperEntry : fileSystem.entrySet()) {
            FileKeeper fk = fileKeeperEntry.getValue();

            for (Map.Entry<Integer, FileKeeper> keeperEntry : fileSystem.entrySet()) {
                if (fk.id == keeperEntry.getValue().parentId) {
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
            xmlOut(-1);
        }
    }

    public static void parseTag(String line, String tag) {
        String tagOpen = "<" + tag;
        String tagClosed = "</" + tag + ">";
        if (tag.equals("file")) tagClosed = "/>";

        int off = line.indexOf(tagOpen);
        int offend = line.indexOf(tagClosed)+tagClosed.length();

        String inline = line.substring(off, offend);
        String subinline = inline.substring(tagOpen.length());
        String nextline = line.substring(offend);

        while (subinline.contains(tagOpen)) {
            int suboffend = nextline.indexOf(tagClosed)+tagClosed.length();
            subinline = subinline.substring(subinline.indexOf(tagOpen)+tagOpen.length()) + nextline.substring(0, suboffend);
            inline += nextline.substring(0, suboffend);
            nextline = nextline.substring(suboffend);
        }
        System.out.println(inline);
        nextline = line.substring(line.indexOf(inline)+inline.length());
//        System.out.println(nextline);
        subinline = inline.substring(tagOpen.length(), inline.lastIndexOf(tagClosed));
//        System.out.println(subinline);
        if (subinline.contains(tagOpen)) parseTag(subinline, tag);
        if (nextline.contains(tagOpen)) parseTag(nextline, tag);
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
                id = Integer.parseInt(initialInfo.trim().split(" ")[1]);
            }

            if (initialFormat == Format.FIND) {
                parseFind();
            }

            if (initialFormat == Format.PYTHON) {
                parsePython();
            }

            if (initialFormat == Format.ACM1 || initialFormat == Format.ACM2 || initialFormat == Format.ACM3) {
                parseAcm();
            }

            if (initialFormat == Format.XML) {
                parseXml();
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

        void parsePython() {
            if (initialInfo.startsWith("    ")) {
                int i = 0;
                char[] chars = initialInfo.toCharArray();
                while (chars[i] == ' ') {
                    i++;
                }
                this.loadLevel = i/4;
            }
            String[] cuted = initialInfo.trim().split(" ");
            this.name = cuted[0];
        }

        void parseAcm() {
            String[] cuted = initialInfo.trim().split(" ");
            this.name = cuted[0];
        }

        void parseXml() {

        }
    }

}
