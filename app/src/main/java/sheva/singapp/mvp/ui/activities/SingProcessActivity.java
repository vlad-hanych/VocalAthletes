package sheva.singapp.mvp.ui.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.renderscript.RenderScript;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blur.andrey.blurtest.BlurHelper;
import com.example.artem.videorecorder.RecordService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fotoapparat.view.CameraView;
import sheva.singapp.R;
import sheva.singapp.mvp.model.entities.ExerciseWrapper;
import sheva.singapp.mvp.presenter.activities.SingProcessActivityPresenter;
import sheva.singapp.mvp.ui.interfaces.ISingProcessActivityView;
import sheva.singapp.mvp.ui.views.SoundGraphView;
import sheva.singapp.utils.NotesResolver;
import sheva.singapp.utils.audio.calculators.AudioCalculator;
import sheva.singapp.utils.audio.core.Recorder;

public class SingProcessActivity extends AppCompatActivity implements ISingProcessActivityView, SurfaceHolder.Callback {

    public static final int AUDIO_CHECK_PERIOD = 333;
    ///private static final String MAIN_LOG_TAG = "SingProcessActivity...";

    @BindView(R.id.tvProcessLoading)
    TextView tvLoading;
    @BindView(R.id.pbProcessLoading)
    ProgressBar pbLoading;
    @BindView(R.id.fabProcessPlay)
    FloatingActionButton fabPlay;
    @BindView(R.id.processContainer_relLay_ASP)
    RelativeLayout processContainer;
    @BindView(R.id.melodyName_txtv_ASP)
    TextView melodyName;

    @BindView(R.id.video_surfv_ASP)
    SurfaceView videoPreview;

    @BindView(R.id.soundGraph_sndgraphv_ASP)
    SoundGraphView soundGraph;

    private ExerciseWrapper mExerciseWrapperEntity;

    private final SingProcessActivityPresenter presenter = new SingProcessActivityPresenter();

    private SurfaceHolder mSurfaceHolder;

    private Camera mCamera;
    private int mCameraId;

    private Recorder mRecorder;
    private AudioCalculator mAudioCalculator;
    private Handler mHandler;

    private Timer mAudioInputCheckingTimer;
    private TimerTask mAudioInputCheckingTimerTask;

    private boolean mNeedToCheckVoiceInput;

    private MediaProjectionManager mProjectionManager;

    private RecordService recordService;

    private static final int RECORD_REQUEST_CODE  = 101;
    private static final int STORAGE_REQUEST_CODE = 102;
    private static final int AUDIO_REQUEST_CODE   = 103;

    private boolean mScreenRecordingStarted;

    private boolean mSingingProcessPaused;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            RecordService.RecordBinder binder = (RecordService.RecordBinder) service;
            recordService = binder.getRecordService();
            recordService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };
    private BlurHelper mBlurHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mExerciseWrapperEntity = getIntent().getExtras().getParcelable(WarmUpActivity.SONG_NAME);

        presenter.attachView(SingProcessActivity.this);

        downloadMIDIofMelodyInput();

        setContentView(R.layout.activity_sing_process);

        ButterKnife.bind(this);

        mSurfaceHolder = videoPreview.getHolder();
        mSurfaceHolder.addCallback(SingProcessActivity.this);

        mCameraId = getMostSuitableCameraId();

        melodyName.setText(mExerciseWrapperEntity.getExercise().getTitle());

        fabPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onPlayEverything();
            }
        });

        mRecorder = new Recorder(callback);
        mAudioCalculator = new AudioCalculator();
        mHandler = new Handler(Looper.getMainLooper());

        mProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        permissions();

        Intent recordingScreenSericeIntent = new Intent(SingProcessActivity.this, RecordService.class);

        startService(recordingScreenSericeIntent);

        bindService(recordingScreenSericeIntent, mConnection, BIND_AUTO_CREATE);

        final Intent captureIntent = mProjectionManager.createScreenCaptureIntent();
        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
            }
        },3000);


        /*final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recordService.stopRecord();

                presenter.uploadVideo(recordService.getVideoFile(), String.valueOf(mExerciseWrapperEntity.getId()));
            }
        }, 15000);

        mScreenRecordingStarted = false;*/
    }

    private void initFotoapparat() {
        CameraView cameraView = findViewById(R.id.camera_camV_ASP);

        TextureView texttureView = findViewById(R.id.blurOverlay_textV_ASP);

        mBlurHelper = new BlurHelper();

        mBlurHelper.setBlur(cameraView, texttureView, SingProcessActivity.this);

        mBlurHelper.startFotoapparat();
    }

    public String getMelodyURLFromEntity () {
        return mExerciseWrapperEntity.getExercise().getAudioFile();
    }

    @Override
    public void downloadMIDIofMelodyInput() {
        presenter.getMIDI(mExerciseWrapperEntity.getExercise().getAudioFile());
    }

    @Override
    public void onMIDIDownloaded(InputStream midiInputStream) {
        soundGraph.setMidiInputStream(midiInputStream);

        pbLoading.setVisibility(View.INVISIBLE);

        fabPlay.setVisibility(View.VISIBLE);

        tvLoading.setText(getResources().getString(R.string.loaded));
    }

    @Override
    public void onUploadScores(int rhytm, int pitch) {
        presenter.uploadScores(rhytm, pitch, String.valueOf(mExerciseWrapperEntity.getId()));
    }

    private sheva.singapp.utils.audio.core.Callback callback = new sheva.singapp.utils.audio.core.Callback() {
        @Override
        public void onBufferAvailable(byte[] buffer) {
            mAudioCalculator.setBytes(buffer);
            int amplitude = mAudioCalculator.getAmplitude();
            ///double decibel = mAudioCalculator.getDecibel();
            final double frequency = mAudioCalculator.getFrequency();

            ///Log.d("SingProcessActivity.this...", "onBufferAvailable");

            if (mNeedToCheckVoiceInput) {
                if (frequency > 2000 || frequency < 50 || amplitude < 500) {
                    return;
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ///Log.d("SingProcessActivity.this...", "onBufferAvailable...run");

                        try {
                            soundGraph.checkIfTheNoteAndPaintIt(NotesResolver.recognizeNoteByFrequency((int) frequency));
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                });
                mNeedToCheckVoiceInput = false;
            }
        }
    };

    private int getMostSuitableCameraId() {
        Camera.CameraInfo ci = new Camera.CameraInfo();

        int id = -1; /// No available camera found

        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, ci);

            if (ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                id = i;
                break;
            } else if (ci.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                id = i;
            }

        }
        return id;
    }

    private void onPlayEverything() {
        fabPlay.setVisibility(View.GONE);

        tvLoading.setVisibility(View.GONE);

        processContainer.setVisibility(View.VISIBLE);

        /// TODO подумати, як цього спекатись!
        findViewById(R.id.headerContainer_rellay_ASP).bringToFront();
    }

    private void permissions () {
        if (ContextCompat.checkSelfPermission(SingProcessActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(SingProcessActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST_CODE || requestCode == AUDIO_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish();
            }
        }
    }

    ///
    /*private void captureVideo3 () {
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();
        Camera.Size optimalSize = getOptimalVideoSize(mSupportedVideoSizes,
                mSupportedPreviewSizes, videoPreview.getWidth(), videoPreview.getHeight());

        // Use the same size for recording profile.
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        profile.videoFrameWidth = optimalSize.width;
        profile.videoFrameHeight = optimalSize.height;

        // likewise for the mCamera object itself.
        parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
        mCamera.setParameters(parameters);

        mCamera.unlock();

        mVideoMediaRecorder = new MediaRecorder();

        mVideoMediaRecorder.setCamera(mCamera);

        mVideoMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mVideoMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mVideoMediaRecorder.setVideoFrameRate(profile.videoFrameRate);
        mVideoMediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
        mVideoMediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
        mVideoMediaRecorder.setVideoEncoder(profile.videoCodec);

        mVideoMediaRecorder.setOrientationHint(90);

        // Step 4: Set output file
        mLastRecordedVideoFile = Utils.getOutputMediaFile(MEDIA_TYPE_VIDEO);
        if (mLastRecordedVideoFile == null) {
            return;
        }
        mVideoMediaRecorder.setOutputFile(mLastRecordedVideoFile.getPath());

        // Step 5: Prepare configured MediaRecorder
        try {
            mVideoMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("TAG", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            ///releaseMediaRecorder();
            return;
        } catch (IOException e) {
            Log.d("TAG", "IOException preparing MediaRecorder: " + e.getMessage());
            ///releaseMediaRecorder();
            return;
        }

        mVideoMediaRecorder.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mVideoMediaRecorder.stop();

                Toast.makeText(SingProcessActivity.this, "Video CAPTURED!", Toast.LENGTH_SHORT).show();
            }
        }, 5000);
    }*/

    /*private Camera.Size getOptimalVideoSize(List<Camera.Size> supportedVideoSizes,
                                            List<Camera.Size> previewSizes, int w, int h) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;

        // Supported video sizes list might be null, it means that we are allowed to use the preview
        // sizes
        List<Camera.Size> videoSizes;
        if (supportedVideoSizes != null) {
            videoSizes = supportedVideoSizes;
        } else {
            videoSizes = previewSizes;
        }
        Camera.Size optimalSize = null;

        // Start with max value and refine as we iterate over available video sizes. This is the
        // minimum difference between view and camera height.
        double minDiff = Double.MAX_VALUE;

        // Target view height
        int targetHeight = h;

        // Try to find a video size that matches aspect ratio and the target view size.
        // Iterate over all available sizes and pick the largest size that can fit in the view and
        // still maintain the aspect ratio.
        for (Camera.Size size : videoSizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff && previewSizes.contains(size)) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find video size that matches the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : videoSizes) {
                if (Math.abs(size.height - targetHeight) < minDiff && previewSizes.contains(size)) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }*/

    @Override
    protected void onResume() {
        super.onResume();

        ///Log.d("SingProcessActivity...this", "onResume");

        if (mScreenRecordingStarted) {
            launchSoundCheckingAndAudioRecording();
            findViewById(R.id.headerContainer_rellay_ASP).bringToFront();
        }

        mRecorder.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK) {
            MediaProjection mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
            recordService.setMediaProject(mMediaProjection);
            recordService.startRecord();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mRecorder.stop();

        if (mScreenRecordingStarted && !mSingingProcessPaused){
            soundGraph.stopAllRedrawingAndPauseMusic();
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!openCamera()) {
            alertCameraDialog();
        }
    }

    private boolean openCamera() {
        boolean result = false;
        releaseCamera();
        try {
            mCamera = Camera.open(mCameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCamera != null) {
            try {
                setUpCamera(mCamera);
                mCamera.setErrorCallback(new Camera.ErrorCallback() {

                    @Override
                    public void onError(int error, Camera camera) {

                    }
                });
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
                releaseCamera();
            }
        }
        return result;
    }

    private void releaseCamera() {
        try {
            if (mCamera != null) {
                mCamera.setPreviewCallback(null);
                mCamera.setErrorCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
            mCamera = null;
        }
    }

    private void setUpCamera(Camera c) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        int mRotation = getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        switch (mRotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;

            default:
                break;
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // frontFacing
            mRotation = (info.orientation + degree) % 330;
            mRotation = (360 - mRotation) % 360;
        } else {
            // Back-facing
            mRotation = (info.orientation - degree + 360) % 360;
        }
        c.setDisplayOrientation(mRotation);
        Camera.Parameters params = c.getParameters();

        List<String> focusModes = params.getSupportedFlashModes();
        if (focusModes != null) {
            if (focusModes
                    .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
        }

        params.setRotation(mRotation);
    }

    private void alertCameraDialog() {
        android.app.AlertDialog.Builder dialog = createAlert(SingProcessActivity.this,
                "Camera info", "Error to open camera");
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        dialog.show();
    }

    private android.app.AlertDialog.Builder createAlert(Context context, String title, String message) {

        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(
                new ContextThemeWrapper(context,
                        android.R.style.Theme_Holo_Light_Dialog));
        ///dialog.setIcon(R.drawable.ic_launcher);
        if (title != null)
            dialog.setTitle(title);
        else
            dialog.setTitle("Information");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void setUIFlags() {

    }

    @Override
    public void onSongLoaded() {

    }

    @Override
    public void onVideoRecordingEnded() {
        recordService.stopRecord();

        presenter.uploadVideo(recordService.getVideoFile(), String.valueOf(mExerciseWrapperEntity.getId()));
    }

    @Override
    public void onError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @OnClick(R.id.playProcess_butt_ASP)
    public void playInProcess(Button playInProcessButt) {
        playInProcessButt.setVisibility(View.INVISIBLE);

        findViewById(R.id.pauseHeader_butt_ASP).setVisibility(View.VISIBLE);

        mScreenRecordingStarted = true;

        launchSoundCheckingAndAudioRecording();

        soundGraph.launchMideProcessor();

        soundGraph.prepareMediaPlayer();
    }

    @OnClick(R.id.pauseHeader_butt_ASP)
    public void pauseHeader() {
        soundGraph.stopAllRedrawingAndPauseMusic(); ///

        findViewById(R.id.headerContainer_rellay_ASP).setVisibility(View.GONE);

        findViewById(R.id.bottomBlackSpace_relLay_ASP).setVisibility(View.GONE);

        soundGraph.setVisibility(View.GONE);

        videoPreview.setVisibility(View.GONE);

        releaseCamera();

        initFotoapparat();

        findViewById(R.id.camera_camV_ASP).setVisibility(View.VISIBLE);

        findViewById(R.id.blurOverlay_textV_ASP).setVisibility(View.VISIBLE);

        findViewById(R.id.paused_linLay_ASP).setVisibility(View.VISIBLE);

        mSingingProcessPaused = true;
    }

    @OnClick(R.id.resumeInPause_txtV_ASP)
    public void resumeInPauseOnClick() {
        findViewById(R.id.paused_linLay_ASP).setVisibility(View.GONE);

        findViewById(R.id.camera_camV_ASP).setVisibility(View.INVISIBLE);

        findViewById(R.id.blurOverlay_textV_ASP).setVisibility(View.INVISIBLE);

        mBlurHelper.stopFotoapparat();

        openCamera();

        soundGraph.setVisibility(View.VISIBLE);

        videoPreview.setVisibility(View.VISIBLE);

        findViewById(R.id.headerContainer_rellay_ASP).setVisibility(View.VISIBLE);

        findViewById(R.id.bottomBlackSpace_relLay_ASP).setVisibility(View.VISIBLE);

        mNeedToCheckVoiceInput = true;

        soundGraph.initializeAndScheduleRedrawing();

        launchSoundCheckingAndAudioRecording();

        soundGraph.launchMideProcessor ();

        soundGraph.resumeMelodyPlaying ();

        mSingingProcessPaused = false;
    }

    @OnClick(R.id.restartInPause_txtV_ASP)
    public void restartInPauseOnClick() {
        findViewById(R.id.camera_camV_ASP).setVisibility(View.INVISIBLE);

        findViewById(R.id.blurOverlay_textV_ASP).setVisibility(View.INVISIBLE);

        findViewById(R.id.paused_linLay_ASP).setVisibility(View.GONE);

        findViewById(R.id.playProcess_butt_ASP).setVisibility(View.VISIBLE);

        mBlurHelper.stopFotoapparat();

        openCamera();

        soundGraph.setVisibility(View.VISIBLE);

        videoPreview.setVisibility(View.VISIBLE);

        findViewById(R.id.headerContainer_rellay_ASP).setVisibility(View.VISIBLE);

        findViewById(R.id.bottomBlackSpace_relLay_ASP).setVisibility(View.VISIBLE);

        soundGraph.stopAndResetMideProcessor();

        soundGraph.stopMediaPlayer();

        soundGraph.initializeAndScheduleRedrawing();

        soundGraph.setNeedToStartMelodyPlaying(true);
    }

    @OnClick(R.id.backToListInPause_txtV_ASP)
    public void backToListInPauseOnClick() {
        finish();
    }

    @OnClick(R.id.restartSingingProcess_butt_ASP)
    public void restartSingingProcess() {
        Toast.makeText(SingProcessActivity.this, "Restart pressed!", Toast.LENGTH_SHORT).show();

        findViewById(R.id.starTableContainer_relLay_ASP).setVisibility(View.GONE);

        findViewById(R.id.restartSingingProcess_butt_ASP).setVisibility(View.GONE);

        findViewById(R.id.vocalAthletes_butt_ASP).setVisibility(View.GONE);

        findViewById(R.id.playProcess_butt_ASP).setVisibility(View.VISIBLE);

        findViewById(R.id.headerContainer_rellay_ASP).setVisibility(View.VISIBLE);

        findViewById(R.id.bottomBlackSpace_relLay_ASP).setVisibility(View.VISIBLE);

        soundGraph.initializeAndScheduleRedrawing();

        soundGraph.setNeedToStartMelodyPlaying(true);

        soundGraph.setSingProcessFinished(false);
    }

    @OnClick(R.id.vocalAthletes_butt_ASP)
    public void vocalAthletesButtClick(Button vocalAthletesButt) {
        Toast.makeText(SingProcessActivity.this, "Vocal Athletes pressed!", Toast.LENGTH_SHORT).show();
    }

    public void cancelSoundCheckingAndAudioRecording() {
        mNeedToCheckVoiceInput = false;

        mAudioInputCheckingTimerTask.cancel();

        mAudioInputCheckingTimer.cancel();

        mAudioInputCheckingTimerTask = null;

        mAudioInputCheckingTimer = null;

        /// TODO подумати про реліз аудіо рекорда
    }

    public void launchSoundCheckingAndAudioRecording() {
        mAudioInputCheckingTimer = new Timer();
        mAudioInputCheckingTimerTask = new TimerTask() {
            @Override
            public void run() {
                ///Log.d("mAudioInputCheckingTimerTask...", this + " ; mNeedToCheckVoiceInput = true");

                mNeedToCheckVoiceInput = true;
            }
        };
        mAudioInputCheckingTimer.schedule(mAudioInputCheckingTimerTask, 0, AUDIO_CHECK_PERIOD); /// 5 разів в секунду
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(mConnection);
    }
}
