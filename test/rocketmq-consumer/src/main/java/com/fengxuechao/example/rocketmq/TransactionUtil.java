package com.fengxuechao.example.rocketmq;

import java.sql.*;

/**
 * 事务管理工具类
 *
 * @author fengxuechao
 * @date 2022/4/7
 */
public class TransactionUtil {

    private static final ThreadLocal<Connection> connections = new ThreadLocal<>();

    private TransactionUtil() {
    }

    /**
     * 开启事务
     */
    public static Connection startTransaction() {
        Connection connection = connections.get();
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/rocketmq?serverTimezone=GMT%2B8",
                        "root",
                        "12345678");
                connection.setAutoCommit(false);
                connections.set(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * 插入, 返回结果
     */
    public static int execute(String sql, Object... args) throws SQLException {
        PreparedStatement preparedStatement = createPreparedStatement(sql, args);
        return preparedStatement.executeUpdate();
    }

    /**
     * 查询
     */
    public static ResultSet select(String sql, Object... args) throws SQLException {
        PreparedStatement preparedStatement = createPreparedStatement(sql, args);
        preparedStatement.execute();
        return preparedStatement.getResultSet();
    }

    private static PreparedStatement createPreparedStatement(String sql, Object[] args) throws SQLException {
        Connection connection = startTransaction();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
        }
        return preparedStatement;
    }


    /**
     * 提交事务
     */
    public static void commit() {
        try (Connection connection = connections.get()) {
            connection.commit();
            connections.remove();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 回滚事务
     */
    public static void rollback() {
        try (Connection connection = connections.get()) {
            connection.rollback();
            connections.remove();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
