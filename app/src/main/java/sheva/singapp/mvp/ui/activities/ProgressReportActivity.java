package sheva.singapp.mvp.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sheva.singapp.App;
import sheva.singapp.R;
import sheva.singapp.mvp.model.entities.ExerciseResultWrapper;
import sheva.singapp.mvp.presenter.activities.ProgressReportActivityPresenter;
import sheva.singapp.mvp.ui.adapters.ProgressReportsAdapter;
import sheva.singapp.mvp.ui.interfaces.IProgressReportView;

public class ProgressReportActivity extends AppCompatActivity implements IProgressReportView {
    private final ProgressReportActivityPresenter presenter = new ProgressReportActivityPresenter();

    private ProgressReportsAdapter adapter;

    @BindView(R.id.progressReports_recv_APR)
    RecyclerView progressReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_progress_report);

        App.get().getAppComponent().inject(ProgressReportActivity.this);

        presenter.attachView(ProgressReportActivity.this);

        ButterKnife.bind(ProgressReportActivity.this);

        progressReports.setLayoutManager(new LinearLayoutManager(ProgressReportActivity.this)); /// TODO test. Remove
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.getProgressReports();
    }

    @Override
    public void onProgressReportsLoaded(List<ExerciseResultWrapper> exerciseResultWrappersList) {
        Log.d("qqq", "onProgressReportsLoaded");

        if (adapter == null) {
            adapter = new ProgressReportsAdapter(ProgressReportActivity.this, exerciseResultWrappersList);

            progressReports.setAdapter(adapter);
        }
        ///else
        /// TODO populate adapter

    }

    @Override
    public void onError(String message) {
        Log.d("qqq", "ProgressReportActivity.this.onError");
    }

    @OnClick(R.id.backArrow_butt_APR)
    public void back() {
        finish();
    }
}
