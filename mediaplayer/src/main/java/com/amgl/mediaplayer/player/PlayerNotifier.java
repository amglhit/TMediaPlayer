package com.amgl.mediaplayer.player;

import android.os.Handler;
import android.os.Looper;

import com.amgl.mediaplayer.listener.IOnPreparedListener;
import com.amgl.mediaplayer.listener.IPlayerListener;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by 阿木 on 2017/5/10.
 */

public class PlayerNotifier {
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private final List<IPlayerListener> mPlayerListeners = new ArrayList<>();
    private final List<IOnPreparedListener> mOnPreparedListeners = new ArrayList<>();

    public void addOnPreparedListener(IOnPreparedListener listener) {
        synchronized (mOnPreparedListeners) {
            mOnPreparedListeners.add(listener);
        }
    }

    public void removeOnPreparedListener(IOnPreparedListener listener) {
        synchronized (mOnPreparedListeners) {
            mOnPreparedListeners.remove(listener);
        }
    }

    public void addPlayerListener(IPlayerListener playerListener) {
        synchronized (mPlayerListeners) {
            mPlayerListeners.add(playerListener);
        }
    }

    public void removePlayerListener(IPlayerListener playerListener) {
        synchronized (mPlayerListeners) {
            mPlayerListeners.remove(playerListener);
        }
    }

    public void notifyPrepareStart() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mOnPreparedListeners) {
                    for (IOnPreparedListener listener : mOnPreparedListeners) {
                        try {
                            listener.onPrepareStart();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Timber.w(e.getMessage());
                        }
                    }
                }
            }
        });
    }

    public void notifyPrepareEnd(final int position) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mOnPreparedListeners) {
                    for (IOnPreparedListener listener : mOnPreparedListeners) {
                        try {
                            listener.onPrepared(position);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Timber.w(e.getMessage());
                        }
                    }
                }
            }
        });
    }

    public void notifyBufferingStart() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mPlayerListeners) {
                    for (IPlayerListener listener : mPlayerListeners) {
                        try {
                            listener.onBufferingStart();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Timber.w(e.getMessage());
                        }
                    }
                }
            }
        });
    }

    public void notifyBufferingEnd() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mPlayerListeners) {
                    for (IPlayerListener listener : mPlayerListeners) {
                        try {
                            listener.onBufferingEnd();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Timber.w(e.getMessage());
                        }
                    }
                }
            }
        });
    }

    public void notifyPlayStart() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mPlayerListeners) {
                    for (IPlayerListener listener : mPlayerListeners) {
                        try {
                            listener.onStart();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Timber.w(e.getMessage());
                        }
                    }
                }
            }
        });
    }

    public void notifyPlayStop() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mPlayerListeners) {
                    for (IPlayerListener listener : mPlayerListeners) {
                        try {
                            listener.onStop();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Timber.w(e.getMessage());
                        }
                    }
                }
            }
        });
    }

    public void notifyPlayPaused() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mPlayerListeners) {
                    for (IPlayerListener listener : mPlayerListeners) {
                        try {
                            listener.onPaused();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Timber.w(e.getMessage());
                        }
                    }
                }
            }
        });
    }

    public void notifyPlayComplete() {
        synchronized (mPlayerListeners) {
            for (IPlayerListener listener : mPlayerListeners) {
                try {
                    listener.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    Timber.w(e.getMessage());
                }
            }
        }
    }

    public void notifyError() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mPlayerListeners) {
                    for (IPlayerListener listener : mPlayerListeners) {
                        try {
                            listener.onError();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Timber.w(e.getMessage());
                        }
                    }
                }
            }
        });
    }

    public void notifySeekComplete(final boolean start) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mPlayerListeners) {
                    for (IPlayerListener listener : mPlayerListeners) {
                        try {
                            listener.onSeekComplete(start);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Timber.w(e.getMessage());
                        }
                    }
                }
            }
        });
    }

    public void notifyRenderingStart() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mPlayerListeners) {
                    for (IPlayerListener listener : mPlayerListeners) {
                        try {
                            listener.onFirstFrameAppeared();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Timber.w(e.getMessage());
                        }
                    }
                }
            }
        });
    }

    public void notifyPlayerReset() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mPlayerListeners) {
                    for (IPlayerListener listener : mPlayerListeners) {
                        try {
                            listener.onReset();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Timber.w(e.getMessage());
                        }
                    }
                }
            }
        });
    }

    public void notifyProgress(final int progress) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mPlayerListeners) {
                    for (IPlayerListener listener : mPlayerListeners) {
                        try {
                            listener.onProgress(progress);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Timber.w(e.getMessage());
                        }
                    }
                }
            }
        });
    }

    public void notifyBuffering(final int percent) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mPlayerListeners) {
                    for (IPlayerListener listener : mPlayerListeners) {
                        try {
                            listener.onBuffering(percent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Timber.w(e.getMessage());
                        }
                    }
                }
            }
        });
    }
}
