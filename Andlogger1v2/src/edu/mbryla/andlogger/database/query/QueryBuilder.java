package edu.mbryla.andlogger.database.query;


/**
 * @author mateusz
 */
public abstract class QueryBuilder {
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String SELECT_ALL = "SELECT * FROM ";
    private static final String COUNT = "SELECT count(*) FROM ";

    public abstract String build();

    public static CreateTableQueryBuilder createTable(String tableName) {
        return new CreateTableQueryBuilder(tableName);
    }

    public static String dropTable(String tableName) {
        return simpleQuery(DROP_TABLE, tableName);
    }

    public static String selectAll(String tableName) {
        return simpleQuery(SELECT_ALL, tableName);
    }

    public static String count(String tableName) {
        return simpleQuery(COUNT, tableName);
    }

    private static String simpleQuery(String type, String tableName) {
        StringBuilder buf = new StringBuilder(type);
        buf.append(tableName).append(";");
        return buf.toString();
    }
}
