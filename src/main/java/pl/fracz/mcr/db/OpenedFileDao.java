package pl.fracz.mcr.db;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.sql.SQLException;

public class OpenedFileDao extends BaseDaoImpl<OpenedFile, String> {
    protected OpenedFileDao(Class<OpenedFile> dataClass) throws SQLException {
        super(dataClass);
    }

    protected OpenedFileDao(ConnectionSource connectionSource, Class<OpenedFile> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    protected OpenedFileDao(ConnectionSource connectionSource, DatabaseTableConfig<OpenedFile> tableConfig) throws SQLException {
        super(connectionSource, tableConfig);
    }
}
