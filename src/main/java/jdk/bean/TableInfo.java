package jdk.bean;

import lombok.Data;

@Data
public class TableInfo {
    protected final String schema;
    protected final String table;

    public TableInfo(String table, String schema) {
        this.schema = schema;
        this.table = table;
    }
}
