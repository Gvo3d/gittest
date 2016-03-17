import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by User on 14.03.2016.
 */
public class Main {

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = null;
        try {
            Properties properties = new Properties();
            Main jdbctest = new Main();
            jdbctest.loadProperties(properties);
            String dbUrl = properties.getProperty("database");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");

            connection = DriverManager.getConnection(dbUrl, username, password);
            System.out.println("Connection has benn established");




            jdbctest.addBatch(connection);


//

            List<Student> students = jdbctest.getStudents(connection);





            printStudents(students);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }

    }

    private static void printStudents(List<Student> students) {
        for (Student student:students){
            System.out.println(student);
        }
    }

    private void loadProperties(Properties properties) throws IOException {
        InputStream stream = getClass().getResourceAsStream("jdbc.properties");
        properties.load(stream);
    }

    private List<Student> getStudents(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("select * from Students");
        ResultSet set = statement.getResultSet();
        List<Student> list = new ArrayList<Student>();
        while (set.next()) {
            Student student = new Student();
            student.setFirstname(set.getString(2));
            student.setLastname(set.getString(3));
            student.setAge(set.getInt(4));
            list.add(student);
        }
        return list;
    }

    private void addStudent(Connection connection) throws SQLException {
        String sql = "insert into students(firstname, lastname, age) values (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, "John");
        statement.setString(2, "Smith");
        statement.setInt(3, 25);
        statement.execute();
    }

    private void addBatch(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
        String sql = "insert into students(firstname, lastname, age) values (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i=0; i<10; i++){
            statement.setString(1, "John"+i);
            statement.setString(2, "Smith"+i);
            statement.setInt(3, 25+i);
            statement.addBatch();
        }
        statement.executeBatch();
        connection.setAutoCommit(true);
    }
}
