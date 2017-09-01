package mvp.tuts.todo_app.taskdetail;

import mvp.tuts.todo_app.BasePresenter;
import mvp.tuts.todo_app.BaseView;

public interface TaskDetailContract {

    interface View extends BaseView<Presenter> {

        void showMissingTask();

        void showEditTask(String taskId);

        void showLoadingIndicator(boolean display);

        boolean isActive();

        void hideTitle();

        void showTitle(String title);

        void showDescription(String description);

        void hideDescription();

        void showCompletionStatus(boolean completed);

        void showCompletedTaskMessage();

        void showActivedTaskMessage();

        void showDeletedTask();

    }

    interface Presenter extends BasePresenter {

        void editTask();

        void openTask();

        void completeTask();

        void activateTask();

        void deleteTask();

    }
}
