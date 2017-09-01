package mvp.tuts.todo_app.taskdetail;

import android.support.annotation.NonNull;

import com.google.common.base.Strings;

import dataaccess.TasksDataSource;
import dataaccess.TasksRepository;
import dataaccess.models.Task;

import static com.google.common.base.Preconditions.checkNotNull;

public class TaskDetailPresenter implements TaskDetailContract.Presenter {

    private final String mTaskId;

    private final TasksRepository mTaskRepository;

    private final TaskDetailContract.View mTaskDetailView;

    public TaskDetailPresenter(String taskId, TasksRepository repository, TaskDetailContract.View taskDetailView){
        mTaskId = taskId;
        mTaskRepository = checkNotNull(repository, "repository cannot be null");
        mTaskDetailView = checkNotNull(taskDetailView, "taskDetailView cannot be null");

        mTaskDetailView.setPresenter(this);
    }

    @Override
    public void start() {
        openTask();
    }

    @Override
    public void editTask() {
        if(Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }

        mTaskDetailView.showEditTask(mTaskId);
    }

    @Override
    public void openTask() {
        if(Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }

        mTaskDetailView.showLoadingIndicator(true);
        mTaskRepository.getTask(mTaskId, new TasksDataSource.GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                if(!mTaskDetailView.isActive()) {
                    return;
                }
                mTaskDetailView.showLoadingIndicator(false);
                if(task == null) {
                    mTaskDetailView.showMissingTask();
                } else {
                    showTask(task);
                }
            }

            @Override
            public void onDataNotAvailable() {
                if(!mTaskDetailView.isActive()) {
                    return;
                }
                mTaskDetailView.showMissingTask();
            }
        });
    }

    @Override
    public void completeTask() {
        if(Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }
        mTaskRepository.completeTask(mTaskId);
        mTaskDetailView.showCompletedTaskMessage();
    }

    @Override
    public void activateTask() {
        if(Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }
        mTaskRepository.activateTask(mTaskId);
        mTaskDetailView.showActivedTaskMessage();
    }

    @Override
    public void deleteTask() {
        if(Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }

        //mTaskRepository.deleteTask(mTaskId);
        mTaskDetailView.showDeletedTask();
    }

    private void showTask(@NonNull Task task) {
        String title = task.getTitle();
        String description = task.getDescription();

        if(Strings.isNullOrEmpty(title)) {
            mTaskDetailView.hideTitle();
        } else {
            mTaskDetailView.showTitle(title);
        }

        if(Strings.isNullOrEmpty(description)) {
            mTaskDetailView.hideDescription();
        } else {
            mTaskDetailView.showDescription(description);
        }

        mTaskDetailView.showCompletionStatus(task.isCompleted());
    }
}
