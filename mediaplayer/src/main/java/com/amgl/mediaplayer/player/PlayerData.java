package com.amgl.mediaplayer.player;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 阿木 on 2017/5/9.
 */

public class PlayerData implements Parcelable {
    boolean needRestore = false;
    int position = 0;
    String url;
    PlayerState playerState = PlayerState.IDLE;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.needRestore ? (byte) 1 : (byte) 0);
        dest.writeInt(this.position);
        dest.writeString(this.url);
        dest.writeInt(this.playerState == null ? -1 : this.playerState.ordinal());
    }

    public PlayerData() {
    }

    protected PlayerData(Parcel in) {
        this.needRestore = in.readByte() != 0;
        this.position = in.readInt();
        this.url = in.readString();
        int tmpPlayerState = in.readInt();
        this.playerState = tmpPlayerState == -1 ? null : PlayerState.values()[tmpPlayerState];
    }

    public static final Parcelable.Creator<PlayerData> CREATOR = new Parcelable.Creator<PlayerData>() {
        @Override
        public PlayerData createFromParcel(Parcel source) {
            return new PlayerData(source);
        }

        @Override
        public PlayerData[] newArray(int size) {
            return new PlayerData[size];
        }
    };
}
