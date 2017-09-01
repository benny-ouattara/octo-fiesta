package dataaccess.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import dataaccess.TasksDataSource;
import dataaccess.models.Task;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksLocalDataSource implements TasksDataSource {

    private TasksDbHelper mDbHelper;
    private static TasksLocalDataSource INSTANCE;

    private TasksLocalDataSource(@NonNull Context context){
        checkNotNull(context);
        mDbHelper = new TasksDbHelper(context);
    }

    public static TasksLocalDataSource getInstance(@NonNull Context context){
        if(INSTANCE == null){
            INSTANCE = new TasksLocalDataSource(context);
        }

        return INSTANCE;
    }

    @Override
    public void getTasks(@NonNull LoadTasksCallback callback) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                TaskContract.TaskTable.COLUMN_NAME_ENTRY_ID,
                TaskContract.TaskTable.COLUMN_NAME_TITLE,
                TaskContract.TaskTable.COLUMN_NAME_DESCRIPTION,
                TaskContract.TaskTable.COLUMN_NAME_COMPLETED
        };

        Cursor c = db.query(TaskContract.TaskTable.TABLE_NAME, projection, null, null, null, null, null);

        if(c != null && c.getCount() > 0){
            while (c.moveToNext()){
                String itemId = c.getString(c.getColumnIndexOrThrow(TaskContract.TaskTable.COLUMN_NAME_ENTRY_ID));
                String title = c.getString(c.getColumnIndexOrThrow(TaskContract.TaskTable.COLUMN_NAME_TITLE));
                String description = c.getString(c.getColumnIndexOrThrow(TaskContract.TaskTable.COLUMN_NAME_DESCRIPTION));
                boolean completed = c.getInt(c.getColumnIndexOrThrow(TaskContract.TaskTable.COLUMN_NAME_COMPLETED)) == 1;

                Task task = new Task(title, description, itemId, completed);
                tasks.add(task);
            }
        }

        if(c != null){
            c.close();
        }

        db.close();

        if(tasks.isEmpty()){
            callback.onDataNotAvailable();
        } else {
            callback.onTasksLoaded(tasks);
        }
    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                TaskContract.TaskTable.COLUMN_NAME_ENTRY_ID,
                TaskContract.TaskTable.COLUMN_NAME_TITLE,
                TaskContract.TaskTable.COLUMN_NAME_DESCRIPTION,
                TaskContract.TaskTable.COLUMN_NAME_COMPLETED
        };

        String selection = TaskContract.TaskTable.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { taskId };

        Cursor c = db.query(TaskContract.TaskTable.TABLE_NAME, projection,selection, selectionArgs, null, null, null);

        Task task = null;

        if(c != null && c.getCount() > 0){
            c.moveToFirst();
            String itemId = c.getString(c.getColumnIndexOrThrow(TaskContract.TaskTable.COLUMN_NAME_ENTRY_ID));
            String title = c.getString(c.getColumnIndexOrThrow(TaskContract.TaskTable.COLUMN_NAME_TITLE));
            String description = c.getString(c.getColumnIndexOrThrow(TaskContract.TaskTable.COLUMN_NAME_DESCRIPTION));
            boolean completed = c.getInt(c.getColumnIndexOrThrow(TaskContract.TaskTable.COLUMN_NAME_COMPLETED)) == 1;

            task = new Task(title, description, itemId, completed);
        }

        if(c != null){
            c.close();
        }

        db.close();

        if(task != null){
            callback.onTaskLoaded(task);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskTable.COLUMN_NAME_ENTRY_ID, task.getId());
        values.put(TaskContract.TaskTable.COLUMN_NAME_TITLE, task.getTitle());
        values.put(TaskContract.TaskTable.COLUMN_NAME_DESCRIPTION, task.getDescription());
        values.put(TaskContract.TaskTable.COLUMN_NAME_COMPLETED, task.isCompleted());

        db.insert(TaskContract.TaskTable.TABLE_NAME, null, values);

        db.close();
    }

    @Override
    public void completeTask(@NonNull Task task) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskTable.COLUMN_NAME_COMPLETED, true);

        String selection = TaskContract.TaskTable.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { task.getId() };

        db.update(TaskContract.TaskTable.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        // not required
    }

    @Override
    public void activateTask(@NonNull Task task) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskTable.COLUMN_NAME_COMPLETED, false);

        String selection = TaskContract.TaskTable.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { task.getId() };

        db.update(TaskContract.TaskTable.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        // not required
    }

    @Override
    public void clearCompletedTasks() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TaskContract.TaskTable.COLUMN_NAME_COMPLETED + " LIKE ?";
        String[] selectionArgs = { "1" };

        db.delete(TaskContract.TaskTable.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    @Override
    public void refreshTasks() {
        // not required
    }

    @Override
    public void deleteAllTasks() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(TaskContract.TaskTable.TABLE_NAME, null, null);

        db.close();
    }

    @Override
    public void deleteTask(@NonNull Task task) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TaskContract.TaskTable.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { task.getId() };

        db.delete(TaskContract.TaskTable.TABLE_NAME, selection, selectionArgs);

        db.close();
    }
}
