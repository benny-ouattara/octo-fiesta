package mvp.tuts.todo_app.AddEditTask;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import dataaccess.TasksRepository;
import dataaccess.local.TasksLocalDataSource;
import dataaccess.remote.TasksRemoteDataSource;
import mvp.tuts.todo_app.R;

public class AddEditTaskActivity extends AppCompatActivity implements AddEditTaskFragment.OnFragmentInteractionListener {

    public static final int REQUEST_ADD_TASK = 1;

    private AddEditTaskPresenter mAddEditTaskPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        // setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        AddEditTaskFragment addEditTaskFragment = (AddEditTaskFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        String taskId = getIntent().getStringExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID);

        if(addEditTaskFragment == null) {
            addEditTaskFragment = AddEditTaskFragment.newInstance();

            if(getIntent().hasExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID)){
                actionBar.setTitle(R.string.edit_task);
                Bundle bundle = new Bundle();
                bundle.putString(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId);
                addEditTaskFragment.setArguments(bundle);
            } else {
                actionBar.setTitle(R.string.add_task);
            }

            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, addEditTaskFragment);
            transaction.commit();
        }

        TasksRepository repository = TasksRepository.getInstance(TasksRemoteDataSource.getInstance(), TasksLocalDataSource.getInstance(getApplicationContext()));
        mAddEditTaskPresenter = new AddEditTaskPresenter(taskId, repository, addEditTaskFragment);
        addEditTaskFragment.setPresenter(mAddEditTaskPresenter);
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
