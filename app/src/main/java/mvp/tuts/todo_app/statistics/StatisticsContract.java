package mvp.tuts.todo_app.statistics;

import mvp.tuts.todo_app.BasePresenter;
import mvp.tuts.todo_app.BaseView;

public interface StatisticsContract {

    interface View extends BaseView<Presenter> {

        void setProgressIndicator(boolean display);

        boolean isActive();

        void showStatistics(int nonCompleted, int completed);

        void showLoadingStatisticsError();

    }

    interface Presenter extends BasePresenter {

    }
}
