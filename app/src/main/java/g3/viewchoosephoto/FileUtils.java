package g3.viewchoosephoto;

import java.io.File;

class FileUtils {

    public static boolean fileExists(String filename) {
        File file = new File(filename);
        return file.exists();
    }
}
