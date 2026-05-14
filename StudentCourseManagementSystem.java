import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

// ============================================================
//  BASE CLASSES
// ============================================================
abstract class Person {
    private String name;
    private String email;

    public Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public abstract String getInfo();
}

class Student extends Person {
    private String rollNumber;
    private double gpa;

    public Student(String name, String rollNumber, String email, double gpa) {
        super(name, email);
        this.rollNumber = rollNumber;
        this.gpa = gpa;
    }

    public String getRollNumber() { return rollNumber; }
    public double getGpa() { return gpa; }

    @Override
    public String getInfo() {
        return "Student[" + rollNumber + "] " + getName() + " | GPA: " + gpa;
    }
}

class Course {
    private String courseCode;
    private String title;
    private int creditHours;

    public Course(String courseCode, String title, int creditHours) {
        this.courseCode = courseCode;
        this.title = title;
        this.creditHours = creditHours;
    }

    public String getCourseCode() { return courseCode; }
    public String getTitle() { return title; }
    public int getCreditHours() { return creditHours; }
}

// ============================================================
//  DATABASE HELPER
// ============================================================
class DatabaseHelper {
    private static final String URL = "jdbc:mysql://localhost:3306/student_management";
    private static final String USER = "root"; 
    private static final String PASSWORD = "Zohaib@123"; 

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void addStudent(Student s) throws SQLException {
        String sql = "INSERT INTO students (roll_number, name, email, gpa) VALUES (?,?,?,?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getRollNumber());
            ps.setString(2, s.getName());
            ps.setString(3, s.getEmail());
            ps.setDouble(4, s.getGpa());
            ps.executeUpdate();
        }
    }

    public static ResultSet getAllStudents() throws SQLException {
        return getConnection().createStatement().executeQuery("SELECT * FROM students");
    }

    public static void addCourse(Course c) throws SQLException {
        String sql = "INSERT INTO courses (course_code, title, credit_hours) VALUES (?,?,?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCourseCode());
            ps.setString(2, c.getTitle());
            ps.setInt(3, c.getCreditHours());
            ps.executeUpdate();
        }
    }

    public static ResultSet getAllCourses() throws SQLException {
        return getConnection().createStatement().executeQuery("SELECT * FROM courses");
    }

    // --- NEW: ENROLLMENT METHODS ---
    public static void enrollStudent(String roll, String code) throws SQLException {
        String sql = "INSERT INTO enrollments (roll_number, course_code) VALUES (?,?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, roll);
            ps.setString(2, code);
            ps.executeUpdate();
        }
    }

    public static ResultSet getAllEnrollments() throws SQLException {
        String sql = "SELECT e.enrollment_id, e.roll_number, s.name, e.course_code, c.title " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.roll_number = s.roll_number " +
                     "JOIN courses c ON e.course_code = c.course_code";
        return getConnection().createStatement().executeQuery(sql);
    }
}

// ============================================================
//  UI UTILS
// ============================================================
class TableUtil {
    public static void fillTable(JTable table, ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        String[] colNames = new String[cols];
        for (int i = 1; i <= cols; i++) colNames[i - 1] = meta.getColumnName(i);

        DefaultTableModel model = new DefaultTableModel(colNames, 0);
        while (rs.next()) {
            Object[] row = new Object[cols];
            for (int i = 1; i <= cols; i++) row[i - 1] = rs.getObject(i);
            model.addRow(row);
        }
        table.setModel(model);
    }
}

// ============================================================
//  MAIN APPLICATION
// ============================================================
public class StudentCourseManagementSystem extends JFrame {
    
    public StudentCourseManagementSystem() {
        setTitle("Student Management System");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Students", new StudentPanel());
        tabs.addTab("Courses", new CoursePanel());
        tabs.addTab("Subject Enrollment", new EnrollmentPanel()); // Added new tab
        
        add(tabs);
        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            SwingUtilities.invokeLater(() -> new StudentCourseManagementSystem());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}

// ============================================================
//  PANELS
// ============================================================

class StudentPanel extends JPanel {
    JTextField t1 = new JTextField(10), t2 = new JTextField(10), t3 = new JTextField(10), t4 = new JTextField(5);
    JTable table = new JTable();

    public StudentPanel() {
        setLayout(new BorderLayout());
        JPanel p = new JPanel();
        p.add(new JLabel("Roll:")); p.add(t1);
        p.add(new JLabel("Name:")); p.add(t2);
        p.add(new JLabel("Email:")); p.add(t3);
        p.add(new JLabel("GPA:")); p.add(t4);
        JButton btn = new JButton("Add Student");
        p.add(btn);
        
        add(p, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btn.addActionListener(e -> {
            try {
                DatabaseHelper.addStudent(new Student(t2.getText(), t1.getText(), t3.getText(), Double.parseDouble(t4.getText())));
                TableUtil.fillTable(table, DatabaseHelper.getAllStudents());
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        });
    }
}

class CoursePanel extends JPanel {
    JTextField t1 = new JTextField(10), t2 = new JTextField(10), t3 = new JTextField(5);
    JTable table = new JTable();

    public CoursePanel() {
        setLayout(new BorderLayout());
        JPanel p = new JPanel();
        p.add(new JLabel("Code:")); p.add(t1);
        p.add(new JLabel("Title:")); p.add(t2);
        p.add(new JLabel("Cr:")); p.add(t3);
        JButton btn = new JButton("Add Course");
        p.add(btn);

        add(p, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btn.addActionListener(e -> {
            try {
                DatabaseHelper.addCourse(new Course(t1.getText(), t2.getText(), Integer.parseInt(t3.getText())));
                TableUtil.fillTable(table, DatabaseHelper.getAllCourses());
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        });
    }
}

// --- NEW PANEL: ENROLLMENT PANEL ---
class EnrollmentPanel extends JPanel {
    JTextField tRoll = new JTextField(10), tCourse = new JTextField(10);
    JTable table = new JTable();

    public EnrollmentPanel() {
        setLayout(new BorderLayout());
        JPanel p = new JPanel();
        p.add(new JLabel("Student Roll No:")); p.add(tRoll);
        p.add(new JLabel("Course Code:")); p.add(tCourse);
        JButton btnEnroll = new JButton("Enroll Now");
        JButton btnRefresh = new JButton("Refresh List");
        p.add(btnEnroll); p.add(btnRefresh);

        add(p, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnEnroll.addActionListener(e -> {
            try {
                DatabaseHelper.enrollStudent(tRoll.getText(), tCourse.getText());
                JOptionPane.showMessageDialog(this, "Enrollment Successful!");
                TableUtil.fillTable(table, DatabaseHelper.getAllEnrollments());
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        btnRefresh.addActionListener(e -> {
            try { TableUtil.fillTable(table, DatabaseHelper.getAllEnrollments()); } 
            catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        });
    }
}