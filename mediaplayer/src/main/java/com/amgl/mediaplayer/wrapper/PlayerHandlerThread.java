package com.amgl.mediaplayer.wrapper;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

/**
 * Created by 阿木 on 2017/5/10.
 */

public class PlayerHandlerThread extends HandlerThread {
    private final PlayerHandler mPlayerHandler;

    public PlayerHandlerThread(String name) {
        super(name);
        start();
        mPlayerHandler = new PlayerHandler(this.getLooper());
    }

    private class PlayerHandler extends Handler {
        public PlayerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

}
