package sheva.singapp.mvp.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sheva.singapp.App;
import sheva.singapp.R;
import sheva.singapp.mvp.model.entities.ExerciseResultWrapper;
import sheva.singapp.mvp.ui.activities.VideoPlaybackActivity;

/**
 * Created by Vlad Aspire Hanych on 25.01.2018.
 */

public class ProgressReportsAdapter extends RecyclerView.Adapter<ProgressReportsAdapter.ProgressReportViewHolder>  {
    private static final String PERCENT_TEMPLATE = " %";

    private LayoutInflater mInflater;

    private List<ExerciseResultWrapper> mExerciseResultWrappersList;

    public static final String VIDEO_URL_KEY = "video_url";

    public ProgressReportsAdapter(Context context, List<ExerciseResultWrapper> exerciseResultWrappersList) {
        mInflater = LayoutInflater.from(context);

        mExerciseResultWrappersList = new ArrayList<>();

        mExerciseResultWrappersList.addAll(exerciseResultWrappersList);
    }

    @Override
    public ProgressReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.item_each_progress_report, parent, false);

        return new ProgressReportViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProgressReportViewHolder holder, int position) {
        ExerciseResultWrapper currentProgressReport = mExerciseResultWrappersList.get(position);

        holder.tvExerciseName.setText(currentProgressReport.getExerciseResult().getTitle());

        holder.tvTopPitchScore.setText(currentProgressReport.getPitchScore() + PERCENT_TEMPLATE);

        holder.tvTopTempoScore.setText(currentProgressReport.getTempoScore() + PERCENT_TEMPLATE);
    }

    @Override
    public int getItemCount() {
        return mExerciseResultWrappersList.size();
    }

    class ProgressReportViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.exerciseName_txtv_IEPR)
        TextView tvExerciseName;

        /*///@BindView(R.id.playProgressReport_butt_IEPR)
        Button buttPlay;*/

        @BindView(R.id.topPitchScore_textv_IEPR)
        TextView tvTopPitchScore;

        @BindView(R.id.topTempoScore_textv_IEPR)
        TextView tvTopTempoScore;

        ProgressReportViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("LongLogTag")
        @OnClick(R.id.playProgressReport_butt_IEPR)
        void onPlay() {
            ///Log.d("ProgressReportViewHolder", "position ->" + getAdapterPosition());

            String videoURLEndpoint
                    = mExerciseResultWrappersList.get(getAdapterPosition()).getVideoFile();

            Context context = tvExerciseName.getContext();

            if (videoURLEndpoint == null) {
                Toast.makeText(context, "Video is not available.", Toast.LENGTH_SHORT).show();

                return;
            }

            Intent intent = new Intent(context, VideoPlaybackActivity.class);
            intent.putExtra(VIDEO_URL_KEY, App.BASE_SERVER_URL + videoURLEndpoint);

            context.startActivity(intent);
        }
    }
}
