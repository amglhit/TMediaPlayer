package com.amgl.tmediaplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;

import com.amgl.mediaplayer.controller.TPlayerController;
import com.amgl.mediaplayer.player.IPlayer;
import com.amgl.mediaplayer.VideoPlayerManager;

import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    public static final String URL = "http://101.28.249.57/v.cctv.com/flash/mp4video6/TMS/2011/01/05/cf752b1c12ce452b3040cab2f90bc265_h264818000nero_aac32-1.mp4?wsiphost=local";//"https://xiaomor.com/demo/test.mp4";//

    @BindView(R.id.surface_view)
    SurfaceView mSurfaceView;

    @BindView(R.id.surface_view_2)
    SurfaceView mSurfaceView2;

    @BindView(R.id.btn_prepare)
    Button mBtnPrepare;

    @BindView(R.id.text_duration)
    TextView mTextDuration;

    @BindView(R.id.text_position)
    TextView mTextPosition;

    @BindView(R.id.view_controller)
    TPlayerController mTPlayerController;

    private IPlayer mMediaPlayer;

    private VideoPlayerManager mLifecycleWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mLifecycleWrapper = new VideoPlayerManager();
        mLifecycleWrapper.onCreate();

        mMediaPlayer = mLifecycleWrapper.getPlayer();

        mTPlayerController.setPlayer(mMediaPlayer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLifecycleWrapper.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLifecycleWrapper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLifecycleWrapper.onStop();
    }

    @OnClick(R.id.btn_init)
    void init() {
//        TrustManager[] trustAllCerts = new TrustManager[]
//                {new X509TrustManager() {
//                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                        return null;
//                    }
//
//                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
//                    }
//
//                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
//                    }
//                }
//                };
//
//        try {
//            SSLContext sc = SSLContext.getInstance("SSL"); // "TLS" "SSL"
//            sc.init(null, trustAllCerts, null);
//            // sc.init( null, trustAllCerts, new java.security.SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//            HttpsURLConnection.setDefaultHostnameVerifier(
//                    new HostnameVerifier() {
//                        public boolean verify(String hostname, SSLSession session) {
//                            return true;
//                        }
//                    });
//        } catch (Exception e) {
//            Timber.e(e);
//        }
        mMediaPlayer.setDataSource(URL);
    }

    @OnClick(R.id.btn_prepare)
    void prepare() {
        mMediaPlayer.prepare();
    }

    @OnClick(R.id.btn_start)
    void start() {
        mMediaPlayer.start();
    }

    @OnClick(R.id.btn_stop)
    void stop() {
        mMediaPlayer.stop();
    }

    @OnClick(R.id.btn_pause)
    void pause() {
        mMediaPlayer.pause();
    }

    @OnClick(R.id.btn_resume)
    void resume() {
        mMediaPlayer.resume(true);
    }

    @OnClick(R.id.btn_release)
    void release() {
        mMediaPlayer.release();
    }

    @OnClick(R.id.btn_reset)
    void reset() {
        mMediaPlayer.reset();
    }

    @OnClick(R.id.btn_position)
    void getCurrent() {
        int current = mMediaPlayer.getCurrentPosition();
        mTextPosition.setText("current: " + current);
    }

    @OnClick(R.id.btn_duration)
    void getDuration() {
        int duration = mMediaPlayer.getDuration();
        mTextDuration.setText("total: " + duration);
    }

    @OnClick(R.id.btn_seek)
    void seek() {
        int from = mMediaPlayer.getCurrentPosition();
        int total = mMediaPlayer.getDuration();
        int to = Math.min(from + 1000, total);

        mMediaPlayer.seekTo(to, false);
    }

    @OnClick(R.id.btn_display_1)
    void display1() {
        mLifecycleWrapper.setSurfaceView(mSurfaceView);
    }

    @OnClick(R.id.btn_display_2)
    void display2() {
        mLifecycleWrapper.setSurfaceView(mSurfaceView2);
    }

    @OnClick(R.id.btn_restart)
    void restart(){
        init();
        prepare();
    }
}
