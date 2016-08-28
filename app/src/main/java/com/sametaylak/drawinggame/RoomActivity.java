package com.sametaylak.drawinggame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RoomActivity extends AppCompatActivity {

    private GameManager      gManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        gManager = new GameManager(this);
    }
}
