package com.garena.android.fireworks;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowManager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HomeActivity extends AppCompatActivity{

    NightScene mNightScene;
    SurfaceHolder mSurfaceHolder;
    boolean isSurfaceCreated = false;

    private AudioMonitorTask recordingTask;

    private final static String TAG = "scene";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mNightScene = (NightScene)findViewById(R.id.night_scene);
        mSurfaceHolder = mNightScene.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mNightScene.init();
                mNightScene.play();
                isSurfaceCreated = true;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                isSurfaceCreated = false;
                mNightScene.stop();
            }
        });

        //keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        if (PackageManager.PERMISSION_GRANTED == permissionCheck) {
            recordingTask = new AudioMonitorTask();
            recordingTask.execute();
        }else{
            //request for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_RECORD_AUDIO);
        }

        //generate the sparks
        if (isSurfaceCreated) {
            mNightScene.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mNightScene.play();
                }
            }, 2000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNightScene.stop();

        if (recordingTask != null){
            recordingTask.cancel(true);
            recordingTask = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNightScene.onDestroy();
    }

    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 1221;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //start the recording task
                recordingTask = new AudioMonitorTask();
                recordingTask.execute();
            }
        }
    }

    //parameters for audio capture
    final int frequency = 8000; //frequency for radio capture
    final int windowSize = 15; //magic number
    int channelConfiguration = AudioFormat.CHANNEL_IN_DEFAULT;
    int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    final int blockSize = 256;
    private AudioRecord audioRecord;
    final long COOL_DOWN = 400;
    final int SAMPLE_TOTAL = 100;

    private class AudioMonitorTask extends AsyncTask<Void, double[], Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioFormat);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                AudioRecord.Builder builder = new AudioRecord.Builder();
                builder.setAudioSource(MediaRecorder.AudioSource.MIC);
                builder.setBufferSizeInBytes(bufferSize);
                AudioFormat.Builder formatBuilder = new AudioFormat.Builder();
                formatBuilder.setChannelIndexMask(channelConfiguration);
                formatBuilder.setEncoding(audioFormat);
                formatBuilder.setSampleRate(frequency);
                builder.setAudioFormat(formatBuilder.build());
                audioRecord = builder.build();
            }else{
                audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration,
                        audioFormat, bufferSize);
            }


            int bufferReadResult;
            short[] buffer = new short[blockSize];
            try {
                audioRecord.startRecording();
            } catch (IllegalStateException e) {
                Log.e(TAG, e.toString());

            }

            boolean isInitialCompleted = false;
            int windowEdge = 0, firstToClear = 0;

            int cacheSize = windowSize * 2;
            double[] cache = new double[cacheSize];
            double sum = 0;
            double minE = 100;
            double maxE = -100;
            int sampleCount = 0;
            double[] samples = new double[SAMPLE_TOTAL];

            boolean isInBurst = false;
            double threshold = 0, mean = 0;
            double burstPeak = 0;
            boolean coolDown = false;
            long coolDownExpiry = 0;
            boolean isSampling = true;
            //preparation
            while (true) {
                if (isCancelled()){
                    Log.d(TAG, "Cancelling the RecordTask");
                    break;
                } else {
                    bufferReadResult = audioRecord.read(buffer, 0, blockSize);

                    int n = Math.min(blockSize, bufferReadResult);
                    if (n == 0){
                        continue;
                    }
                    double doubleN = (double)n;

                    double amp = 0;
                    for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
                        amp += buffer[i];
                    }
                    amp /= doubleN;

                    //compute the e
                    double e = 0;

                    for (int i = 0; i < n; i++){
                        e += amp * amp * (i + bufferReadResult * doubleN);
                    }

                    e = e / n;

                    if (isInitialCompleted){
                        cache[windowEdge] = e;
                        //compute the new sum
                        sum += e;
                        sum -= cache[firstToClear];

                        minE = Math.min(sum, minE);
                        maxE = Math.max(maxE, sum - minE + 1e10);
                        double normalized = (sum - minE) / maxE;

                        if (isSampling){
                            samples[sampleCount] = normalized;
                            //perform estimation for the environment
                            sampleCount++;
                            if (sampleCount == SAMPLE_TOTAL){
                                isSampling = false;
                                //calculate the standard deviation
                                double sumOfSample = 0, intermediateSum = 0;
                                for (int i = 0; i < sampleCount; i++){
                                    sumOfSample += samples[i];
                                }
                                mean = sumOfSample / sampleCount;
                                for (int i = 0; i < sampleCount; i++){
                                    intermediateSum += Math.pow(samples[i] - mean, 2);
                                }
                                threshold = Math.sqrt(intermediateSum/sampleCount);
                                Log.i(TAG, "threshold benchmarking done:"+threshold);
                            }
                        }

                        if (coolDown && System.currentTimeMillis() - coolDownExpiry > COOL_DOWN){
                            coolDown = false;
                        }

                        if (!isSampling && !coolDown) {
                            //compute the gradient
                            if (!isInBurst && normalized - mean > 3 * threshold){
                                isInBurst = true;
                                burstPeak = normalized;
                                Log.i(TAG, "normalized:" + normalized + " threshold:"+threshold);
                            }else if (isInBurst && burstPeak < normalized){
                                burstPeak = normalized;
                            }else if (isInBurst && burstPeak > normalized){
                                publishProgress(null);
                                isInBurst = false;
                                coolDown = true;
                                coolDownExpiry = System.currentTimeMillis() + COOL_DOWN;
                            }
                        }
                        firstToClear++;
                        windowEdge++;
                    }else{
                        //not completed yet, push the window edge only
                        cache[windowEdge] = e;
                        windowEdge++;
                        if (windowEdge == windowSize){
                            isInitialCompleted = true;
                            //initialize the sum
                            for (int i = 0; i < windowSize; i++){
                                sum += cache[i];
                            }
                        }
                    }
                    //publishProgress(toTransform);
                }

                windowEdge = windowEdge % cacheSize;
                firstToClear = firstToClear % cacheSize;
            }

            return true;
        }
        @Override
        protected void onProgressUpdate(double[]...progress) {
            Log.i(TAG, "Clap!");
            if (mNightScene != null){
                mNightScene.randomFire();
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            releaseAudioRecorder();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            releaseAudioRecorder();
        }
    }

    private void releaseAudioRecorder(){
        if (audioRecord != null){
            try {
                audioRecord.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } finally {
                audioRecord.release();
                audioRecord = null;
            }

        }
    }
}
