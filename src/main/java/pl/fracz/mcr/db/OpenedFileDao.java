package pl.fracz.mcr.db;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.io.File;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import pl.fracz.mcr.source.SourceFile;

public class OpenedFileDao extends BaseDaoImpl<OpenedFile, String> {
    public OpenedFileDao(Class<OpenedFile> dataClass) throws SQLException {
        super(dataClass);
    }

    public OpenedFileDao(ConnectionSource connectionSource, Class<OpenedFile> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public OpenedFileDao(ConnectionSource connectionSource, DatabaseTableConfig<OpenedFile> tableConfig) throws SQLException {
        super(connectionSource, tableConfig);
    }

    public void registerFileOpen(SourceFile sourceFile, File file) {
        try {
            OpenedFile openedFile;
            if (idExists(sourceFile.getIdentifier())) {
                openedFile = queryForId(sourceFile.getIdentifier());
            } else {
                openedFile = new OpenedFile(sourceFile, file);
            }
            openedFile.updateLastOpened();
            createOrUpdate(openedFile);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public OpenedFile findLastOpened() {
        List<OpenedFile> opened = findLastOpened(1);
        if (opened.size() > 0) return opened.get(0);
        return null;
    }

    public List<OpenedFile> findLastOpened(int maxQuantity) {
        QueryBuilder<OpenedFile, String> query = queryBuilder().orderBy("last_opened", false).limit(Long.valueOf(maxQuantity));
        try {
            return query.query();
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }
}
