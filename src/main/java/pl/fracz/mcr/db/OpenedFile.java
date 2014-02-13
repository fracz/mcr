package pl.fracz.mcr.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "source_file", daoClass = OpenedFileDao.class)
public class OpenedFile {
    @DatabaseField(columnName = "id", id = true, canBeNull = false)
    private String id;

    @DatabaseField(columnName = "path", canBeNull = false)
    private String path;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
