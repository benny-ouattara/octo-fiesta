package mvp.tuts.todo_app.taskdetail;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import dataaccess.TasksRepository;
import dataaccess.local.TasksLocalDataSource;
import dataaccess.remote.TasksRemoteDataSource;
import mvp.tuts.todo_app.R;

public class TaskDetailActivity extends AppCompatActivity implements TaskDetailFragment.OnFragmentInteractionListener {

    public static final String EXTRA_TASK_ID = "TASK_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        String taskId = getIntent().getStringExtra(EXTRA_TASK_ID);

        TaskDetailFragment taskDetailFragment = (TaskDetailFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(taskDetailFragment == null) {
            taskDetailFragment = TaskDetailFragment.newInstance(taskId);
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, taskDetailFragment);
            transaction.commit();
        }

        TasksRepository repository = TasksRepository.getInstance(TasksRemoteDataSource.getInstance(), TasksLocalDataSource.getInstance(getApplicationContext()));
        new TaskDetailPresenter(taskId, repository, taskDetailFragment);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}
