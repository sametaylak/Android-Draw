package com.sametaylak.drawinggame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomActivity extends AppCompatActivity {

    private final static String TAG = "RoomView";

    private GameManager     gManager;

    @BindView(R.id.users) TextView mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        ButterKnife.bind(this);
        gManager = new GameManager(this);
        initListeners();
    }

    private void initListeners() {
        gManager.onJoinedRoom = new Emitter.Listener() {
            @Override
            public void call(Object... args) {

            }
        };
        gManager.setListener();
    }
}
