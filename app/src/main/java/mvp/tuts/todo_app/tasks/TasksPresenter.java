package mvp.tuts.todo_app.tasks;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import dataaccess.TasksDataSource;
import dataaccess.TasksRepository;
import dataaccess.models.Task;
import mvp.tuts.todo_app.addedittask.AddEditTaskActivity;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksPresenter implements TasksContract.Presenter {

    private final TasksRepository mTasksRepository;

    private final TasksContract.View mTasksView;

    private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TASKS;

    private boolean mFirstLoad = true;

    public TasksPresenter(@NonNull TasksRepository tasksRepository, @NonNull TasksContract.View tasksView) {
        checkNotNull(tasksRepository);
        checkNotNull(tasksView);

        mTasksRepository = tasksRepository;
        mTasksView = tasksView;

        mTasksView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTasks(false, true);
    }

    @Override
    public void setFiltering(@NonNull TasksFilterType filterType) {
        checkNotNull(filterType);
        mCurrentFiltering = filterType;
    }

    @Override
    public void clearCompletedTasks() {

    }

    @Override
    public void loadTasks(boolean forceUpdate, final boolean showLoadingIndicator) {
        if(showLoadingIndicator){
            mTasksView.showLoadIndicator(true);
        }

        if(forceUpdate) {
            mTasksRepository.refreshTasks();
        }

        mTasksRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                List<Task> tasksToShow = new ArrayList<Task>();

                for(Task task : tasks) {
                    switch (mCurrentFiltering) {
                        case ALL_TASKS:
                            tasksToShow.add(task);
                            break;
                        case ACTIVE_TASKS:
                            if(task.isActive()) {
                                tasksToShow.add(task);
                            }
                            break;
                        case COMPLETED_TASKS:
                            if(task.isCompleted()) {
                                tasksToShow.add(task);
                            }
                            break;
                        default:
                            tasksToShow.add(task);
                            break;
                    }
                }

                if (!mTasksView.isActive()){
                    return;
                }

                if(showLoadingIndicator) {
                    mTasksView.showLoadIndicator(false);
                }

                processTasks(tasksToShow);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    @Override
    public void openTaskDetails(@NonNull Task task) {
        checkNotNull(task, "requested task cannot be null");
        mTasksView.showTaskDetailsUi(task.getId());
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task, "completed task cannot be null");
        mTasksRepository.completeTask(task);
        mTasksView.showTaskMarkedComplete();
    }

    @Override
    public void activateTask(@NonNull Task task) {
        checkNotNull(task, "activated task cannot be null");
        mTasksRepository.activateTask(task);
        mTasksView.showTaskMarkedActive();
    }

    @Override
    public void addNewTask() {
        mTasksView.showAddTask();
     }

    @Override
    public void result(int requestCode, int resultCode) {
        if(AddEditTaskActivity.REQUEST_ADD_TASK == requestCode && Activity.RESULT_OK == resultCode){
            mTasksView.showSuccessfullySavedMessage();
        }
    }

    @Override
    public void processTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            processEmptyTasks();
        } else {
            mTasksView.showTasks(tasks);
            showFilterLabel();
        }
    }

    public TasksFilterType getFiltering() {
        return mCurrentFiltering;
    }

    private void processEmptyTasks(){
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksView.showNoActiveTasks();
                break;
            case COMPLETED_TASKS:
                mTasksView.showNoCompletedTasks();
                break;
            default:
                mTasksView.showNoTasks();
                break;
        }
    }

    private void showFilterLabel() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksView.showActiveFilterLabel();
                break;
            case COMPLETED_TASKS:
                mTasksView.showCompletedFilterLabel();
                break;
            default:
                mTasksView.showAllFilterLabel();
                break;
        }
    }
}
