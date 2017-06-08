package com.robbin.prestotemplate;

import com.robbin.prestotemplate.db.sql.PrestoClient;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by robbin on 2017/6/8.
 */
public class PrestoTemplate {

    public static void main(String[] args) throws SQLException {
        String path = PrestoTemplate.class.getClassLoader().getResource("./st").getPath();
        System.out.println(path);
        STGroupFile stg = new STGroupFile(path + "/test.stg");

        ST st = stg.getInstanceOf("selectAll");

        st.add("fields", "*");
        st.add("tableName", "table1");

        String selectAllSql = st.render();

        System.out.println(selectAllSql);

        execQuery(selectAllSql);


        ST st2 = stg.getInstanceOf("selectOne");

        st2.add("fields", "*");
        st2.add("tableName", "table1");
        st2.add("aliasTableName", "t1");
        st2.add("id", "1");

        String selectOneSql = st2.render();

        System.out.println(selectOneSql);

        execQuery(selectOneSql);
    }


    /**
     * 统一执行sql，返回每行结果数据
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public static List<List<String>> execQuery(String sql) throws SQLException {
        List result = new ArrayList();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = PrestoClient.getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = PrestoClient.executeQuery(sql, statement);
            while (resultSet.next()) {
                List rowList = new ArrayList();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    String col = resultSet.getString(resultSet.getMetaData().getColumnLabel(i));
                    rowList.add(col);
                }
                result.add(rowList);
            }
            resultSet.close();
        } catch (Throwable t) {
            if (t.getMessage().contains("statement is too large")) {
                throw new SQLException("筛选参数过多！", "", 1001);
            }
            throw new SQLException("sql 拼写错误或语法错误；");
        } finally {
            if (null != statement)
                statement.close();

            if (null != connection) {
                connection.close();
            }
        }

        return result;
    }
}
