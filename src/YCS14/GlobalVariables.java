package YCS14; /**
 * User: Razor
 * Date: 03.05.14
 * Time: 2:03
 */

import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Logger;

public class GlobalVariables {

    public static void main(String[] args) throws Exception {
        File file = new File("runs.7z");
        extract(file,"C:\\Users\\Razor\\IdeaProjects\\YandexContest\\src\\YCS14\\runs");
    }

    private static Logger logger = Logger.getLogger(GlobalVariables.class.getCanonicalName());
    public static void extract(File file, String extractPath) throws Exception {
        ISevenZipInArchive inArchive = null;
        RandomAccessFile randomAccessFile = null;
        randomAccessFile = new RandomAccessFile(file, "r");
        inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
        inArchive.extract(null, false, new MyExtractCallback(inArchive, extractPath));
        if (inArchive != null) {
            inArchive.close();
        }
        if (randomAccessFile != null) {
            randomAccessFile.close();
        }
    }
    public static class MyExtractCallback implements IArchiveExtractCallback {
        private final ISevenZipInArchive inArchive;
        private final String extractPath;
        public MyExtractCallback(ISevenZipInArchive inArchive, String extractPath) {
            this.inArchive = inArchive;
            this.extractPath = extractPath;
        }
        @Override
        public ISequentialOutStream getStream(final int index, ExtractAskMode extractAskMode) throws SevenZipException {
            return new ISequentialOutStream() {
                @Override
                public int write(byte[] data) throws SevenZipException {
                    String filePath = inArchive.getStringProperty(index, PropID.PATH);
                    FileOutputStream fos = null;
                    try {
                        File dir = new File(extractPath);
                        File path = new File(extractPath + filePath);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        if (!path.exists()) {
                            path.createNewFile();
                        }
                        fos = new FileOutputStream(path, true);
                        fos.write(data);
                    } catch (IOException e) {
                        logger.severe(e.getLocalizedMessage());
                    } finally {
                        try {
                            if (fos != null) {
                                fos.flush();
                                fos.close();
                            }
                        } catch (IOException e) {
                            logger.severe(e.getLocalizedMessage());
                        }
                    }
                    return data.length;
                }
            };
        }
        @Override
        public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException {
        }
        @Override
        public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException {
        }
        @Override
        public void setCompleted(long completeValue) throws SevenZipException {
        }
        @Override
        public void setTotal(long total) throws SevenZipException {
        }
    }
}