package com.example.Kanban.Board.utilities;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetExtractor<T> {
    T extractData(ResultSet rs) throws SQLException;
}
