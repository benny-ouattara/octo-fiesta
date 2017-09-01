package dataaccess.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TasksDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TaskContract.TaskTable.TABLE_NAME + " (" +
                    TaskContract.TaskTable.COLUMN_NAME_ENTRY_ID + " TEXT  PRIMARY_KEY,"+
                    TaskContract.TaskTable.COLUMN_NAME_TITLE + " TEXT," +
                    TaskContract.TaskTable.COLUMN_NAME_DESCRIPTION + " TEXT,"+
                    TaskContract.TaskTable.COLUMN_NAME_COMPLETED + " INTEGER " +
            ")";

    private static final String DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TaskContract.TaskTable.TABLE_NAME;

    public TasksDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DELETE_TABLE);
        onCreate(sqLiteDatabase);
    }
}
