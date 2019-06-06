package DBUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String USERNAME = "WILL_BE_REPLACED";
    private static final String PASSWORD = "WILL_BE_REPLACED";
    private static final String DB_NAME = "WILL_BE_REPLACED";
    private static final String CONN = "WILL_BE_REPLACED" + DB_NAME;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CONN, USERNAME, PASSWORD);
    }
}
