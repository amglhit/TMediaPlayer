package com.amgl.mediaplayer.player;

import android.text.TextUtils;

import timber.log.Timber;

/**
 * Created by 阿木 on 2017/5/9.
 */

public class PlayerHelper {
    /**
     * 保存播放状态
     *
     * @return
     */
    public static PlayerData savePlayerState(IPlayer player, PlayerData previousPlayerData) {
        if (player == null) {
            return null;
        }
        PlayerData playerData = new PlayerData();

        final PlayerState state = player.getPlayerState();
        playerData.playerState = state;

        if (state == PlayerState.RELEASED || state == PlayerState.IDLE) {
            return playerData;
        }

        playerData.url = player.getUrl();

        if (state == PlayerState.STARTED) {
            player.pause();
            playerData.position = player.getCurrentPosition();
            playerData.needRestore = true;
        } else if (player.isCanPlayback()) {
            playerData.position = player.getCurrentPosition();
            playerData.needRestore = true;
        } else if (state == PlayerState.PREPARING) {
            if (previousPlayerData != null && previousPlayerData.needRestore) {
                playerData.position = previousPlayerData.position;
                playerData.url = previousPlayerData.url;
                playerData.needRestore = true;
            }
        } else {

        }
        Timber.d("store player state on hide, state: %s, position: %s; saved: %s", playerData.playerState, playerData.position, playerData.needRestore);
        return playerData;
    }

    /**
     * 恢复播放器状态
     *
     * @param playerData
     */
    public static void restorePlayerState(PlayerData playerData, IPlayer player) {
        if (player == null || playerData == null)
            return;

        int lastPosition = playerData.position;
        final PlayerState lastState = playerData.playerState;
        String lastUrl = playerData.url;

        final PlayerState currentState = player.getPlayerState();

        Timber.d("restore player state: preStat: %s; currentStat:%s", lastState, currentState);

        if (lastState == PlayerState.PREPARING || lastState == PlayerState.STARTED || lastState == PlayerState.PAUSED) {
            if (currentState == PlayerState.INITIALIZED) {
                player.prepare(lastPosition);
                Timber.d("prepare");
            } else if (player.isCanPlayback()) {
                Timber.d("resume and start");
                player.resume(true);
            } else if (currentState == PlayerState.STOPPED) {
                player.prepare(lastPosition);
            } else if (currentState == PlayerState.RELEASED) {
                player.reset();
                if (!TextUtils.isEmpty(lastUrl)) {
                    startPlayer(player, lastUrl, lastPosition);
                }
            } else if (currentState == PlayerState.IDLE) {
                if (!TextUtils.isEmpty(lastUrl)) {
                    startPlayer(player, lastUrl, lastPosition);
                }
            }
        } else if (lastState == PlayerState.ERROR) {
            restartPlayer(player, lastUrl);
        }
    }

    public static void startPlayer(IPlayer player, String lastUrl, int startPosition) {
        Timber.d("start, url: %s; position: %s;", lastUrl, startPosition);
        player.setDataSource(lastUrl);
        player.prepare(startPosition);
    }

    public static void restartPlayer(IPlayer player, String lastUrl) {
        int startPosition = player.getLastPosition();
        player.reset();
        startPlayer(player, lastUrl, startPosition);
    }

    /**
     * 调用时需要判断是否在前台
     *
     * @param player
     * @param startPosition
     * @param preState
     */
    public static void onPrepared(IPlayer player, int startPosition, PlayerState preState) {
        if (player == null)
            return;

        if (preState == null) {
            preState = PlayerState.IDLE;
        }

//        if (!isVisible)
//            return;

        Timber.d("on prepared, preStat:%s, startPos:%s", preState, startPosition);

        if (preState == PlayerState.PAUSED) {
            if (startPosition > 0) {
                player.seekTo(startPosition, false);
            } else {
                player.start();
            }
        } else if (preState == PlayerState.STARTED) {
            if (startPosition > 0) {
                player.seekTo(startPosition, true);
            } else {
                player.start();
            }
        } else if (preState == PlayerState.IDLE || preState == PlayerState.PREPARED || preState == PlayerState.PREPARING) {
            //第一次播放时（IDLE）或者退出时状态为prepared时。
            Timber.d("start");
            if (startPosition > 0) {
                player.seekTo(startPosition, true);
            } else {
                player.start();
            }
        } else if (preState == PlayerState.ERROR) {
            if (startPosition > 0) {
                player.seekTo(startPosition, true);
            } else {
                player.start();
            }
        } else {
            Timber.d("do nothing: %s", preState);
        }
    }

    public static void onSeekComplete(IPlayer player, boolean start) {
        if (start) {
            Timber.d("start player on seek complete");
            player.start();
        }
//        else {
//            Timber.d("pause player on seek complete");
//            player.pause();
//        }
    }
}
