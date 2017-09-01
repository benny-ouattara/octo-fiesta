package dataaccess;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dataaccess.models.Task;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksRepository implements TasksDataSource{
    private static TasksRepository INSTANCE = null;

    private final TasksDataSource mTasksRemoteDataSource;

    private final TasksDataSource mTasksLocalDataSource;

    Map<String, Task> mCachedTasks;

    boolean mCacheIsDirty = false;

    private TasksRepository(@NonNull TasksDataSource tasksRemoteDataSource, @NonNull TasksDataSource tasksLocalDataSource){
        mTasksLocalDataSource = tasksLocalDataSource;
        mTasksRemoteDataSource = tasksRemoteDataSource;
    }

    public static TasksRepository getInstance(TasksDataSource tasksRemoteDataSource, TasksDataSource tasksLocalDataSource){
        if(INSTANCE == null){
            INSTANCE = new TasksRepository(tasksRemoteDataSource, tasksLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    @Override
    public void getTasks(@NonNull final LoadTasksCallback callback) {
        checkNotNull(callback);

        if(mCachedTasks != null && !mCacheIsDirty){
            callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
        }

        if(mCacheIsDirty){
            getTasksFromRemoteDataSource(callback);
        } else {
            mTasksLocalDataSource.getTasks(new LoadTasksCallback() {
                @Override
                public void onTasksLoaded(List<Task> tasks) {
                    refreshCache(tasks);
                    callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callback);
                }
            });
        }
    }

    private void getTasksFromRemoteDataSource(@NonNull final LoadTasksCallback callback){
        mTasksRemoteDataSource.getTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshCache(tasks);
                refreshLocalDataSource(tasks);
                callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Task> tasks){
        if (mCachedTasks == null){
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
        for (Task task: tasks){
            mCachedTasks.put(task.getId(), task);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Task> tasks){
        mTasksLocalDataSource.deleteAllTasks();
        for(Task task : tasks){
            mTasksLocalDataSource.saveTask(task);
        }
    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull final GetTaskCallback callback) {
        checkNotNull(taskId);
        checkNotNull(callback);

        final Task cachedTask = getTaskWithId(taskId);

        if(cachedTask != null){
            callback.onTaskLoaded(cachedTask);
        }

        mTasksLocalDataSource.getTask(taskId, new GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                if(mCachedTasks == null){
                    mCachedTasks = new LinkedHashMap<String, Task>();
                }
                mCachedTasks.put(task.getId(), task);
                callback.onTaskLoaded(task);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private Task getTaskWithId(@NonNull String id){
        checkNotNull(id);
        if(mCachedTasks == null || mCachedTasks.isEmpty()){
            return null;
        } else {
            return mCachedTasks.get(id);
        }
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksLocalDataSource.saveTask(task);
        mTasksRemoteDataSource.saveTask(task);

        if(mCachedTasks == null){
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksLocalDataSource.completeTask(task);
        mTasksRemoteDataSource.completeTask(task);

        Task newtask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);

        if(mCachedTasks == null){
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), newtask);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        checkNotNull(taskId);
        completeTask(getTaskWithId(taskId));
    }

    @Override
    public void activateTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.activateTask(task);
        mTasksLocalDataSource.activateTask(task);

        Task newtask = new Task(task.getTitle(), task.getDescription(), task.getId());

        if(mCachedTasks == null){
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), newtask);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        checkNotNull(taskId);
        activateTask(getTaskWithId(taskId));
    }

    @Override
    public void clearCompletedTasks() {
        mTasksLocalDataSource.clearCompletedTasks();
        mTasksRemoteDataSource.clearCompletedTasks();

        if(mCachedTasks == null){
            mCachedTasks = new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String, Task>> it = mCachedTasks.entrySet().iterator();

        while(it.hasNext()){
            Map.Entry<String, Task> entry = it.next();
            if(entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllTasks() {
        mTasksLocalDataSource.deleteAllTasks();
        mTasksRemoteDataSource.deleteAllTasks();

        if(mCachedTasks == null){
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
    }

    @Override
    public void deleteTask(@NonNull Task task) {
        checkNotNull(task);

        mTasksRemoteDataSource.deleteTask(task);
        mTasksLocalDataSource.deleteTask(task);
        mCachedTasks.remove(task);
    }
}
