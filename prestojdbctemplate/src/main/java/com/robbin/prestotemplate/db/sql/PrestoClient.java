package com.robbin.prestotemplate.db.sql;

import com.facebook.presto.jdbc.PrestoConnection;
import com.facebook.presto.jdbc.PrestoDriver;
import com.robbin.prestotemplate.util.PropertyLoader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static java.lang.String.format;

/**
 * Created by robbin on 2016/10/14.
 */
public class PrestoClient {
    public static Properties properties;

    static {
        properties = new PropertyLoader("presto.properties").getProp();
    }

    private static String host = properties.getProperty("host");
    private static String port = properties.getProperty("port");
    private static String catalog = properties.getProperty("catalog");
    private static String url = "jdbc:presto://%s";
    private static String size = properties.getProperty("size");
    private static PrestoDriver prestoDriver = new PrestoDriver();

    public static Connection getConnection() throws Throwable {
        PrestoConnection connection;
        try {
            url = format(url, host + ":" + port + "/" + catalog);
            connection = (PrestoConnection) prestoDriver.connect(url, properties);
            connection.setSessionProperty("query_priority", properties.getProperty("query_priority"));
            connection.setSessionProperty("task_concurrency", properties.getProperty("task_concurrency"));
            return connection;
        } catch (Throwable t) {
            throw t;
        }
    }


    /**
     * @param sql
     * @return
     * @throws SQLException
     */
    public static ResultSet executeQuery(String sql, Statement statement) throws SQLException {
        if (statement == null) {
            throw new RuntimeException("执行sql的statement为空");
        }
        statement.setFetchSize(Integer.parseInt(size));
        ResultSet resultSet = statement.executeQuery(sql);
        return resultSet;
    }
}

