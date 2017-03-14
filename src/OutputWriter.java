import java.util.List;

/**
 * Created by pivanov on 22.1.2017 г..
 */
public class OutputWriter {

    public static void writeMessage(String message) {

    }

    public static void writeMessageOnNewLine(String message) {

    }

    public static void writeEmptyLine() {

    }

    public static void displayException(String message) {

    }

    public static void printStudent(String name, List<Integer> marks) {
        String output = String.format("%s - %s", name, marks.toString());
        OutputWriter.writeMessageOnNewLine(output);
    }

}
