package mvp.tuts.todo_app.AddEditTask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import dataaccess.TasksDataSource;
import dataaccess.models.Task;

public class AddEditTaskPresenter implements AddEditTaskContract.Presenter, TasksDataSource.GetTaskCallback {

    private String mTaskId;

    private TasksDataSource mTasksRepository;

    private AddEditTaskContract.View mAddTaskView;

    public AddEditTaskPresenter(@Nullable String taskId, @NonNull TasksDataSource tasksRepository, @NonNull AddEditTaskContract.View addTaskView){
        mTaskId = taskId;
        mTasksRepository = tasksRepository;
        mAddTaskView = addTaskView;
    }

    @Override
    public void saveTask(String title, String description) {
        if(isNewTask()) {
            createTask(title, description);
        } else {
            updateTask(title, description);
        }
    }

    @Override
    public void populateTask() {
        if(isNewTask()) {
            throw new RuntimeException("populate task cannot be called on new task");
        }
        mTasksRepository.getTask(mTaskId, this);
    }

    @Override
    public boolean isDataMissing() {
        return false;
    }

    @Override
    public void start() {
        if(!isNewTask()){
            populateTask();
        }
    }

    @Override
    public void onTaskLoaded(Task task) {
        if(mAddTaskView.isActive()) {
            mAddTaskView.setTitle(task.getTitle());
            mAddTaskView.setDescription(task.getDescription());
        } else {
            throw new RuntimeException("view is not available for updates");
        }
    }

    @Override
    public void onDataNotAvailable() {
        if (mAddTaskView.isActive()) {
            mAddTaskView.showEmptyTaskError();
        }
    }


    private boolean isNewTask() {
        return mTaskId == null;
    }

    private void createTask(String title, String description) {
        Task newTask = new Task(title, description);
        if(newTask.isEmpty()) {
            mAddTaskView.showEmptyTaskError();
        } else {
            mTasksRepository.saveTask(newTask);
            mAddTaskView.showTasksList();
        }
    }

    private void updateTask(String title, String description){
        if(isNewTask()) {
            throw new RuntimeException("update task cannot be called with a new task");
        }

        mTasksRepository.saveTask(new Task(title, description, mTaskId));
        mAddTaskView.showTasksList();
    }
}
