package com.example.adrey.floatingiconmusic;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M &&
                !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else
            initializeView();
    }

    private void initializeView() {
        findViewById(R.id.notify_me).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(MainActivity.this, FloatingViewService.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        if(requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if(Settings.canDrawOverlays(this))
                initializeView();
            else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
                // NOTE: This is a workaround to fix the bug in Android O where the
                // Settings.canDrawOverlays() will always return 'false'
                if(canDrawOverlays(this))
                    initializeView();
            }
        }

//        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
//            if (requestCode == RESULT_OK)
//                initializeView();
//            else {
//                Toast.makeText(this, "Draw over other app permission no available. Closing the application",
//                        Toast.LENGTH_SHORT).show();
//
//                finish();
//            }
//        } else
//            super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Workaround for Android O
     */
    public static boolean canDrawOverlays(Context context) {
        try {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            return windowManager != null;

//            if (windowManager == null)
//                return false;
//
//            final View viewToAdd = new View(context);
//            WindowManager.LayoutParams params =
//                    new WindowManager.LayoutParams(
//                            0,
//                            0,
//                            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ?
//                                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
//                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                            PixelFormat.TRANSPARENT);
//            viewToAdd.setLayoutParams(params);
//            windowManager.addView(viewToAdd, params);
//            windowManager.removeView(viewToAdd);
//            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
