package Database;
import Utils.PropertyReader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class TrafficDatabaseImpl {
    private Connection currentConnection;

    public TrafficDatabaseImpl() {
        try {
            System.out.println("Connected to PostgreSQL database!");
            String ip = PropertyReader.getProperties().getProperty("ip");
            String port = PropertyReader.getProperties().getProperty("port");
            String user = PropertyReader.getProperties().getProperty("user");
            String password = PropertyReader.getProperties().getProperty("password");
            String dbName = PropertyReader.getProperties().getProperty("dbName");
            String schemaName = PropertyReader.getProperties().getProperty("schemaName");
            Connection connection = DriverManager.getConnection(
                    String.format("jdbc:postgresql://%s:%s/%s?currentSchema=%s", ip, port, dbName, schemaName),
                    user, password);
            currentConnection = connection;
            File initSql = new File("src/main/resources/database/init.sql");
            Statement statement = connection.createStatement();
            String s = FileUtils.readFileToString(initSql, StandardCharsets.UTF_8);
            statement.executeUpdate(s);
            System.out.println("everything was created");
        } catch (SQLException e) {
            System.out.println("Connection failure!");
            closeConnection();
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        }
    }

    public void updateValue(String valueName, String value, String date) {
        final String ADD_USER_POINTS_SQL = "UPDATE limits_per_hour " +
                String.format("SET limit_value = %s, effective_date = %s", value, date) +
                String.format("WHERE limit_name = %s", valueName);

        try {
            Statement statement = currentConnection.createStatement();
            statement.executeUpdate(ADD_USER_POINTS_SQL);

        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
        }
    }

    public long getMin() {
        final String GET_MIN = "SELECT limit_value FROM limits_per_hour WHERE " +
                "limit_name = 'min' AND effective_date = (SELECT MAX(effective_date) FROM limits_per_hour " +
                "WHERE limit_name = 'min');";
        try {
            Statement statement = currentConnection.createStatement();
            ResultSet rs = statement.executeQuery(GET_MIN);
            if (rs.next()) {
                return rs.getLong("limit_value");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
        }
        return -1;
    }

    public long getMax() {
        final String GET_MAX = "SELECT limit_value FROM limits_per_hour WHERE " +
                "limit_name = 'max' AND effective_date = (SELECT MAX(effective_date) FROM limits_per_hour " +
                "WHERE limit_name = 'max');";
        try {
            Statement statement = currentConnection.createStatement();
            ResultSet rs = statement.executeQuery(GET_MAX);
            if (rs.next()) {
                return rs.getLong("limit_value");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
        }
        return -1;
    }

    private void closeConnection() {
        try {
            currentConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
