package mvp.tuts.todo_app.taskdetail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.common.base.Preconditions;

import mvp.tuts.todo_app.addedittask.AddEditTaskActivity;
import mvp.tuts.todo_app.addedittask.AddEditTaskFragment;
import mvp.tuts.todo_app.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TaskDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TaskDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskDetailFragment extends Fragment implements TaskDetailContract.View {

    private OnFragmentInteractionListener mListener;

    private TaskDetailContract.Presenter mPresenter;

    private CheckBox mTaskDetailCB;

    private TextView mTaskDetailTitle;

    private TextView mTaskDetailDescription;

    private static final String ARGUMENT_TASK_ID = "TASK_ID";

    private static final int REQUEST_EDIT_TASK = 1;

    public TaskDetailFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TaskDetailFragment.
     */
    public static TaskDetailFragment newInstance(String taskId) {
        TaskDetailFragment fragment = new TaskDetailFragment();
        Bundle arguments = new Bundle();
       arguments.putString(ARGUMENT_TASK_ID, taskId);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_task_detail, container, false);

        setHasOptionsMenu(true);

        mTaskDetailCB = (CheckBox) root.findViewById(R.id.task_detail_complete);
        mTaskDetailTitle = (TextView) root.findViewById(R.id.task_detail_title);
        mTaskDetailDescription = (TextView) root.findViewById(R.id.task_detail_description);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.editTask();
            }
        });

        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                mPresenter.deleteTask();
                break;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_EDIT_TASK) {
            if(resultCode == Activity.RESULT_OK) {
                getActivity().finish();
            }
        }
    }

    @Override
    public void setPresenter(TaskDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showMissingTask() {
        mTaskDetailTitle.setText("");
        mTaskDetailDescription.setText(getString(R.string.no_data));
    }

    @Override
    public void showEditTask(@NonNull String taskId) {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        intent.putExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId);
        startActivityForResult(intent, REQUEST_EDIT_TASK);
    }

    @Override
    public void showLoadingIndicator(boolean display) {
        if(display) {
            mTaskDetailTitle.setText("");
            mTaskDetailDescription.setText(getString(R.string.loading));
        }
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void hideTitle() {
        mTaskDetailTitle.setVisibility(View.GONE);
    }

    @Override
    public void showTitle(@NonNull String title) {
        mTaskDetailTitle.setVisibility(View.VISIBLE);
        mTaskDetailTitle.setText(title);
    }

    @Override
    public void showDescription(@NonNull String description) {
        mTaskDetailDescription.setVisibility(View.VISIBLE);
        mTaskDetailDescription.setText(description);
    }

    @Override
    public void hideDescription() {
        mTaskDetailDescription.setVisibility(View.GONE);
    }

    @Override
    public void showCompletionStatus(boolean completed) {
        Preconditions.checkNotNull(mTaskDetailCB);

        mTaskDetailCB.setChecked(completed);
        mTaskDetailCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    mPresenter.completeTask();
                } else {
                    mPresenter.activateTask();
                }
            }
        });
    }

    @Override
    public void showCompletedTaskMessage() {
        Snackbar.make(getView(), getString(R.string.task_marked_complete), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showActivedTaskMessage() {
        Snackbar.make(getView(), getString(R.string.task_marked_active), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showDeletedTask() {
        getActivity().finish();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
