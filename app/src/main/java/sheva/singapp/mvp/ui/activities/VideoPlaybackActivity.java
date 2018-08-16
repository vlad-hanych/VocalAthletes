package sheva.singapp.mvp.ui.activities;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.VideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import sheva.singapp.R;
import sheva.singapp.mvp.ui.adapters.ProgressReportsAdapter;

public class VideoPlaybackActivity extends AppCompatActivity {
    @BindView(R.id.progressReportVideo_videoV_AVP)
    VideoView mProgressReportVideoView;
    @BindView(R.id.ibSingSongBack)
    ImageButton ibBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_playback);

        ButterKnife.bind(VideoPlaybackActivity.this);

        String videoURL = getIntent().getStringExtra(ProgressReportsAdapter.VIDEO_URL_KEY);

        mProgressReportVideoView.setVideoPath(videoURL); /// TODO ключ перенести в якеь нормальне місце

        ///mProgressReportVideoView.setMediaController(new MediaController(this));
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mProgressReportVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });

        mProgressReportVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer myMediaPlayer) {
                DisplayMetrics metrics = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(metrics);
                android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) mProgressReportVideoView.getLayoutParams();
                params.width =  metrics.widthPixels;
                params.height = metrics.heightPixels;
                params.leftMargin = 0;
                mProgressReportVideoView.setLayoutParams(params);
                mProgressReportVideoView.start(); // начинаем воспроизведение автоматически
            }
        });



        ///mProgressReportVideoView.start();
    }
}
