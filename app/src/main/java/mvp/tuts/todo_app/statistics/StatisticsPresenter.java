package mvp.tuts.todo_app.statistics;

import android.support.annotation.NonNull;

import java.util.List;

import dataaccess.TasksDataSource;
import dataaccess.TasksRepository;
import dataaccess.models.Task;

import static com.google.common.base.Preconditions.checkNotNull;

public class StatisticsPresenter implements StatisticsContract.Presenter {

    private StatisticsContract.View mStatisticsView;

    private TasksRepository mTasksRepository;

    public StatisticsPresenter(@NonNull TasksRepository repository, @NonNull StatisticsContract.View view) {
        mTasksRepository = checkNotNull(repository, "repository cannot be null");
        mStatisticsView = checkNotNull(view, "view cannot be null");

        mStatisticsView.setPresenter(this);
    }

    @Override
    public void start() {
        loadStatistics();
    }

    private void loadStatistics(){
        mStatisticsView.setProgressIndicator(true);

        mTasksRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                int nonCompleteTasks = 0;
                int completedTasks = 0;

                for(Task task : tasks) {
                    if(task.isCompleted()) {
                        completedTasks += 1;
                    } else {
                        nonCompleteTasks += 1;
                    }
                }

                if(!mStatisticsView.isActive()) {
                    return;
                }

                mStatisticsView.setProgressIndicator(false);
                mStatisticsView.showStatistics(nonCompleteTasks, completedTasks);
            }

            @Override
            public void onDataNotAvailable() {
                if(!mStatisticsView.isActive()) {
                    return;
                }
                mStatisticsView.showLoadingStatisticsError();
            }
        });
    }
}
