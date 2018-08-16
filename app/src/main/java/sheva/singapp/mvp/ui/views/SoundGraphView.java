package sheva.singapp.mvp.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.foxy_corporation.androidmidilib.MidiFile;
import com.foxy_corporation.androidmidilib.event.MidiEvent;
import com.foxy_corporation.androidmidilib.event.NoteOff;
import com.foxy_corporation.androidmidilib.event.NoteOn;
import com.foxy_corporation.androidmidilib.util.MidiEventListener;
import com.foxy_corporation.androidmidilib.util.MidiProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import sheva.singapp.App;
import sheva.singapp.R;
import sheva.singapp.mvp.ui.activities.SingProcessActivity;
import sheva.singapp.utils.NotesResolver;

/**
 * Created by Vlad Aspire Hanych on 25.12.2017.
 */

public class SoundGraphView extends SurfaceView implements SurfaceHolder.Callback,
        MidiEventListener {
    private static final String MAIN_LOG_TAG = "SoundGraphView...";

    private SingProcessActivity mParentActivity;

    private String mMelodyURL;

    private String[] mNotes;

    private int mSurfaceWidth;

    private int mSurfaceHeight;

    private int mLeftMarginForBorder;

    private int mNoteFragmentWidth;

    private int mNoteFragmentHeight;

    private HashMap<String, Integer> mTopYRowCoordsByNote;

    private Timer mCanvasRedrawingTimer;
    private RedrawCanvasTimerTask mCanvasRedrawingTimerTask;
    private static final long CANVAS_REDRAWING_TIMER_PERIOD = 80;

    private long mSustainNoteFragmentRedrawingPeriod;

    private static final int NOTE_FRAGMENTS_MOVING_DELTA = 10;

    private MediaPlayer mMelodyMediaPlayer;

    private boolean mNeedToStartMelodyPlaying;

    private HashMap<String, MelodyNoteSustainStuff> mCurrentMelodyNotesSustains;
    private SurfaceHolder mSurfaceHolder;

    private class MelodyNoteSustainStuff {
        private Timer timer;

        private TimerTask timerTask;

        private MelodyNoteSustainStuff(Timer timer, TimerTask timerTask) {
            MelodyNoteSustainStuff.this.timer = timer;
            MelodyNoteSustainStuff.this.timerTask = timerTask;
        }

        private Timer getTimer() {
            return MelodyNoteSustainStuff.this.timer;
        }

        private TimerTask getTimerTask() {
            return MelodyNoteSustainStuff.this.timerTask;
        }
    }

    private int mTotalMelodyNoteFragmentsCount; /// кількість всіх фрагментів нот мелодії

    private int mRhytmCorrectVoiceNoteFragmentsCount; /// кількість коректних фрагментів ноти по ритму

    private int mFullyCorrectVoiceNoteFragmentsCount; /// кількість коректних фрагментів ноти по ритму і по висоті

    private boolean mSingProcessFinished;

    private MidiProcessor mMideProcessor;

    private boolean mNeedToStopCanvasRedrawing;

    private enum NoteFragmentCorrectness {CORRECT, INCORRECT_NOTE, INCORRECT_RHYTM}

    public SoundGraphView(Context context) { /// TODO перевірити, чи потрібен цей конструктор
        super(context);

        mParentActivity = (SingProcessActivity) context;

        mMelodyURL = App.BASE_SERVER_URL + mParentActivity.getMelodyURLFromEntity();

        mNotes = getResources().getStringArray(R.array.notes);

        /// adding surface holder callbacks to this class
        SurfaceHolder surfaceHolder = getHolder();

        surfaceHolder.addCallback(SoundGraphView.this);

        SoundGraphView.this.setZOrderMediaOverlay(true);

        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);

        mTopYRowCoordsByNote = new HashMap<>();

        mNeedToStartMelodyPlaying = true;

        mCurrentMelodyNotesSustains = new HashMap<>();

        mSingProcessFinished = false;
    }

    public SoundGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mParentActivity = (SingProcessActivity) context;

        mMelodyURL = App.BASE_SERVER_URL + mParentActivity.getMelodyURLFromEntity();

        mNotes = getResources().getStringArray(R.array.notes);

        /// adding surface holder callbacks to this class
        SurfaceHolder surfaceHolder = getHolder();

        surfaceHolder.addCallback(SoundGraphView.this);

        SoundGraphView.this.setZOrderMediaOverlay(true);

        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);

        mTopYRowCoordsByNote = new HashMap<>();

        mNeedToStartMelodyPlaying = true;

        mCurrentMelodyNotesSustains = new HashMap<>();

        mSingProcessFinished = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        ///Log.d(MAIN_LOG_TAG, "DrawView...surfaceCreated");

        mTopYRowCoordsByNote.clear();

        mSurfaceWidth = getWidth(); /// TODO це перенести в інше місце (конструктор), якщо вийде

        mSurfaceHeight = getHeight();

        mLeftMarginForBorder = mSurfaceWidth / 3;

        ///Log.d("mLeftMarginForBorder", String.valueOf(mLeftMarginForBorder));

        mNoteFragmentHeight = mSurfaceHeight / 36;

        mNoteFragmentWidth = mSurfaceWidth / 30;

        mSustainNoteFragmentRedrawingPeriod = 10 * mNoteFragmentWidth;

        int currentRowTopYCoord;

        if (mTopYRowCoordsByNote.size() == 0) {
            for (int i = 0; i < mNotes.length; i++) {
                currentRowTopYCoord = i * mNoteFragmentHeight;

                mTopYRowCoordsByNote.put(mNotes[i], currentRowTopYCoord);
            }
        }

        mSurfaceHolder = holder;

        initializeAndScheduleRedrawing();

        /// TODO працє!
        mMelodyMediaPlayer = new MediaPlayer();
        mMelodyMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            ///Log.d("media setting source", mMelodyURL);

            mMelodyMediaPlayer.setDataSource(mMelodyURL);
            ///mMelodyMediaPlayer.setDataSource("http://midimelody.ru/midi.php?str=%2Fl%2FLinkin%20Park%2FLinkin%20Park%20-%20Ntr_Mission%281%29.mid");

        } catch (IOException e) {
            e.printStackTrace();
        }
        /*mMelodyMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d("media", "prepared");
            }
        });*/

        /// TODO потім це прибрати! А MIDI передавати у контруктор цієї вюшки
        ///mMelodyMediaPlayer = MediaPlayer.create(getContext(), R.raw.testoff5);

        /// TODO чисто тетовий варіант! Брати музичку з сервера! 01.02.2018
        /*try {
            mMideProcessor = new MidiProcessor(new MidiFile(getResources().openRawResource(R.raw.testoff5)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // or listen for all events:
        mMideProcessor.registerEventListener(SoundGraphView.this, MidiEvent.class);

        mMideProcessor.start();*/
    }

    public void initializeAndScheduleRedrawing() {
        mNeedToStopCanvasRedrawing = false;

        mCanvasRedrawingTimer = new Timer();
        mCanvasRedrawingTimerTask = new RedrawCanvasTimerTask(mSurfaceHolder);
        mCanvasRedrawingTimer.schedule(mCanvasRedrawingTimerTask, 0, CANVAS_REDRAWING_TIMER_PERIOD);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        ///Log.d(MAIN_LOG_TAG, "DrawView...surfaceChanged");
    }

    public void setMidiInputStream (InputStream inputStream) {
        // Create a new MidiProcessor:

        /*try {
            mMideProcessor = new MidiProcessor(new MidiFile(new URL(mMelodyURL).openStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }*/

        /*try {
            File file = null;
            try {
                file = File.createTempFile("tempMelody", ".mid");
            } catch (IOException e) {
                e.printStackTrace();
            }
            OutputStream output = new FileOutputStream(file);
            try {
                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                int read;

                while ((read = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }

                FileInputStream fis = new FileInputStream(file);

                mMelodyMediaPlayer = new MediaPlayer();
                mMelodyMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMelodyMediaPlayer.setDataSource(fis.getFD());
                mMelodyMediaPlayer.prepare();

                mMideProcessor = new MidiProcessor(new MidiFile(file));

                mMideProcessor.registerEventListener(SoundGraphView.this, MidiEvent.class);

                output.flush();
            } finally {
                output.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        try {
            mMideProcessor = new MidiProcessor(new MidiFile(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // or listen for all events:
        mMideProcessor.registerEventListener(SoundGraphView.this, MidiEvent.class);
    }

    public void prepareMediaPlayer () {
        try {
            mMelodyMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        ///Log.d(MAIN_LOG_TAG, "DrawView...surfaceDestroyed");
    }

    public void launchMideProcessor () {
        mMideProcessor.start();
    }

    public void checkIfTheNoteAndPaintIt(String noteName) {
        if (mTopYRowCoordsByNote.containsKey(noteName))
            /// TODO зробити різницю фрагментів голосу і мелодії

            mCanvasRedrawingTimerTask.checkVoiceNoteFragmentAccuracy(noteName);

    }

    public void resumeMelodyPlaying () {
        mMelodyMediaPlayer.start();
    }

    private class RedrawCanvasTimerTask extends TimerTask {
        private SurfaceHolder surfaceHolder;

        private Canvas canvas;

        private Paint gridPaint;

        private Paint correctVoicePaint;

        private Paint uncorrectNotePaint;

        private Paint uncorrectRhytmPaint;

        private Paint correctMelodyPaint;

        private ArrayList<NoteFragment> currentNoteFragments;

        private MelodyRectangleNoteFragment currentCorrectRhytmMelodyNoteFragment;

        private abstract class NoteFragment {
            /// TODO замінити інти на шорти?!
            protected int[] figureCoordinates;

            protected NoteFragmentCorrectness noteFragmentCorrectnessState;

            protected abstract void moveNoteFragment();

            public NoteFragmentCorrectness getNoteFragmentCorrectnessState() {
                return noteFragmentCorrectnessState;
            }
        }

        private class VoiceCircleNoteFragment extends NoteFragment {
            private VoiceCircleNoteFragment(String note, NoteFragmentCorrectness state) {
                int circleR = mNoteFragmentHeight / 2;

                figureCoordinates = new int[]{mLeftMarginForBorder/* - circleR*/, mTopYRowCoordsByNote.get(note) + circleR, circleR};

                noteFragmentCorrectnessState = state;
            }

            @Override
            protected void moveNoteFragment() {
                int x = figureCoordinates[0];

                int y = figureCoordinates[1];

                int r = figureCoordinates[2];

                x -= NOTE_FRAGMENTS_MOVING_DELTA;

                figureCoordinates = new int[]{x, y, r};
            }

            private int getXCoordForRemoving() {
                return figureCoordinates[0];
            }
        }

        private class MelodyRectangleNoteFragment extends NoteFragment {
            private Rect rectangle;

            private int sequenceNumber;

            private boolean isCurrentlyCorrect;

            private boolean isAlreadyPainted;

            private MelodyRectangleNoteFragment(String note, int sequenceNumber) {
                int topYCoord = mTopYRowCoordsByNote.get(note);

                figureCoordinates = new int[]{mSurfaceWidth, topYCoord, mSurfaceWidth + mNoteFragmentWidth, topYCoord + mNoteFragmentHeight};

                MelodyRectangleNoteFragment.this.rectangle = new Rect(mSurfaceWidth, topYCoord, mSurfaceWidth + mNoteFragmentWidth, topYCoord + mNoteFragmentHeight);

                MelodyRectangleNoteFragment.this.sequenceNumber = sequenceNumber;
            }

            @Override
            protected void moveNoteFragment() {
                int x1 = figureCoordinates[0];

                int y1 = figureCoordinates[1];

                int x2 = figureCoordinates[2];

                int y2 = figureCoordinates[3];

                x1 -= NOTE_FRAGMENTS_MOVING_DELTA;

                x2 -= NOTE_FRAGMENTS_MOVING_DELTA;

                if (/*x1 > 135 && x1 < 155*/x1 > mLeftMarginForBorder - 25 && x1 < mLeftMarginForBorder - 5) { /// TODO покращити тут умову правильності
                    ///Log.d("New currently correct melody note fragment", "number : " + sequenceNumber);

                    currentCorrectRhytmMelodyNoteFragment = MelodyRectangleNoteFragment.this;

                    isCurrentlyCorrect = true;
                } else {
                    if (isCurrentlyCorrect) {
                        if (!isAlreadyPainted) {
                            if (x1 < mLeftMarginForBorder - 25) { /// якщо був правильним фрагментом, але вже вийшов за межу
                                setNoteFragmentCorrectness(NoteFragmentCorrectness.INCORRECT_RHYTM);

                                isCurrentlyCorrect = false;
                            }
                        }
                    }
                }

                /// TODO зробити, щоб при перетині певного рубікону стан фрагменту ноти мелодії змінювався на червоний (неправильний ритм)

                figureCoordinates = new int[]{x1, y1, x2, y2};

                rectangle = new Rect(x1, y1, x2, y2);
            }

            private int getX1Coord() {
                return figureCoordinates[0];
            }

            private int getX2Coord() {
                return figureCoordinates[2];
            }

            private int getY1Coord() {
                return figureCoordinates[1];
            }

            private int getY2Coord() {
                return figureCoordinates[3];
            }

            private int getSequenceNumber() {
                ///Log.d("sequence_number", String.valueOf(sequenceNumber));

                return MelodyRectangleNoteFragment.this.sequenceNumber;
            }

            private void setNoteFragmentCorrectness(NoteFragmentCorrectness state) {
                MelodyRectangleNoteFragment.this.noteFragmentCorrectnessState = state;
            }

            private void setIsAlreadyPainted(boolean value) {
                MelodyRectangleNoteFragment.this.isAlreadyPainted = value;
            }
        }

        private RedrawCanvasTimerTask(SurfaceHolder surfaceHolder) {
            ///Log.d(MAIN_LOG_TAG, "RedrawCanvasTimerTask...RedrawCanvasTimerTask");

            RedrawCanvasTimerTask.this.surfaceHolder = surfaceHolder;

            RedrawCanvasTimerTask.this.gridPaint = new Paint();
            RedrawCanvasTimerTask.this.gridPaint.setStrokeWidth(2);
            RedrawCanvasTimerTask.this.gridPaint.setTextSize(20);
            RedrawCanvasTimerTask.this.gridPaint.setColor(Color.parseColor("#5A5655"));

            RedrawCanvasTimerTask.this.correctVoicePaint = new Paint();
            correctVoicePaint.setColor(Color.parseColor("#00E200"));

            RedrawCanvasTimerTask.this.uncorrectNotePaint = new Paint();
            uncorrectNotePaint.setColor(Color.parseColor("#FFFE01"));

            RedrawCanvasTimerTask.this.uncorrectRhytmPaint = new Paint();
            uncorrectRhytmPaint.setColor(Color.parseColor("#FF0008"));

            RedrawCanvasTimerTask.this.correctMelodyPaint = new Paint();
            correctMelodyPaint.setColor(Color.parseColor("#0000FE"));

            currentNoteFragments = new ArrayList<>();
        }

        @Override
        public void run() {
            Log.d(MAIN_LOG_TAG, "RedrawCanvasTimerTask : " + this + "...run...trying to redraw canvas");

            /// trying to get the current canvas from the surface view

            canvas = null;

            try {
                canvas = surfaceHolder.lockCanvas(null);

                if (canvas == null)
                    return;

                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                if (mNeedToStopCanvasRedrawing) {
                    ///Log.d(MAIN_LOG_TAG, "RedrawCanvasTimerTask...run...stopping redrawing canvas");

                    cancel();

                    mCanvasRedrawingTimer.cancel();

                    mCanvasRedrawingTimerTask = null;

                    mCanvasRedrawingTimer = null;

                    return;
                }

                int y1y2RowCoord;

                String text;

                for (int i = 0; i < 36; i++) {
                    y1y2RowCoord = (mNoteFragmentHeight * i);

                    canvas.drawLine(0, y1y2RowCoord, mSurfaceWidth, y1y2RowCoord, gridPaint);

                    text = mNotes[i];

                    Rect bounds = new Rect();
                    RedrawCanvasTimerTask.this.gridPaint.getTextBounds(text, 0, text.length(), bounds);
                    int height = bounds.height();

                    canvas.drawText(text, 5, y1y2RowCoord + mNoteFragmentHeight / 2 + height / 2, gridPaint);
                }

                canvas.drawLine(mLeftMarginForBorder, 0, mLeftMarginForBorder, mSurfaceHeight, gridPaint);

                for (int i = 0; i < currentNoteFragments.size(); i++) {
                    NoteFragment iNoteFragment = currentNoteFragments.get(i);

                    iNoteFragment.moveNoteFragment();

                    if (iNoteFragment instanceof MelodyRectangleNoteFragment) {
                        MelodyRectangleNoteFragment iMelodyFragment = (MelodyRectangleNoteFragment) iNoteFragment;

                        NoteFragmentCorrectness melodyNoteState = iMelodyFragment.getNoteFragmentCorrectnessState();

                        if (melodyNoteState == null || melodyNoteState == NoteFragmentCorrectness.CORRECT)
                            canvas.drawRect(iMelodyFragment.rectangle, correctMelodyPaint);
                        else if (melodyNoteState == NoteFragmentCorrectness.INCORRECT_NOTE)
                            canvas.drawRect(iMelodyFragment.rectangle, uncorrectNotePaint);
                        else
                            canvas.drawRect(iMelodyFragment.rectangle, uncorrectRhytmPaint);

                        int iNoteFragmentX1Coord = ((MelodyRectangleNoteFragment) iNoteFragment).getX1Coord(); /// для вчасного старту плеєра

                        if (iNoteFragmentX1Coord == mLeftMarginForBorder && mNeedToStartMelodyPlaying) {
                            ///Log.d("media", "start");

                            mMelodyMediaPlayer.start();

                            mNeedToStartMelodyPlaying = false;
                        }

                        ///Log.d("getSequenceNumber", String.valueOf(iMelodyFragment.getSequenceNumber()) + ", mTotalMelodyNoteFragmentsCount : " + mTotalMelodyNoteFragmentsCount);

                        if (!mSingProcessFinished) {
                            if (iMelodyFragment.getSequenceNumber() == mTotalMelodyNoteFragmentsCount &&
                                    iMelodyFragment.getX2Coord() <= mLeftMarginForBorder) {
                                mSingProcessFinished = true;

                                mParentActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                   /*float i1 = mFullyCorrectVoiceNoteFragmentsCount;

                                   float i2 = mTotalMelodyNoteFragmentsCount;

                                   float f3 = i1 / i2;

                                   int i4 = (int) (f3 * 100);*/

                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mParentActivity.cancelSoundCheckingAndAudioRecording();

                                                stopMediaPlayer();

                                                stopAndResetMideProcessor();

                                                mNeedToStopCanvasRedrawing = true;

                                                mParentActivity.findViewById(R.id.headerContainer_rellay_ASP).setVisibility(View.GONE);

                                                mParentActivity.findViewById(R.id.bottomBlackSpace_relLay_ASP).setVisibility(View.GONE);

                                                /*Log.d("Counts", "mTotalMelodyNoteFragmentsCount : " + mTotalMelodyNoteFragmentsCount
                                                                          + "; mRhytmCorrectVoiceNoteFragmentsCount : " + mRhytmCorrectVoiceNoteFragmentsCount
                                                                          + "; mFullyCorrectVoiceNoteFragmentsCount : " + mFullyCorrectVoiceNoteFragmentsCount);*/

                                                int rhytmAccuracyPercentage = (int) (float) (((float) mRhytmCorrectVoiceNoteFragmentsCount / (float) mTotalMelodyNoteFragmentsCount) * 100);

                                                int pitchAccuracyPercentage = (int) (float) (((float) mFullyCorrectVoiceNoteFragmentsCount / (float) mTotalMelodyNoteFragmentsCount) * 100);

                                                TextView pitchingTxt = mParentActivity.findViewById(R.id.pitchingScore_txtv_ASP);
                                                pitchingTxt.setText("PITCHING " + pitchAccuracyPercentage + "%");

                                                TextView rhytmTxt = mParentActivity.findViewById(R.id.rhytmScore_txtv_ASP);
                                                rhytmTxt.setText("RHYTM " + rhytmAccuracyPercentage + "%");

                                                mParentActivity.findViewById(R.id.starTableContainer_relLay_ASP).setVisibility(View.VISIBLE);

                                                mParentActivity.findViewById(R.id.restartSingingProcess_butt_ASP).setVisibility(View.VISIBLE);

                                                mParentActivity.findViewById(R.id.vocalAthletes_butt_ASP).setVisibility(View.VISIBLE);

                                                mParentActivity.findViewById(R.id.bottomBlackSpace_relLay_ASP).setVisibility(View.VISIBLE);

                                                Toast.makeText(mParentActivity, "Rhytm score is " +  rhytmAccuracyPercentage +  " and pitch score is " + pitchAccuracyPercentage, Toast.LENGTH_LONG);

                                                mParentActivity.onUploadScores(rhytmAccuracyPercentage, pitchAccuracyPercentage);

                                                mParentActivity.onVideoRecordingEnded();
                                            }
                                        }, 4000);
                                    }
                                });
                            }
                        }

                        int iMelodyNoteFragmentX2Coord = iMelodyFragment.getX2Coord();

                        if (iMelodyNoteFragmentX2Coord == 0)
                            currentNoteFragments.remove(iNoteFragment);
                    } else {
                        VoiceCircleNoteFragment iVoiceFragment = (VoiceCircleNoteFragment) iNoteFragment;

                        int circleCoordinates[] = iVoiceFragment.figureCoordinates;

                        int circleXCoord = circleCoordinates[0];

                        int circleYCoord = circleCoordinates[1];

                        int circleR = circleCoordinates[2];

                        NoteFragmentCorrectness vocalNoteState = iVoiceFragment.getNoteFragmentCorrectnessState();

                        if (vocalNoteState == NoteFragmentCorrectness.CORRECT)
                            canvas.drawCircle(circleXCoord, circleYCoord, circleR, correctVoicePaint);
                        else if (vocalNoteState == NoteFragmentCorrectness.INCORRECT_NOTE)
                            canvas.drawCircle(circleXCoord, circleYCoord, circleR, uncorrectNotePaint);
                        else
                            canvas.drawCircle(circleXCoord, circleYCoord, circleR, uncorrectRhytmPaint);

                        int iVoiceFragmentXCoord = iVoiceFragment.getXCoordForRemoving();

                        if (iVoiceFragmentXCoord == 0) /// TODO тут покращити умову, щоб не було видно пропадання кружка
                            currentNoteFragments.remove(iNoteFragment);
                    }

                }
            } finally {
                if (canvas != null) {
                    /// posting the canvas to the surface view

                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

        private void checkVoiceNoteFragmentAccuracy(String noteName) {
            int circleR = mNoteFragmentHeight / 2;

            int futureCircleCenterXCoord = mLeftMarginForBorder - circleR;

            int futureCircleCenterYCoord = mTopYRowCoordsByNote.get(noteName) + circleR;

            NoteFragmentCorrectness createdVoiceNoteState;

            if (currentCorrectRhytmMelodyNoteFragment != null) {

                NoteFragmentCorrectness newMelodyNoteState;

                if (futureCircleCenterXCoord > currentCorrectRhytmMelodyNoteFragment.getX1Coord() - 15 &&
                        futureCircleCenterXCoord < currentCorrectRhytmMelodyNoteFragment.getX2Coord() + 15) {
                    /// користувач попав в ритм

                    mRhytmCorrectVoiceNoteFragmentsCount++;

                    createdVoiceNoteState = NoteFragmentCorrectness.INCORRECT_NOTE;

                    newMelodyNoteState = NoteFragmentCorrectness.INCORRECT_NOTE;

                    if (futureCircleCenterYCoord > currentCorrectRhytmMelodyNoteFragment.getY1Coord() - 15 &&
                            futureCircleCenterYCoord < currentCorrectRhytmMelodyNoteFragment.getY2Coord() + 15) {
                        /// користувач попав ще й в ноту
                        createdVoiceNoteState = NoteFragmentCorrectness.CORRECT;

                        newMelodyNoteState = NoteFragmentCorrectness.CORRECT;

                        mFullyCorrectVoiceNoteFragmentsCount++;
                    }
                } else { /// коли не попав в ритм
                    createdVoiceNoteState = NoteFragmentCorrectness.INCORRECT_RHYTM;

                    newMelodyNoteState = NoteFragmentCorrectness.INCORRECT_RHYTM;
                }

                currentCorrectRhytmMelodyNoteFragment.setNoteFragmentCorrectness(newMelodyNoteState);

                currentCorrectRhytmMelodyNoteFragment.setIsAlreadyPainted(true);
            } else { /// коли в даний момент взагалі немає правильного фрагмента
                createdVoiceNoteState = NoteFragmentCorrectness.INCORRECT_RHYTM;
            }

            currentNoteFragments.add(new VoiceCircleNoteFragment(noteName, createdVoiceNoteState));
        }

        private void paintAndMoveMelodyNoteFragment(String noteName) {
            if (mTopYRowCoordsByNote.containsKey(noteName))
                /// TODO зробити різницю фрагментів голосу і мелодії
                currentNoteFragments.add(new MelodyRectangleNoteFragment(noteName, mTotalMelodyNoteFragmentsCount));

        }
    }

    @Override
    public void onStart(boolean fromBeginning) {

    }


    /// TODO при паузі все-таки спрацьовує onEvent і через це вилітає
    @Override
    public void onEvent(MidiEvent event, long ms) {
        ///Log.d("onEvent", String.valueOf(event) + "; " + mMideProcessor.isRunning());

        if (event instanceof NoteOn) {
            ///Log.d("NoteOn", String.valueOf(((NoteOn) event).getNoteValue()));

            final String noteName = NotesResolver.resolveNoteForMidiNumber(((NoteOn) event).getNoteValue());

            Log.d("note ON, noteName", noteName);

            Timer newNoteSustainTimer = new Timer();
            TimerTask newNoteSustainTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (!mNeedToStopCanvasRedrawing) {
                        mTotalMelodyNoteFragmentsCount++;

                        mCanvasRedrawingTimerTask.paintAndMoveMelodyNoteFragment(noteName);
                    }
                }
            };
            newNoteSustainTimer.schedule(newNoteSustainTimerTask, 0, mSustainNoteFragmentRedrawingPeriod);

            mCurrentMelodyNotesSustains.put(noteName, new MelodyNoteSustainStuff(newNoteSustainTimer, newNoteSustainTimerTask));
        } else if (event instanceof NoteOff) {
            /// TODO можна про всяк випадок зробити перевірку, чи є ця нота по номеру в списках

            String offingNoteName = NotesResolver.resolveNoteForMidiNumber(((NoteOff) event).getNoteValue());

            Log.d("note OFF, noteName", offingNoteName);

            MelodyNoteSustainStuff offingNoteMelodySustainStuff = mCurrentMelodyNotesSustains.get(offingNoteName);

            TimerTask offingNoteSustainTask = offingNoteMelodySustainStuff.getTimerTask();
            offingNoteSustainTask.cancel();

            Timer offingNoteSustainTimer = offingNoteMelodySustainStuff.getTimer();
            offingNoteSustainTimer.cancel();

            mCurrentMelodyNotesSustains.remove(offingNoteName);
        }
    }

    @Override
    public void onStop(boolean finished) {
        ///Log.d("mTotalMelodyNoteFragmentsCount", String.valueOf(mTotalMelodyNoteFragmentsCount));
    }

    /// TODO також зробити дзеркальний метод для резюма цих речей!
    public void stopAllRedrawingAndPauseMusic() {
        stopAllSustainsTasksAndTimers();

        mNeedToStopCanvasRedrawing = true;

        mParentActivity.cancelSoundCheckingAndAudioRecording();

        mMelodyMediaPlayer.pause();

        mMideProcessor.stop();
    }

    public void stopAndResetMideProcessor () {
        mMideProcessor.stop();

        mMideProcessor.reset();
    }

    public void stopMediaPlayer () {
        mMelodyMediaPlayer.stop();
    }

    public void setNeedToStartMelodyPlaying (boolean value) {
        mNeedToStartMelodyPlaying = value;
    }

    public void setSingProcessFinished (boolean value) {
        mSingProcessFinished = value;
    }

    private void stopAllSustainsTasksAndTimers() {
        for (Object value : mCurrentMelodyNotesSustains.values()) {
            MelodyNoteSustainStuff loopingMelodyNoteSustainStuff = (MelodyNoteSustainStuff) value;

            loopingMelodyNoteSustainStuff.getTimerTask().cancel();

            loopingMelodyNoteSustainStuff.getTimer().cancel();

            /// TODO тут можна запотіти і продовжувати всякі очитски mCurrentMelodyNotesSustains
        }
    }
}
