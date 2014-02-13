package pl.fracz.mcr.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.File;

import pl.fracz.mcr.source.SourceFile;

@DatabaseTable(tableName = "source_file", daoClass = OpenedFileDao.class)
public class OpenedFile {
    @DatabaseField(columnName = "id", id = true, canBeNull = false)
    private String id;

    @DatabaseField(columnName = "path", canBeNull = false)
    private String path;

    @DatabaseField(columnName = "last_opened", canBeNull = false)
    private long lastOpened;

    public OpenedFile() {
    }

    public OpenedFile(SourceFile sourceFile, File file) {
        this.id = sourceFile.getIdentifier();
        this.path = file.getAbsolutePath();
        this.updateLastOpened();
    }

    public void updateLastOpened() {
        this.lastOpened = System.currentTimeMillis();
    }

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
