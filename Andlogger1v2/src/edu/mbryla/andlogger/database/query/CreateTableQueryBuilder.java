package edu.mbryla.andlogger.database.query;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import edu.mbryla.andlogger.database.Type;


/**
 * @author mateusz
 */
public class CreateTableQueryBuilder extends QueryBuilder {
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";

    private String tableName;
    private LinkedHashMap<String, String> columns = new LinkedHashMap<String, String>();

    protected CreateTableQueryBuilder(String tableName) {
        this.tableName = tableName;
    }

    public CreateTableQueryBuilder addPrimaryKey(String name) {
        columns.put(name, Type.KEY.toString());
        return this;
    }

    public CreateTableQueryBuilder addColumn(String name, Type type, Type.Nullable nullp) {
        columns.put(name,
                new StringBuilder().append(type).append(" ").append(nullp)
                        .toString());
        return this;
    }

    @Override
    public String build() {
        StringBuilder buf = new StringBuilder();
        boolean first = true;

        buf.append(CREATE_TABLE).append(tableName).append(" (");
        for (Entry<String, String> e : columns.entrySet()) {
            if (!first) {
                buf.append(", ");
            } else {
                first = !first;
            }

            buf.append(e.getKey()).append(" ").append(e.getValue());
        }
        buf.append(");");

        return buf.toString();
    }

}
