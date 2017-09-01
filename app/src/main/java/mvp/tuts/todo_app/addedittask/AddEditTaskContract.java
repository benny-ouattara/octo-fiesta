package mvp.tuts.todo_app.addedittask;

import mvp.tuts.todo_app.BasePresenter;
import mvp.tuts.todo_app.BaseView;

public interface AddEditTaskContract {

    interface View extends BaseView<Presenter> {

        void showEmptyTaskError();

        void showTasksList();

        void setTitle(String title);

        void setDescription(String description);

        boolean isActive();

    }

    interface Presenter extends BasePresenter {

        void saveTask(String title, String description);

        void populateTask();

        boolean isDataMissing();

    }
}
