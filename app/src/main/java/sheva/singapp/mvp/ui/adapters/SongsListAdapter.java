package sheva.singapp.mvp.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sheva.singapp.R;
import sheva.singapp.mvp.model.entities.Exercise;
import sheva.singapp.mvp.model.entities.ExerciseWrapper;

public class SongsListAdapter extends RecyclerView.Adapter<SongsListAdapter.SongViewHolder> {

    private List<ExerciseWrapper> mExercisesWrappers;
    private LayoutInflater inflater;
    private ILauncherSong launcherSong;

    public interface ILauncherSong{
        void launchSong(ExerciseWrapper entity);
    }

    public SongsListAdapter(Fragment fragment) {
        inflater = LayoutInflater.from(fragment.getContext());
        mExercisesWrappers = new ArrayList<>();
        if (fragment instanceof ILauncherSong) {
            launcherSong = (ILauncherSong) fragment;
        } else {
            throw new IllegalStateException(fragment.toString() + " must implement ILauncherSong");
        }
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_each_song, parent, false);
        return new SongViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SongViewHolder holder, int position) {
        holder.tvTitle.setText(mExercisesWrappers.get(position).getExercise().getTitle());
        holder.ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcherSong.launchSong(mExercisesWrappers.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mExercisesWrappers.size();
    }

    public void updateList(List<ExerciseWrapper> list) {
        mExercisesWrappers.clear();
        mExercisesWrappers.addAll(list);
        notifyDataSetChanged();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvSongDescription)
        TextView tvDescription;
        @BindView(R.id.tvSongTitle)
        TextView tvTitle;
        @BindView(R.id.ibSongPlay)
        ImageButton ibPlay;

        public SongViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
