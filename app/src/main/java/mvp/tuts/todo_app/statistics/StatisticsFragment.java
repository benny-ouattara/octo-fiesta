package mvp.tuts.todo_app.statistics;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mvp.tuts.todo_app.R;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatisticsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends Fragment implements StatisticsContract.View {

    private OnFragmentInteractionListener mListener;

    private TextView mStatisticsTV;

    private StatisticsContract.Presenter mPresenter;

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        mStatisticsTV = (TextView) rootView.findViewById(R.id.statistics);
        return rootView;
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
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setPresenter(@NonNull StatisticsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void setProgressIndicator(boolean display) {
        if(display) {
            mStatisticsTV.setText(getString(R.string.loading));
        } else {
            mStatisticsTV.setText("");
        }
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showStatistics(int nonCompleted, int completed) {
        if (nonCompleted == 0 && completed == 0) {
            mStatisticsTV.setText(getResources().getString(R.string.statistics_no_tasks));
        } else {
            String displayString = getResources().getString(R.string.statistics_active_tasks) + " "
                    + nonCompleted + "\n" + getResources().getString(R.string.statistics_completed_tasks)
                    + " " + completed;
            mStatisticsTV.setText(displayString);
        }
    }

    @Override
    public void showLoadingStatisticsError() {
        mStatisticsTV.setText(getResources().getString(R.string.statistics_error));
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
