package g3.viewchoosephoto.util;

import java.io.File;

public class FileUtils {

    public static boolean fileExists(String filename) {
        File file = new File(filename);
        return file.exists();
    }
}
