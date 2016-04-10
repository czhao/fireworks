package com.garena.android.fireworks;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import bolts.Task;

/**
 * Stage for animation
 *
 * @author zhaocong
 */
public class NightScene extends SurfaceView implements AudioManager.OnAudioFocusChangeListener{

    private float sceneWidthHalf, sceneHeightHalf;
    private float densityDpi;

    public NightScene(Context context) {
        super(context);
        initDpi(context);
    }

    public NightScene(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDpi(context);
    }

    public NightScene(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDpi(context);
    }

    @TargetApi(21)
    public NightScene(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initDpi(context);
    }

    private List<SparkBase> sparks = new ArrayList<>();

    private ArrayList<SparkBase> recycleList = new ArrayList<>();

    float dpToMeterRatio; //dp per meter
    float pixelMeterRatio; //pixels per meter
    float sceneWidth, sceneDepth = 80f, sceneHeight = 200f; //expect to support scene with 200 m
    private boolean isShowOngoing = true, isSoundPoolReady, isSlientHintShown = false;
    private Random mRandom;
    protected SoundPool soundPool;
    private AudioManager audioManager;
    private ConcurrentLinkedQueue<Spark> waitingList = new ConcurrentLinkedQueue<>();

    private int mVolume;
    private int explosionSoundId = 0;
    private boolean isAudioResourceReady = true;

    private long mostRecentHintIssuedTime = 0L;

    private void initDpi(Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        densityDpi = metrics.densityDpi;
    }

    protected void init(){
        //add the sparks
        dpToMeterRatio =  pixelToDp(getHeight()) / sceneHeight;
        pixelMeterRatio = getHeight() / sceneHeight;
        sceneWidth = pixelToDp(getWidth()) / dpToMeterRatio; //dynamically calculate the width in meters
        sceneWidthHalf = sceneWidth  / 2;
        sceneHeightHalf = sceneHeight / 2;
        mRandom = new Random();

        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        // Request audio focus for playback
        int result = audioManager.requestAudioFocus(this,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            isSoundPoolReady = true;
        }

        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(5);
            AudioAttributes.Builder attributeBuilder = new AudioAttributes.Builder();
            attributeBuilder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
            attributeBuilder.setUsage(AudioAttributes.USAGE_GAME);
            attributeBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            attributeBuilder.setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED);
            builder.setAudioAttributes(attributeBuilder.build());
            soundPool = builder.build();
        }else{
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
        }


        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                isAudioResourceReady = false;
                playLoadedExplosionStream();
            }
        });
    }

    protected void addSpark(SparkBase base){
        sparks.add(base);
    }

    protected void randomFire(){
        long time = System.currentTimeMillis() - lastFireTime;
        //implement basic weak boundary check
        if (time > 100){
            float x =  (-mRandom.nextFloat() * sceneWidth * .5f + sceneWidthHalf) * .2f;
            float y =  -mRandom.nextFloat() * 30;
            float z = -mRandom.nextFloat() * sceneDepth /4 - sceneDepth /2;
            Point3f pos = new Point3f(x, y, z);

            //the vertical speed cannot be faster than the frame rate
            Vector3f v = new Vector3f(0, 6f, 0);

            long random  = time % 5;

            if (random == 1L) {
                waitingList.add(new Spark(pos, v));
            }else if (random == 2L){
                waitingList.add(new GroupSpark(pos, v));
            }else if (random == 3L){
                waitingList.add(new BallSpark(pos, v));
            }else if (random == 4L){
                waitingList.add(new BallSpark(pos, v));
            }else{
                waitingList.add(new GroupSpark(pos, v));
            }

            lastFireTime = System.currentTimeMillis();
        }
    }

    long time;
    long lastFireTime = 0;

    protected void stop(){
        isShowOngoing = false;
        if (soundPool != null){
            soundPool.autoPause();
            isSoundPoolReady = false;
            isAudioResourceReady = false;
        }
    }

    protected void play(){
        time = System.currentTimeMillis();
        isShowOngoing = true;

        new Thread(){
            @Override
            public void run() {
                while (isShowOngoing) {
                    int screenHeight = getHeight();
                    long newTime = System.currentTimeMillis();
                    long timeDelta = newTime - time;
                    Canvas canvas = getHolder().lockCanvas();
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    for (SparkBase s : sparks) {
                        if (s.isExploding()) {
                            recycleList.add(s);
                        } else {
                            PhysicsEngine.move(s, timeDelta);
                            //convert 3D to 2D
                            float scale = 1.5f * sceneDepth / (sceneDepth + s.mPosition.z);
                            float x2d = s.mPosition.x * scale + sceneWidthHalf;
                            float y2d = s.mPosition.y * scale + sceneHeightHalf;
                            s.draw(canvas, (int)(x2d * pixelMeterRatio), screenHeight - (int)(y2d * pixelMeterRatio),scale, true);
                        }
                    }
                    sparks.removeAll(recycleList);
                    for (SparkBase s : recycleList) {
                        s.onExplosion(NightScene.this);
                    }
                    recycleList.clear();
                    if (sparks.size() > 0) {
                        //do nothing
                        mostRecentHintIssuedTime = newTime;
                    } else {
                        //randomFire();
                        try {
                            //60fps if possible
                            Thread.sleep(16);
                        } catch (Exception e) {
                            //DO NOTHING
                        }
                        if (newTime - mostRecentHintIssuedTime > 5000L){
                            showClappingHint(newTime);
                        }
                    }
                    //randomFire();
                    time = newTime;
                    getHolder().unlockCanvasAndPost(canvas);
                    while (waitingList.size() > 0){
                        //remove the item
                        sparks.add(waitingList.poll());
                    }
                }
            }
        }.start();
    }

    /**
     * This is usually called via the background thread to play the sound effect
     */
    protected void playExplosionSound() {
        if (!isSoundPoolReady) {
            return;
        }

        if (!isAudioResourceReady || explosionSoundId == 0) {
            Task.call(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    mVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    if (mVolume <= 0) {
                        if (!isSlientHintShown) {
                            Toast.makeText(getContext(), R.string.open_audio_hint, Toast.LENGTH_SHORT).show();
                            isSlientHintShown = true;
                        }
                        return null; // do nothing to save the memory
                    }
                    explosionSoundId = soundPool.load(getContext(), R.raw.explosion_fuzz, 1);
                    return null;
                }
            }, Task.BACKGROUND_EXECUTOR);
        }else{
            Task.call(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    //play the sound effect directly
                    playLoadedExplosionStream();
                    return null;
                }
            },Task.UI_THREAD_EXECUTOR);
        }
    }

    private void playLoadedExplosionStream(){
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float normalized = (float) mVolume / (float) maxVolume;
        soundPool.play(explosionSoundId,normalized,normalized,1, 0, 1.2f);
    }

    protected void onDestroy(){
        if (soundPool != null){
            soundPool.release();
        }
    }

    private void showClappingHint(long newTime){
        Task.call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Toast.makeText(getContext(), R.string.make_some_noise, Toast.LENGTH_SHORT).show();

                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
        mostRecentHintIssuedTime = newTime;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        isSoundPoolReady = focusChange == AudioManager.AUDIOFOCUS_GAIN;
    }

    private float pixelToDp(float px){
        return px / (densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
