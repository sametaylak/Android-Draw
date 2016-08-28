package com.sametaylak.drawinggame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class DrawView extends View {

    private final static String TAG = "DrawView";
    private static final float  DELAY = 4;

    private float       mX, mY;
    private Bitmap      mBitmap;
    private Canvas      mCanvas;
    private Path        mPath;
    private Paint       mBitmapPaint;
    private Paint       mPaint;
    private Handler     mHandler;
    private Socket      mSocket; {
        try {
            mSocket = IO.socket("http://192.168.1.26:3000");
        } catch (URISyntaxException ignored) {}
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHandler = new Handler(context.getMainLooper());


        mSocket.on("redraw", onReDraw);
        mSocket.connect();

        mPaint = new Paint();
        mPaint.setAntiAlias(true); // smoother edges
        mPaint.setDither(true); // sensivity for multi devices
        mPaint.setColor(0xFF000000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void modeChanged(PorterDuffXfermode mode) {
        mPaint.setXfermode(mode);
    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= DELAY || dy >= DELAY) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    private void touch_up() {
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mPaint);
        mPath.reset();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        mSocket.emit("draw", encoded);
    }

    private void runOnUiThread(Runnable r) {
        mHandler.post(r);
    }

    private Emitter.Listener onReDraw = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    byte[] decodedBytes = Base64.decode((String) args[0], Base64.DEFAULT);
                    mBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    invalidate();
                }
            });
        }
    };

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint); // contains all paths
        canvas.drawPath(mPath, mPaint); // draw new path
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // first touch
                touch_start(x, y);
                invalidate(); // some kind of re-render
                break;
            case MotionEvent.ACTION_MOVE: // move touch
                touch_move(x, y);
                invalidate(); // some kind of re-render
                break;
            case MotionEvent.ACTION_UP: // up touch
                touch_up();
                invalidate(); // some kind of re-render
                break;
        }
        return true;
    }
}
