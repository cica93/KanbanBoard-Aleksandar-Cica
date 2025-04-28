package com.example.Kanban.Board.daoHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ResultSetProcessor {
     public Set<String> getColumns(ResultSet rs) throws SQLException {
        final Set<String> columns = new HashSet<>();
        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
            columns.add(rs.getMetaData().getColumnLabel(i));
        }
        return columns;
    }

    public Integer getResultSetInteger(ResultSet rs, String colName) throws SQLException {
        int v = rs.getInt(colName);
        if (rs.wasNull()) {
            return null;
        }
        return v;
    }

    public Long getResultSetLong(ResultSet rs, String colName) throws SQLException {
        long v = rs.getLong(colName);
        if (rs.wasNull()) {
            return null;
        }
        return v;
    }

}
