package bg.softuni.repository;

import bg.softuni.io.OutputWriter;
import bg.softuni.models.Course;
import bg.softuni.models.Student;
import bg.softuni.staticData.ExceptionMessages;
import bg.softuni.staticData.SessionData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudentsRepository {

    private boolean isDataInitialized = false;
    private HashMap<String, HashMap<String, ArrayList<Integer>>> studentsByCourse;
    private RepositoryFilter filter;
    private RepositorySorter sorter;
    private LinkedHashMap<String, Course> courses;
    private LinkedHashMap<String, Student> students;

    public StudentsRepository(RepositoryFilter filter, RepositorySorter sorter) {
        this.filter = filter;
        this.sorter = sorter;
    }

    public void loadData(String fileName) throws IOException {
        if (isDataInitialized) {
            OutputWriter.displayException(ExceptionMessages.DATA_ALREADY_INITIALIZED);
            return;
        }

        this.students = new LinkedHashMap<>();
        this.courses = new LinkedHashMap<>();
        this.readData(fileName);
    }

    public void unloadData() {
        if (!this.isDataInitialized) {
            OutputWriter.displayException(ExceptionMessages.DATA_NOT_INITIALIZED);
        }

        this.students = null;
        this.courses = null;
        this.isDataInitialized = false;
    }

    private void readData(String fileName) throws IOException {
        String regex = "([A-Z][a-zA-Z#\\+]*_[A-Z][a-z]{2}_\\d{4})\\s+([A-Za-z]+\\d{2}_\\d{2,4})\\s([\\s0-9]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;

        String path = SessionData.currentPath + "\\" + fileName;
        List<String> lines = Files.readAllLines(Paths.get(path));
        int lineIndex = 0;

        for (String line : lines) {
            lineIndex++;
            matcher = pattern.matcher(line);

            if (!line.isEmpty() && matcher.find()) {
                String courseName = matcher.group(1);
                String studentName = matcher.group(2);
                String scoresStr = matcher.group(3);

                try {
                    String[] splitScores = scoresStr.split("\\s+");
                    int[] scores = new int[scoresStr.length()];
                    for (int i = 0; i < splitScores.length; i++) {
                        scores[i] = Integer.parseInt(splitScores[i]);
                    }

                    if (Arrays.stream(scores).anyMatch(score -> score > 100 || score < 0)) {
                        OutputWriter.displayException(ExceptionMessages.INVALID_SCORE);
                        continue;
                    }

                    if (scores.length > Course.NUMBER_OF_TASKS_ON_EXAM) {
                        OutputWriter.displayException(ExceptionMessages.INVALID_NUMBER_OF_SCORES);
                        continue;
                    }

                    if(!this.students.containsKey(studentName)) {
                        this.students.put(studentName, new Student(studentName));
                    }

                    if(!this.courses.containsKey(courseName)) {
                        this.courses.put(courseName, new Course(courseName));
                    }

                    Course course = this.courses.get(courseName);
                    Student student = this.students.get(studentName);
                    student.enrollInCourse(course);
                    student.setMarkOnCourse(courseName, scores);
                    course.enrollStudent(student);
                } catch (NumberFormatException nfe) {
                    OutputWriter.displayException(nfe.getMessage() + " at line: " + lineIndex);
                }
            }

        }

        isDataInitialized = true;
        OutputWriter.writeMessageOnNewLine("Data read.");
    }

    public void printFilteredStudents(String course, String filter, Integer numberOfStudents) {
        if (! isQueryForCoursePossible(course)) {
            return;
        }

        if (numberOfStudents == null) {
            numberOfStudents = studentsByCourse.get(course).size();
        }

        RepositoryFilter.printFilteredStudents(studentsByCourse.get(course), filter, numberOfStudents);
    }

    public void printOrderedStudents(String course, String compareType, Integer numberOfStudents) {
        if (!isQueryForCoursePossible(course)) {
            return;
        }

        if (numberOfStudents == null) {
            numberOfStudents = studentsByCourse.get(course).size();
        }

        RepositorySorter.printSortedStudents(studentsByCourse.get(course), compareType, numberOfStudents);
    }

    public void getStudentMarksInCourse(String course, String student) {
        if (!isQueryForStudentPossible(course, student)) {
            return;
        }

        ArrayList<Integer> marks = studentsByCourse.get(course).get(student);
        OutputWriter.printStudent(student, marks);
    }

    public void getStudentsByCourse(String course) {
        if (!isQueryForCoursePossible(course)) {
            return;
        }

        OutputWriter.writeMessageOnNewLine(course + ":");
        for (Map.Entry<String, ArrayList<Integer>> student : studentsByCourse.get(course).entrySet()) {
            OutputWriter.printStudent(student.getKey(), student.getValue());
        }
    }

    private boolean isQueryForCoursePossible(String courseName) {
        if (!isDataInitialized) {
            OutputWriter.displayException(ExceptionMessages.DATA_NOT_INITIALIZED);
            return false;
        }

        if (!studentsByCourse.containsKey(courseName)) {
            OutputWriter.displayException(ExceptionMessages.NON_EXISTING_COURSE);
            return false;
        }

        return true;
    }

    private boolean isQueryForStudentPossible(String courseName, String studentName) {
        if (!isQueryForCoursePossible(courseName)) {
            return false;
        }

        if (!studentsByCourse.get(courseName).containsKey(studentName)) {
            OutputWriter.displayException(ExceptionMessages.NON_EXISTING_STUDENT);
            return false;
        }

        return true;
    }

    public void filterAndTake(String courseName, String filter) {
        int studentsToTake = studentsByCourse.get(courseName).size();
        filterAndTake(courseName, filter, studentsToTake);
    }

    public void filterAndTake(
            String courseName, String filter, int studentsToTake) {
        if (!isQueryForCoursePossible(courseName)) {
            return;
        }

        RepositoryFilter.printFilteredStudents(
                studentsByCourse.get(courseName),
                filter, studentsToTake);
    }

    public void orderAndTake(
            String courseName, String orderType, int studentsToTake) {
        if (!isQueryForCoursePossible(courseName)) {
            return;
        }

        RepositorySorter.printSortedStudents(
                studentsByCourse.get(courseName),
                orderType, studentsToTake);
    }

    public void orderAndTake(String courseName, String orderType) {
        int studentsToTake = studentsByCourse.get(courseName).size();
        orderAndTake(courseName, orderType, studentsToTake);
    }
}
