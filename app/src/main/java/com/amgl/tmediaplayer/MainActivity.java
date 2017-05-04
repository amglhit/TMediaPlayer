package com.amgl.tmediaplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amgl.mediaplayer.TMediaPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String URL = "http://220.181.117.183/187/28/92/letv-gug/14/ver_00_22-1051581402-avc-1507856-aac-96000-117151-23680505-2e0b3774490e51ac469db4313025b877-1466497857703.m3u8?crypt=13aa7f2e16900&b=169&nlh=4096&nlt=60&bf=8000&p2p=1&video_type=mp4&termid=0&tss=ios&platid=15&splatid=1502&its=0&qos=5&fcheck=0&amltag=8800&mltag=8800&proxy=1778917254&uid=172698987.rp&keyitem=GOw_33YJAAbXYE-cnQwpfLlv_b2zAkYctFVqe5bsXQpaGNn3T1-vhw..&ntm=1493824200&nkey=1c416d5954e75e583aebcd8526e09e00&nkey2=435a49d23766f73fd99587148ecbfb2f&geo=CN-1-7-666&mmsid=62824553&tm=1487400909&key=9a852aac5fe3a75d50ecea606f2fe34d&playid=0&vtype=58&cvid=719159742449&payff=0&errc=429&gn=820&vrtmcd=106&buss=8800&cips=10.75.45.107";

    @BindView(R.id.surface_view)
    SurfaceView mSurfaceView;

    @BindView(R.id.btn_prepare)
    Button mBtnPrepare;

    @BindView(R.id.text_duration)
    TextView mTextDuration;

    @BindView(R.id.text_position)
    TextView mTextPosition;

    private TMediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mMediaPlayer = new TMediaPlayer();

        mBtnPrepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlay();
            }
        });
    }

    private void startPlay() {
        mMediaPlayer.prepare(URL);
        mMediaPlayer.setDisplay(mSurfaceView.getHolder());
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
        mMediaPlayer.resume();
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
}
