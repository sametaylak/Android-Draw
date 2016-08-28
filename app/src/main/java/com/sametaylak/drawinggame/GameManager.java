package com.sametaylak.drawinggame;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class GameManager {

    private Socket      mSocket; {
        try {
            mSocket = IO.socket("http://192.168.1.26:3000");
        } catch (URISyntaxException ignored) {}
    }
    private Context     mContext;

    public GameManager(Context ctx) {
        mSocket.connect();
        mContext = ctx;
    }

    public void createRoom (String roomName) {
        mSocket.emit("create room", roomName);
        Intent intent = new Intent(mContext, RoomActivity.class);
        mContext.startActivity(intent);
    }

    public void leaveRoom (String roomName) {
        mSocket.emit("leave room", roomName);
    }

    public void joinRoom (String roomName) {
        mSocket.emit("join room", roomName);
        Intent intent = new Intent(mContext, RoomActivity.class);
        mContext.startActivity(intent);
    }
}
