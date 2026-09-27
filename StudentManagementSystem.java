import java.io.*;
import java.util.*;

/**
 * Student Management System
 * Demonstrates: methods & overloading, ArrayList, HashMap, HashSet,
 * custom exceptions, try-catch-finally, and file reading/writing.
 */
public class StudentManagementSystem {

    // ---------- Student model ----------
    static class Student {
        int id;
        String name;
        double gpa;

        Student(int id, String name, double gpa) {
            this.id = id;
            this.name = name;
            this.gpa = gpa;
        }

        @Override
        public String toString() {
            return "ID: " + id + " | Name: " + name + " | GPA: " + gpa;
        }

        // Convert to a single line for file storage: id,name,gpa
        String toFileLine() {
            return id + "," + name + "," + gpa;
        }

        static Student fromFileLine(String line) {
            String[] parts = line.split(",");
            return new Student(Integer.parseInt(parts[0]), parts[1], Double.parseDouble(parts[2]));
        }
    }

    // ---------- Custom exception ----------
    static class StudentNotFoundException extends Exception {
        StudentNotFoundException(String message) {
            super(message);
        }
    }

    // ---------- Data structures ----------
    private final ArrayList<Student> studentList = new ArrayList<>();      // ordered storage
    private final HashMap<Integer, Student> studentById = new HashMap<>(); // fast ID lookup
    private final HashSet<Integer> usedIds = new HashSet<>();              // enforce unique IDs

    private static final String FILE_NAME = "students.txt";

    // ---------- Core methods ----------

    // Add a new student
    void addStudent(int id, String name, double gpa) {
        if (usedIds.contains(id)) {
            System.out.println("A student with ID " + id + " already exists.");
            return;
        }
        Student s = new Student(id, name, gpa);
        studentList.add(s);
        studentById.put(id, s);
        usedIds.add(id);
        System.out.println("Added: " + s);
    }

    // Overloaded search: by ID
    Student search(int id) throws StudentNotFoundException {
        Student s = studentById.get(id);
        if (s == null) {
            throw new StudentNotFoundException("No student found with ID " + id);
        }
        return s;
    }

    // Overloaded search: by name (case-insensitive, first match)
    Student search(String name) throws StudentNotFoundException {
        for (Student s : studentList) {
            if (s.name.equalsIgnoreCase(name)) {
                return s;
            }
        }
        throw new StudentNotFoundException("No student found with name '" + name + "'");
    }

    // Edit an existing student's name and/or GPA
    void editStudent(int id, String newName, double newGpa) {
        try {
            Student s = search(id);
            s.name = newName;
            s.gpa = newGpa;
            System.out.println("Updated: " + s);
        } catch (StudentNotFoundException e) {
            System.out.println("Edit failed: " + e.getMessage());
        }
    }

    // Delete a student by ID
    void deleteStudent(int id) {
        try {
            Student s = search(id);
            studentList.remove(s);
            studentById.remove(id);
            usedIds.remove(id);
            System.out.println("Deleted student with ID " + id);
        } catch (StudentNotFoundException e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }

    // Print all students
    void listAll() {
        if (studentList.isEmpty()) {
            System.out.println("No students on record.");
            return;
        }
        System.out.println("---- Student Records ----");
        for (Student s : studentList) {
            System.out.println(s);
        }
    }

    // ---------- File I/O ----------

    void saveToFile() {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            for (Student s : studentList) {
                writer.write(s.toFileLine() + System.lineSeparator());
            }
            System.out.println("Saved " + studentList.size() + " records to " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        } finally {
            System.out.println("Save operation finished.");
        }
    }

    void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No saved file found yet (" + FILE_NAME + ").");
            return;
        }
        try (Scanner reader = new Scanner(file)) {
            studentList.clear();
            studentById.clear();
            usedIds.clear();
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if (line.isEmpty()) continue;
                Student s = Student.fromFileLine(line);
                studentList.add(s);
                studentById.put(s.id, s);
                usedIds.add(s.id);
            }
            System.out.println("Loaded " + studentList.size() + " records from " + FILE_NAME);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing saved data: " + e.getMessage());
        }
    }

    // ---------- Menu-driven console interface ----------
    public static void main(String[] args) {
        StudentManagementSystem sms = new StudentManagementSystem();
        Scanner sc = new Scanner(System.in);
        sms.loadFromFile();

        boolean running = true;
        while (running) {
            System.out.println("\n===== Student Management System =====");
            System.out.println("1. Add Student");
            System.out.println("2. Edit Student");
            System.out.println("3. Delete Student");
            System.out.println("4. Search by ID");
            System.out.println("5. Search by Name");
            System.out.println("6. List All Students");
            System.out.println("7. Save to File");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            try {
                int choice = Integer.parseInt(sc.nextLine().trim());
                switch (choice) {
                    case 1:
                        System.out.print("Enter ID: ");
                        int id = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Enter Name: ");
                        String name = sc.nextLine().trim();
                        System.out.print("Enter GPA: ");
                        double gpa = Double.parseDouble(sc.nextLine().trim());
                        sms.addStudent(id, name, gpa);
                        break;
                    case 2:
                        System.out.print("Enter ID to edit: ");
                        int editId = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Enter new name: ");
                        String newName = sc.nextLine().trim();
                        System.out.print("Enter new GPA: ");
                        double newGpa = Double.parseDouble(sc.nextLine().trim());
                        sms.editStudent(editId, newName, newGpa);
                        break;
                    case 3:
                        System.out.print("Enter ID to delete: ");
                        int delId = Integer.parseInt(sc.nextLine().trim());
                        sms.deleteStudent(delId);
                        break;
                    case 4:
                        System.out.print("Enter ID to search: ");
                        int searchId = Integer.parseInt(sc.nextLine().trim());
                        try {
                            System.out.println(sms.search(searchId));
                        } catch (StudentNotFoundException e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case 5:
                        System.out.print("Enter name to search: ");
                        String searchName = sc.nextLine().trim();
                        try {
                            System.out.println(sms.search(searchName));
                        } catch (StudentNotFoundException e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case 6:
                        sms.listAll();
                        break;
                    case 7:
                        sms.saveToFile();
                        break;
                    case 8:
                        sms.saveToFile();
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option, please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: please enter a valid number.");
            }
        }
        sc.close();
    }
}
