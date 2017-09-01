package cn.bluemobi.dylan.vrdevelop;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 显示VR360度全景图片的控件
     */
    private VrPanoramaView vr_pan_view;
    /**
     * 打印的TAG
     */
    private final String TAG = "VrPanoramaView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//remove title bar  即隐藏标题栏
        getSupportActionBar().hide();// 隐藏ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notification bar  即全屏
        setContentView(R.layout.activity_main);
        registerVolumeChangeReceiver();
        load360Image();
    }

    SettingsContentObserver mSettingsContentObserver;
    private void registerVolumeChangeReceiver() {
        mSettingsContentObserver = new SettingsContentObserver(this, new Handler());
        getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver);
    }

    private void unregisterVolumeChangeReceiver(){
        getApplicationContext().getContentResolver().unregisterContentObserver(mSettingsContentObserver);
    }

    public class SettingsContentObserver extends ContentObserver {
        Context context;

        public SettingsContentObserver(Context c, Handler handler) {
            super(handler);
            context = c;
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            Log.e(TAG, "onChange: " + currentVolume );
            showNext();
        }
    }
    private VrPanoramaView.Options options;

    /**
     * 加载360度全景图片
     */
    private void load360Image() {
        vr_pan_view = (VrPanoramaView) findViewById(R.id.vr_pan_view);
        /**获取assets文件夹下的图片**/
        InputStream open = null;
        try {
            open = getAssets().open("1.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(open);
        /**设置加载VR图片的相关设置**/
        options = new VrPanoramaView.Options();
        options.inputType = VrPanoramaView.Options.TYPE_MONO ;//一张平面图
//        options.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER ;//上下两张图叠加的立体图
        vr_pan_view.setFullscreenButtonEnabled(false);
        vr_pan_view.setKeepScreenOn(true);
        Button tv = new Button(getBaseContext());
        tv.setOnClickListener(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        tv.setText("NEXT");
        vr_pan_view.setDisplayMode(3);
        vr_pan_view.addView(tv, lp);
        /**设置加载VR图片监听**/
        vr_pan_view.setEventListener(new VrPanoramaEventListener() {
            /**
             * 显示模式改变回调
             * 1.默认
             * 2.全屏模式
             * 3.VR观看模式，即横屏分屏模式
             * @param newDisplayMode 模式
             */
            @Override
            public void onDisplayModeChanged(int newDisplayMode) {
                super.onDisplayModeChanged(newDisplayMode);
                Log.d(TAG, "onDisplayModeChanged()->newDisplayMode=" + newDisplayMode);
                if(newDisplayMode==1){
                    finish();
                }
            }

            /**
             * 加载VR图片失败回调
             * @param errorMessage
             */
            @Override
            public void onLoadError(String errorMessage) {
                super.onLoadError(errorMessage);
                Log.d(TAG, "onLoadError()->errorMessage=" + errorMessage);
            }

            /**
             * 加载VR图片成功回调
             */
            @Override
            public void onLoadSuccess() {
                super.onLoadSuccess();
                Log.d(TAG, "onLoadSuccess->图片加载成功");
            }

            /**
             * 点击VR图片回调
             */
            @Override
            public void onClick() {
                super.onClick();
                Log.d(TAG, "onClick()");
                showNext();
            }
        });
        /**加载VR图片**/
        vr_pan_view.loadImageFromBitmap(bitmap, options);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof Button) {
            showNext();
        }
    }

    int index = 0;
    void showNext() {
        try {
            if(index==2){
                index = 0;
                showVideo();
                return;
            }
            InputStream open;
            open = getAssets().open(++index%2+".jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(open);
            open.close();
            vr_pan_view.loadImageFromBitmap(bitmap, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showVideo(){
        startActivity(new Intent(this,VideoActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterVolumeChangeReceiver();
        /**关闭加载VR图片，释放内存**/
        vr_pan_view.pauseRendering();
        vr_pan_view.shutdown();
    }
}
