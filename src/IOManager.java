import java.io.File;
import java.util.LinkedList;

/**
 * Created by pivanov on 22.1.2017 г..
 */
public class IOManager {

    public static void traverseDirectory(String path) {
        LinkedList<File> subFolders = new LinkedList<File>();
        File root = new File(path);

        try {
            subFolders.add(root);

            while (subFolders.size() != 0) {
                File currentFolder = subFolders.removeFirst();

                if (currentFolder.listFiles() != null) {
                    for (File file : currentFolder.listFiles()) {
                        if (file.isDirectory()) {
                            subFolders.offer(file); //added by me
                        }
                        System.out.println(file.toString());
                    }
                }
                System.out.println(currentFolder.toString());
            }

        } catch (Exception e) {
            System.err.println("Access denied"); // added by me
        }
    }

    //public static void main(String[] args) {
    //        traverseDirectory("C:\\Users\\pivanov\\Dropbox\\Personal\\SoftUni\\");
    //}

}
