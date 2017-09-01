package cn.bluemobi.dylan.vrdevelop;

import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by Administrator on 2017/9/1.
 */

public class VideoActivity extends AppCompatActivity {
    private JCVideoPlayerStandard jcVideoPlayerStandard;
    private String TAG = "VideoActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//remove title bar  即隐藏标题栏
        getSupportActionBar().hide();// 隐藏ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notification bar  即全屏
        setContentView(R.layout.activity_video);
        try {
            if(!new File(getCacheDir()+File.separator+"video.mp4").exists()) {
                InputStream in = getAssets().open("video.mp4");
                copyFile(in, new File(getCacheDir() + File.separator + "video.mp4"));
            }
        }catch (Exception e){
            Toast.makeText(this,"视频拷贝出错",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        jcVideoPlayerStandard = (JCVideoPlayerStandard) findViewById(R.id.videoplayer);
        jcVideoPlayerStandard.setUp(new File(getCacheDir()+File.separator+"video.mp4").getAbsolutePath(), JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "嫂子闭眼睛");
        jcVideoPlayerStandard.thumbImageView.setImageURI(Uri.parse("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640"));
    }

    private void copyFile(InputStream in,File dest){
        try {
            if (in!=null&&!dest.exists()) {
                dest.createNewFile();
                FileOutputStream out = new FileOutputStream(dest);
                byte[] data = new byte[2048];
                int i = 0;
                while((i=in.read(data))!=-1){
                    out.write(data,0,i);
                }
                out.close();
                in.close();
                Log.e(TAG, "copyFile: success");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }
}
