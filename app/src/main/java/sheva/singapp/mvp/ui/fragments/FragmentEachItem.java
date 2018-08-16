package sheva.singapp.mvp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import sheva.singapp.R;
import sheva.singapp.mvp.model.entities.Exercise;
import sheva.singapp.mvp.model.entities.ExerciseWrapper;
import sheva.singapp.mvp.presenter.fragments.EachItemFragmentPresenter;
import sheva.singapp.mvp.ui.adapters.SongsListAdapter;
import sheva.singapp.mvp.ui.interfaces.IEachItemFragmentView;

public class FragmentEachItem extends Fragment implements SongsListAdapter.ILauncherSong, IEachItemFragmentView {
    private static final String SHOW_TYPE = "param1";

    private String showType;
    private Unbinder unbinder;
    private SongsListAdapter adapter;
    @BindView(R.id.rvSongsList)
    RecyclerView rvSongList;

    private EachItemFragmentPresenter presenter;


    private OnSongSelectedListener mListener;

    public FragmentEachItem() {
        // Required empty public constructor
    }

    public static FragmentEachItem newInstance(String showType) {
        FragmentEachItem fragment = new FragmentEachItem();
        Bundle args = new Bundle();
        args.putString(SHOW_TYPE, showType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            showType = getArguments().getString(SHOW_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        presenter = new EachItemFragmentPresenter();
        presenter.attachView(this);
        View v = inflater.inflate(R.layout.fragment_each_item, container, false);
        unbinder = ButterKnife.bind(this, v);
        adapter = new SongsListAdapter(this);
        rvSongList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSongList.setAdapter(adapter);
        presenter.loadSongs();
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSongSelectedListener) {
            mListener = (OnSongSelectedListener) context;
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
    public void onDestroyView() {
        unbinder.unbind();
        presenter.destroyPresenter();
        super.onDestroyView();
    }

    @Override
    public void launchSong(ExerciseWrapper entity) {
        mListener.onSongSelected(entity);
    }

    @Override
    public void onSongsLoaded(List<ExerciseWrapper> exerciseWrappersList) {
        adapter.updateList(exerciseWrappersList);
    }

    @Override
    public void showError(String message, String title) {
        mListener.showError(message, title);
    }

    public interface OnSongSelectedListener {
        void onSongSelected(ExerciseWrapper entity);

        void showError(String message, String title);
    }
}
