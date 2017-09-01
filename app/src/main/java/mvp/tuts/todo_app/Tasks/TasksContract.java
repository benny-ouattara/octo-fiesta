package mvp.tuts.todo_app.Tasks;

import java.util.List;

import dataaccess.models.Task;
import mvp.tuts.todo_app.BasePresenter;
import mvp.tuts.todo_app.BaseView;

public interface TasksContract {
    interface View extends BaseView<Presenter> {
        void showFilteringPopupMenu();

        void showAddTask();

        void showSuccessfullySavedMessage();

        boolean isActive();

        void showNoActiveTasks();

        void showNoCompletedTasks();

        void showNoTasks();

        void showActiveFilterLabel();

        void showCompletedFilterLabel();

        void showAllFilterLabel();

        void showTasks(List<Task> tasks);

        void showLoadIndicator(boolean display);

        void showTaskDetailsUi(String taskId);

        void showTaskMarkedComplete();

        void showTaskMarkedActive();
    }

    interface Presenter extends BasePresenter {
        void setFiltering(TasksFilterType filterType);

        void clearCompletedTasks();

        void loadTasks(boolean forceUpdate, boolean displayIndicator);

        void openTaskDetails(Task task);

        void completeTask(Task task);

        void activateTask(Task task);

        void addNewTask();

        void result(int requestCode, int resultCode);

        void processTasks(List<Task> tasks);
    }
}
