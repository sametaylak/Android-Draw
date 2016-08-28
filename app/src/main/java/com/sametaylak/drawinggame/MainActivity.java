package com.sametaylak.drawinggame;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainView";

    @BindView(R.id.pencilFAB) FloatingActionButton fPencil;
    @BindView(R.id.rubberFAB) FloatingActionButton fRubber;
    @BindView(R.id.menuFAB) FloatingActionsMenu fMenu;
    @BindView(R.id.drawView) DrawView dView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initListeners();
    }

    private void initListeners() {
        fPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dView.modeChanged(null);
                fMenu.collapse();
            }
        });
        fRubber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dView.modeChanged(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                fMenu.collapse();
            }
        });
    }
}
