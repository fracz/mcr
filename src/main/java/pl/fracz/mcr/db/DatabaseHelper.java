package pl.fracz.mcr.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Database helper class used to manage the creation and upgrading of your database.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "inspector.db";
    private static final int DATABASE_VERSION = 1;

    protected AndroidConnectionSource connectionSource = new AndroidConnectionSource(this);

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource conn) {
        try {
            TableUtils.createTable(connectionSource, OpenedFile.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final void onUpgrade(SQLiteDatabase db, ConnectionSource conn, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, OpenedFile.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
