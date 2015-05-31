package com.example.masayuki.test3;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;


public class CameraActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ステータスバーを消す
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);



        // 戻り値の指定
        setResult(Activity.RESULT_CANCELED);

        // インテントからパラメータ取得
        String text = "";
        Bundle extras = getIntent().getExtras();
        if( extras != null ){
            text = extras.getString("text");
            Log.d("CameraActivity", text);
        }
        String[] temp = text.split(",");
        String folderPath = temp[0];
        int dateIndex = Integer.parseInt(temp[1]);
        int photoIndex = Integer.parseInt(temp[2]);






        setContentView(R.layout.activity_camera);

        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(R.id.frameLayout);



        // 雑誌部分の選択
        ImageView iv = new ImageView(this);
        /*
        String[] spliterStr = folderPath.split("/");
        if( spliterStr.length != 0 ){
            String str = spliterStr[spliterStr.length-1];

            if( photoIndex == 0 ){
                if( str.equals("明治村") ){
                    iv.setImageResource(R.drawable.meizimura_1);
                }else{
                    iv.setImageResource(R.drawable.arti_sample_top);
                }
            }else{
                iv.setImageResource(R.drawable.arti_sample);
            }
        }else{
            iv.setImageResource(R.drawable.arti_sample);
        }
        */
        Bitmap bmp =  ((BitmapDrawable)iv.getDrawable()).getBitmap();



        // カメラ用ビュー
        SurfaceView sv = new CameraView(this, folderPath, dateIndex, photoIndex, bmp);

        frameLayout.addView(sv);
        frameLayout.addView(iv);

        setContentView(frameLayout);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        //super.onPrepareOptionsMenu(menu);
        return true;
    }

}